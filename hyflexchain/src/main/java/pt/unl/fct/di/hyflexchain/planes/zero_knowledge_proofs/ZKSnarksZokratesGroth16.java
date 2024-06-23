package pt.unl.fct.di.hyflexchain.planes.zero_knowledge_proofs;

import org.json.JSONObject;

import pt.unl.fct.di.hyflexchain.planes.zero_knowledge_proofs.zokrates.ZokratesService
;
public class ZKSnarksZokratesGroth16 implements ZeroKnowledgeProofsInterface {
    
    private final ZokratesService zokratesService;

    public ZKSnarksZokratesGroth16(ZokratesService zokratesService) {
        this.zokratesService = zokratesService;
    }

    @Override
    public String verifyProof(JSONObject proof) {
        zokratesService.verifyProof(proof);
        return null;
    }

}
