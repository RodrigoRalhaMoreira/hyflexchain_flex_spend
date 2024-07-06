package pt.unl.fct.di.hyflexchain.planes.zero_knowledge_proofs.zokrates;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;

public class ZokratesService {

    public ZokratesService() {
        startZokratesService();
    }

    public void stopZokratesService() {
        try {
            // Stop the Docker container
            String[] stopCommand = {"docker", "stop", "zokrates_container"};
            executeDockerCommand(stopCommand);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startZokratesService() {
        try {
            if (dockerContainerExists("zokrates_container")) {
                // If the Docker container exists, start it
                String[] startExistingCommand = {"docker", "start", "zokrates_container"};
                executeDockerCommand(startExistingCommand);
            } else {
                // If the Docker container does not exist, create and start it
                String[] startCommand = {"docker", "run", "-d", "--name", "zokrates_container", "zokrates/zokrates", "tail", "-f", "/dev/null"};
                executeDockerCommand(startCommand);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to compile the circuit
    public void compileCircuit(String circuitFilePath) {
        try {
            // Copy the Zokrates file into the running Docker container
            String[] copyCommand = {"docker", "cp", circuitFilePath, "zokrates_container:/home/zokrates/signature_proof.zok"};
            executeDockerCommand(copyCommand);
    
            // Execute the Zokrates compile command inside the Docker container
            String[] compileCommand = {"docker", "exec", "zokrates_container", "/bin/bash", "-c", "zokrates compile -i signature_proof.zok"};
            executeDockerCommand(compileCommand);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setup() {
        try {
            // Execute the command inside the Docker container
            String[] dockerCommand = {"docker", "exec", "zokrates_container", "/bin/bash", "-c", "zokrates setup"};
            executeDockerCommand(dockerCommand);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void computeWitness(String[] inputs) {
        try {
            // Prepare the Zokrates command
            String command = "zokrates compute-witness -a " + String.join(" ", inputs);
    
            // Execute the command inside the Docker container
            String[] dockerCommand = {"docker", "exec", "zokrates_container", "/bin/bash", "-c", command};
            executeDockerCommand(dockerCommand);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    // Method to generate proof
    public void generateProof() {
        try {
            // Execute the command inside the Docker container
            String[] dockerCommand = {"docker", "exec", "zokrates_container", "/bin/bash", "-c", "zokrates generate-proof"};
            executeDockerCommand(dockerCommand);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void verifyProof(JSONObject proof, JSONObject provingKey) {
        // check the type of the provingKey 
        if (proof == null || provingKey == null) {
            throw new IllegalArgumentException("Proof and provingKey cannot be null.");
        }
    
        File proofFile = new File("proof.json");
        File provingKeyFile = new File("proving.key");
        try (BufferedWriter proofWriter = new BufferedWriter(new FileWriter(proofFile));
             BufferedWriter provingKeyWriter = new BufferedWriter(new FileWriter(provingKeyFile))) {
            // Write the proof to its file
            proofWriter.write(proof.toString());
            // Write the proving key to its file
            provingKeyWriter.write(provingKey.toString());
    
            // Copy both files to the Docker container
            String[] copyProofCommand = {"docker", "cp", "proof.json", "zokrates_container:/home/zokrates/"};
            String[] copyProvingKeyCommand = {"docker", "cp", "proving.key", "zokrates_container:/home/zokrates/"};
            executeDockerCommand(copyProofCommand);
            executeDockerCommand(copyProvingKeyCommand);
    
            // Execute the verify command inside the Docker container
            // Assuming the verification process requires both the proof and the proving key
            String verifyCmd = "cd /home/zokrates && zokrates verify -p proof.json -v proving.key";
            String[] verifyCommand = {"docker", "exec", "zokrates_container", "/bin/bash", "-c", verifyCmd};
            executeDockerCommand(verifyCommand);
    
            // Delete the files from the Docker container
            String[] deleteProofCommand = {"docker", "exec", "zokrates_container", "/bin/bash", "-c", "rm /home/zokrates/proof.json"};
            String[] deleteProvingKeyCommand = {"docker", "exec", "zokrates_container", "/bin/bash", "-c", "rm /home/zokrates/proving.key"};
            executeDockerCommand(deleteProofCommand);
            executeDockerCommand(deleteProvingKeyCommand);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Delete the files from the host machine
            proofFile.delete();
            provingKeyFile.delete();
        }
    }

    // helper functions

    private void executeDockerCommand(String[] command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command).start();
    
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    
        int exitCode = process.waitFor();
        System.out.println("Docker command execution " + (exitCode == 0 ? "successful" : "failed") + ". (Command: " + String.join(" ", command) + ")");
    }

    private boolean dockerContainerExists(String containerName) throws IOException, InterruptedException {
        String[] checkCommand = {"docker", "ps", "-a", "-q", "--filter", "name=" + containerName};
        Process checkProcess = new ProcessBuilder(checkCommand).start();
        BufferedReader checkReader = new BufferedReader(new InputStreamReader(checkProcess.getInputStream()));
        return checkReader.readLine() != null;
    }
}
