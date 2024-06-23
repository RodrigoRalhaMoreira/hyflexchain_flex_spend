package pt.unl.fct.di.hyflexchain.planes.zero_knowledge_proofs;
import org.json.JSONObject;

/**
 * An interface to generate and validate zero-knowledge proofs.
 */


public interface ZeroKnowledgeProofsInterface {
    
    public static ZeroKnowledgeProofsInterface getInstance()
	{
		return ZeroKnowledgeProofsInterface.getInstance();
	}

    /**
     * Verify a zero-knowledge proof.
     * @param proof
     * @return The generated proof.
     */
    public String verifyProof(JSONObject proof);


}
