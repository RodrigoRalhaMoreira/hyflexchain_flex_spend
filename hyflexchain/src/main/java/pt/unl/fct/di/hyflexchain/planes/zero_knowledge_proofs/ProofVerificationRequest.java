package pt.unl.fct.di.hyflexchain.planes.zero_knowledge_proofs;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProofVerificationRequest {
    private JSONObject proof;
    private JSONObject provingKey;
    private JSONArray publicInputs;
    // Add other fields as needed

    // Constructor, getters, and setters
    public ProofVerificationRequest(JSONObject proof, JSONObject provingKey, JSONArray publicInputs) {
        this.proof = proof;
        this.provingKey = provingKey;
        this.publicInputs = publicInputs;
    }

    public JSONObject getProof() {
        return proof;
    }

    public void setProof(JSONObject proof) {
        this.proof = proof;
    }

    public JSONObject getProvingKey() {
        return provingKey;
    }

    public void setProvingKey(JSONObject provingKey) {
        this.provingKey = provingKey;
    }

    public JSONArray getPublicInputs() {
        return publicInputs;
    }

    public void setPublicInputs(JSONArray publicInputs) {
        this.publicInputs = publicInputs;
    }

    // Add other getters and setters as needed
}