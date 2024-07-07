/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

'use strict';

const WorkloadModuleBase = require('@hyperledger/caliper-core').WorkloadModuleBase;

const HyFlexChainPrivateTransaction = require("../connector/HyFlexChainPrivateTransaction");

const Context = require("../connector/Context");

const Util = require('../util/Util');

/**
 * Workload module for the benchmark round.
 */
class CreateTransactionWithoutScWorkload extends WorkloadModuleBase {
    /**
     * Initializes the workload module instance.
     */
    constructor() {
        super();
        this.txIndex = 0;
    }

    /**
     * Assemble TXs for the round.
     * @return {Promise<TxStatus[]>}
     */
    async submitTransaction() {
        // use the keyPair to get the public and private transactions neeeded for zkproof generation
        console.log(this.sutContext.keyPair);
        console.log("THIS IS A TEST")
        console.log(this.sutContext.keyPair.getPublicKeyString());
        console.log(this.sutContext.keyPair.getPrivateKeyString());
        console.log("END TEST")
        const originPubKey = "0x01" + this.sutContext.encodedPublicKey;
        const destAddress = this.getRandDestAddress();
        const val = Util.getRandomInt32();

        const inputTxs = [HyFlexChainPrivateTransaction.createInputTx(this.getRandDestAddress(), "some hash", 0)];
        const outputTxs = [HyFlexChainPrivateTransaction.createOutputTx(destAddress, val)];
        const zkpProofData = getZkpProofData();
        const tx = new HyFlexChainPrivateTransaction(HyFlexChainPrivateTransaction.TRANSFER, originPubKey, inputTxs, outputTxs, zkpProofData);
        tx.nonce = this.txIndex;

        this.txIndex++;

        return this.sutAdapter.sendRequests(tx);
    }

    /**
     * Get a rand replica address from the array of destination addresses
     * @return {string} random replica address
     */
    getRandDestAddress()
    {
        const destAddresses = this.sutContext.destAddresses;
        const i = Util.getRandomInt(0, destAddresses.length);
        return destAddresses[i];
    }

    /**
     * Private function to get ZKP proof data.
     * @return {Promise<Buffer[]>}
     */
    async getZkpProofData() {
        return [];
        // TODO: Implement the actual logic to get ZKP proof data
        const proofJsonData = await fs.readFile('proof.json', 'utf8');
        const proof = JSON.parse(proofJsonData);
        const provingKey = await fs.readFile('proving.key');

        // Construct zkpProofData based on the read and processed data
        // Adjust according to your actual data structure and requirements
        return [Buffer.from(provingKey), ...publicInputs.map(input => Buffer.from(input)), Buffer.from(JSON.stringify(proof))];
    }
}

/**
 * Create a new instance of the workload module.
 * @return {WorkloadModuleInterface}
 */
function createWorkloadModule() {
    return new CreateTransactionWithoutScWorkload();
}

module.exports.createWorkloadModule = createWorkloadModule;
