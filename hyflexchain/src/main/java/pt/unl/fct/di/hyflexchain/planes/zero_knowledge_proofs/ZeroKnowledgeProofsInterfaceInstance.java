package pt.unl.fct.di.hyflexchain.planes.zero_knowledge_proofs;

import pt.unl.fct.di.hyflexchain.planes.data.transaction.ZeroKnowledgeProofType;

public class ZeroKnowledgeProofsInterfaceInstance {

    private static ZeroKnowledgeProofsInterface instance;

    // default zkp mechanism
    public static ZeroKnowledgeProofsInterface getInstance() {
        if (instance != null)
			return instance;
		instance = new ZKSnarksZokratesGroth16();
        return instance;
    }


	public static ZeroKnowledgeProofsInterface getInstance(ZeroKnowledgeProofType zkpType) {
		if (instance != null)
			return instance;

		synchronized (ZeroKnowledgeProofsInterface.class) {
			switch (zkpType) {
				case ZKSNARKS_ZOKRATES_GROTH_16:
					instance = new ZKSnarksZokratesGroth16();
					break;
				default:
					instance = new ZKSnarksZokratesGroth16();
					break;
			}

			return instance;
		}
	}
}