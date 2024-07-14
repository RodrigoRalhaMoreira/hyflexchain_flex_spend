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

const Buffer = require('buffer').Buffer;
const ZokratesService = require('../util/zokrates/ZokratesService');
const fs = require('fs').promises;
const path = require('path');

/**
 * Workload module for the benchmark round.
 */
class CreateTransactionPowWorkload extends WorkloadModuleBase {

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

        if (!this.smartContract)
        {
            // reference smart contract
            if (this.sutAdapter.hyflexchainConfig.reference_smart_contract)
            {
                const ref = this.sutContext.installedContracts.get("pow");
                this.smartContract = HyFlexChainPrivateTransaction.smartContractRef(ref);
            } else // pyggyback smart contract
            {
                const contractData = this.sutAdapter.smart_contracts_map.get("pow");
                this.smartContract = HyFlexChainPrivateTransaction.smartContractCode(contractData);
            }
        }

        const originPubKey = Buffer.from("01" + this.sutContext.encodedPublicKey, 'hex');
        const destAddress = this.getRandDestAddress();
        const val = Util.getRandomInt32();

        const inputTxs = [HyFlexChainPrivateTransaction.createInputTx(Buffer.from("some hash", "utf-8"), 0)];
        const outputTxs = [HyFlexChainPrivateTransaction.createOutputTx(destAddress, val)];
        const zkpProofData = await this.getZkpProofData();
        const tx = new HyFlexChainPrivateTransaction(HyFlexChainPrivateTransaction.TRANSFER, originPubKey, inputTxs, outputTxs, zkpProofData);
        tx.nonce = this.txIndex;
        tx.smartContract = this.smartContract;

        this.txIndex++;

        return this.sutAdapter.sendRequests(tx);
    }

    /**
     * Get a rand replica address from the array of destination addresses
     * @return {Buffer} random replica address
     */
    getRandDestAddress()
    {
        const destAddresses = this.sutContext.destAddresses;
        const i = Util.getRandomInt(0, destAddresses.length);
        return destAddresses[i];
    }

    /**
     * 
     * Private function to get ZKP proof data.
     * @return {Promise<Buffer[]>}
     */
    async getZkpProofData() {
        const [proofBytes, provingKeyBytes, verificationKeyBytes] = await Promise.all([
            fs.readFile(path.join(__dirname, "../util/zokrates/proof.json")),
            fs.readFile(path.join(__dirname, "../util/zokrates/proving.key")),
            fs.readFile(path.join(__dirname, "../util/zokrates/verification.key")),
        ]);
        const DELIMITER = Buffer.from("UNIQUE_DELIMITER_SEQUENCE");
        const DELIMITER2 = Buffer.from("UNIQUE_DELIMITER_SEQUENCE2");
        return Buffer.concat([proofBytes, DELIMITER, provingKeyBytes, DELIMITER2, verificationKeyBytes]);    
    }

    async createZkpProofData() {
        // optimization for later, create zkp data only if needed
    }
}

/**
 * Create a new instance of the workload module.
 * @return {WorkloadModuleInterface}
 */
function createWorkloadModule() {
    return new CreateTransactionPowWorkload();
}

module.exports.createWorkloadModule = createWorkloadModule;
