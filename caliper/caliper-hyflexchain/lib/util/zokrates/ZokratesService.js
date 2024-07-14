const { exec } = require('child_process');
const util = require('util');
const execAsync = util.promisify(exec);
const fs = require('fs');

class ZokratesService {
  static DELIMITER = Buffer.from("UNIQUE_DELIMITER_SEQUENCE");
  static DELIMITER2 = Buffer.from("UNIQUE_DELIMITER_SEQUENCE2");

  constructor() {
    this.startZokratesService();
  }

  async dockerContainerExists(containerName) {
    try {
      const { stdout } = await execAsync(`docker ps -a -q -f name=^/${containerName}$`);
      return stdout.trim().length > 0;
    } catch (error) {
      console.error(error);
      return false;
    }
  }

  async executeDockerCommand(command) {
    try {
      const { stdout, stderr } = await execAsync(command.join(' '));
      console.log(stdout);
      if (stderr) {
        console.error(stderr);
      }
    } catch (error) {
      console.error(error);
    }
  }

  async stopZokratesService() {
    try {
      const stopCommand = ["docker", "stop", "zokrates_container"];
      await this.executeDockerCommand(stopCommand);
    } catch (error) {
      console.error(error);
    }
  }

  async startZokratesService() {
    try {
      if (await this.dockerContainerExists("zokrates_container")) {
        const startExistingCommand = ["docker", "start", "zokrates_container"];
        await this.executeDockerCommand(startExistingCommand);
      } else {
        const startCommand = ["docker", "run", "-d", "--name", "zokrates_container", "zokrates/zokrates", "tail", "-f", "/dev/null"];
        await this.executeDockerCommand(startCommand);
      }
    } catch (error) {
      console.error(error);
    }
  }

  async compileCircuit(circuitFilePath) {
    try {
      const copyCommand = ["docker", "cp", circuitFilePath, "zokrates_container:/home/zokrates/signature_proof.zok"];
      await this.executeDockerCommand(copyCommand);

      const compileCommand = ["docker", "exec", "zokrates_container", "/bin/bash", "-c", "zokrates compile -i signature_proof.zok"];
      await this.executeDockerCommand(compileCommand);
    } catch (error) {
      console.error('Compile error:', error);
    }
  }

  async setup() {
    try {
      const dockerCommand = ["docker", "exec", "zokrates_container", "/bin/bash", "-c", "zokrates setup"];
      await this.executeDockerCommand(dockerCommand);
    } catch (error) {
      console.error('Setup error:', error);
    }
  }

  async computeWitness(inputs) {
    try {
      const command = `zokrates compute-witness -a ${inputs.join(' ')}`;
      const dockerCommand = ["docker", "exec", "zokrates_container", "/bin/bash", "-c", command];
      await this.executeDockerCommand(dockerCommand);
    } catch (error) {
      console.error('Compute witness error:', error);
    }
  }

  async generateProof() {
    let outputStream = Buffer.alloc(0);
    try {
      await this.executeDockerCommand(["docker", "exec", "zokrates_container", "/bin/bash", "-c", "zokrates generate-proof"]);

      const filesToCopy = [
        { from: "zokrates_container:/home/zokrates/proof.json", to: "./proof.json" },
        { from: "zokrates_container:/home/zokrates/proving.key", to: "./proving.key" },
        { from: "zokrates_container:/home/zokrates/verification.key", to: "./verification.key" }
      ];

      for (const file of filesToCopy) {
        await this.executeDockerCommand(["docker", "cp", file.from, file.to]);
      }

      const proofBytes = await fs.readFile("proof.json");
      const provingKeyBytes = await fs.readFile("proving.key");
      const verificationKeyBytes = await fs.readFile("verification.key");

      outputStream = Buffer.concat([proofBytes, DELIMITER, provingKeyBytes, DELIMITER2, verificationKeyBytes]);

      await Promise.all(filesToCopy.map(file => fs.unlink(file.to.split('/').pop())));

    } catch (error) {
      console.error('Error generating proof:', error);
    }
    console.log("Proof and proving key generated successfully, and returned as byte array");
    return outputStream;
  }

  async getZkpProofData() {
    const files = [
        { name: "proof.json", path: "./proof.json" },
        { name: "proving.key", path: "./proving.key" },
        { name: "verification.key", path: "./verification.key" }
    ];

    // Check if files exist locally, if not, copy from Docker
    for (const file of files) {
        if (!fs.existsSync(file.path)) {
            // Assuming `file.from` is the Docker path and `file.to` is the local path
            await this.executeDockerCommand(["docker", "cp", file.from, file.to]);
        }
    }

    // Read files
    const fileContents = await Promise.all(files.map(file => fs.readFile(file.path)));

    // Concatenate file contents
    const outputStream = Buffer.concat(fileContents.reduce((acc, content, index) => {
        if (index > 0) acc.push(index === 1 ? DELIMITER : DELIMITER2);
        acc.push(content);
        return acc;
    }, []));

    // Cleanup is not necessary if files are to be reused. If cleanup is needed, add it here.

    return outputStream;
}
}

module.exports = ZokratesService;