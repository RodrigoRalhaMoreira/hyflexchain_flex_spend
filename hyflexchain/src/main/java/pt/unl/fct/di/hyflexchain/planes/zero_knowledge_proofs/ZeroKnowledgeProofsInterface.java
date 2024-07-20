package pt.unl.fct.di.hyflexchain.planes.zero_knowledge_proofs;
import pt.unl.fct.di.hyflexchain.planes.data.transaction.ZeroKnowledgeProofType;

/**
 * An interface to generate and validate zero-knowledge proofs.
 */


public interface ZeroKnowledgeProofsInterface {

    public static ZeroKnowledgeProofsInterface getInstance()
	{
		return ZeroKnowledgeProofsInterfaceInstance.getInstance();
	}

    public static ZeroKnowledgeProofsInterface getInstance(ZeroKnowledgeProofType zkpType)
	{
		return ZeroKnowledgeProofsInterfaceInstance.getInstance(zkpType);
	}

    /**
     * Verify a zero-knowledge proof.
     * @param proof
     * @return True if the proof could be validated, False otherwise.
     */
    public boolean verifyProof(byte[] proof);


}