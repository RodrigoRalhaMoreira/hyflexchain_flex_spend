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

    public void verifyProof(JSONObject proof) {
        if (proof == null) {
            throw new IllegalArgumentException("Proof cannot be null.");
        }

        // create unique paths for each proof afterwards, using some other attribute or hashing the proof
        
        File proofFile = new File("proof.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(proofFile))) {
            // Write the proof to the file
            writer.write(proof.toString());

            // Copy the proof file to the Docker container
            String[] copyCommand = {"docker", "cp", "proof.json", "zokrates_container:/home/zokrates/"};
            executeDockerCommand(copyCommand);

            // Execute the verify command inside the Docker container
            String[] verifyCommand = {"docker", "exec", "zokrates_container", "/bin/bash", "-c", "cd /home/zokrates && zokrates verify"};
            executeDockerCommand(verifyCommand);

            // Delete the proof file from the Docker container
            String[] deleteCommand = {"docker", "exec", "zokrates_container", "/bin/bash", "-c", "rm /home/zokrates/proof.json"};
            executeDockerCommand(deleteCommand);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Delete the proof file from the host machine
            proofFile.delete();
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
