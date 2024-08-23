package pt.unl.fct.di.hyflexchain.planes.zero_knowledge_proofs.zokrates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ZokratesService {

    private static final byte[] DELIMITER = "UNIQUE_DELIMITER_SEQUENCE".getBytes();
    private static final byte[] DELIMITER2 = "UNIQUE_DELIMITER_SEQUENCE2".getBytes();
    private String containerName = "zokrates-";


    public ZokratesService() {
    }

    public void setContainerName(String replicaId) {
        this.containerName = this.containerName + replicaId;
    }


    public boolean verifyProof(byte[] zkpData) {
        // Assuming separateAndCreateFiles function handles file creation and checks
        
        boolean verified = false;
        try {
            if (!fileExistsInContainer(this.containerName, "/home/zokrates/proof.json")) {
                separateAndCreateFiles(zkpData);
                // Copy both files to the Docker container
                String[] copyProofCommand = {"docker", "cp", "./proof.json", this.containerName+ ":/home/zokrates/"};
                String[] copyProvingKeyCommand = {"docker", "cp", "./proving.key", this.containerName+":/home/zokrates/"};
                String[] copyVerificationKeyCommand = {"docker", "cp", "./verification.key", this.containerName+":/home/zokrates/"};
                new ProcessBuilder(copyProofCommand).start();
                new ProcessBuilder(copyProvingKeyCommand).start();
                new ProcessBuilder(copyVerificationKeyCommand).start();
            }
        
            // Execute verification command in Docker container
            String[] dockerCommand = {"docker", "exec", this.containerName, "/bin/bash", "-c", "zokrates verify"};
            Process process = new ProcessBuilder(dockerCommand).start();
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("PASSED")) {
                    verified = true;
                }
            }
        } catch (IOException e) {            
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return verified;
    }

    // helper functions


    public void separateAndCreateFiles(byte[] combinedData) throws IOException {
        // Find the indexes of the two delimiters in the combinedData
        int delimiterIndex1 = findDelimiterIndex(combinedData, DELIMITER);
        int delimiterIndex2 = findDelimiterIndex(combinedData, DELIMITER2);
    
        if (delimiterIndex1 == -1 || delimiterIndex2 == -1 || delimiterIndex1 >= delimiterIndex2) {
            throw new IllegalArgumentException("Delimiters not found or in wrong order in the combined data");
        }
    
        // Split the combinedData into proofBytes, provingKeyBytes, and additionalDataBytes
        byte[] proofBytes = new byte[delimiterIndex1];
        System.arraycopy(combinedData, 0, proofBytes, 0, delimiterIndex1);
    
        byte[] provingKeyBytes = new byte[delimiterIndex2 - delimiterIndex1 - DELIMITER.length];
        System.arraycopy(combinedData, delimiterIndex1 + DELIMITER.length, provingKeyBytes, 0, provingKeyBytes.length);
    
        byte[] verificationKeyBytes = new byte[combinedData.length - delimiterIndex2 - DELIMITER2.length];
        System.arraycopy(combinedData, delimiterIndex2 + DELIMITER2.length, verificationKeyBytes, 0, verificationKeyBytes.length);
    
        // Write the separated bytes to their respective files
        Files.write(Paths.get("proof.json"), proofBytes);
        Files.write(Paths.get("proving.key"), provingKeyBytes);
        Files.write(Paths.get("verification.key"), verificationKeyBytes);
    
    }

    private int findDelimiterIndex(byte[] data, byte[] delimiter) {
        for (int i = 0; i < data.length - delimiter.length + 1; ++i) {
            boolean found = true;
            for (int j = 0; j < delimiter.length; ++j) {
                if (data[i + j] != delimiter[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i;
            } 
        }
        return -1; // Delimiter not found
    }

    public boolean fileExistsInContainer(String containerName, String filePath) throws IOException, InterruptedException {
    String checkFileCommand = String.format("docker exec %s test -f %s && echo found || echo not found", containerName, filePath);
    Process process = new ProcessBuilder("/bin/bash", "-c", checkFileCommand).start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String output = reader.readLine();
    process.waitFor();
    return "found".equals(output);
}
}