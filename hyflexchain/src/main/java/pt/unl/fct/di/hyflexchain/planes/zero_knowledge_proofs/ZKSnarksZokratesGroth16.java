package pt.unl.fct.di.hyflexchain.planes.zero_knowledge_proofs;

import pt.unl.fct.di.hyflexchain.planes.zero_knowledge_proofs.zokrates.ZokratesService;
public class ZKSnarksZokratesGroth16 implements ZeroKnowledgeProofsInterface {

    private final ZokratesService zokratesService;

    public ZKSnarksZokratesGroth16() {
        this.zokratesService = new ZokratesService();
    }

    public ZKSnarksZokratesGroth16(ZokratesService zokratesService) {
        this.zokratesService = zokratesService;
    }

    public void setContainerName(String replicaId) {
        this.zokratesService.setContainerName(replicaId);
    }


    @Override
    public boolean verifyProof(byte[] proof) {
        return zokratesService.verifyProof(proof);
    }

}