'use strict';

const CryptoUtils = require("../util/crypto/Crypto");
const KeyPair = require("../util/crypto/KeyPair");
const WorkerArgs = require("./WorkerArgs");
const fs = require('fs').promises;
const path = require('path');

/**
 * A wrapper for worker context.
 */
class Context
{
    /**
     * Create a context for the worker
     * @param {WorkerArgs} workerArgs
     * @param {Number} workerIndex
     * @param {CryptoUtils} crypto 
     */
    constructor(workerArgs, workerIndex, crypto)
    {
        this.url = workerArgs.getUrl();

        const encodedWorkerKeyPair = workerArgs.getKeyPair();
        this.encodedPublicKey = encodedWorkerKeyPair[0];
        this.keyPair = crypto.decodeKeyPair(encodedWorkerKeyPair);
        
        this.destAddresses = workerArgs.getDestAddresses()
            .map(a => Buffer.from(a.substring(2), "hex"));

        const installedContracts = new Map(Object.entries(workerArgs.getInstalledContracts()));
        // console.log(installedContracts);
        this.installedContracts = new Map();
        installedContracts.forEach((v, k) => {
            this.installedContracts.set(k, Buffer.from(v.substring(2), "hex"));
        });

        this.zkpProofData = null;

        // console.log(Object.fromEntries(this.installedContracts));
    }
    
    /**
     * Initialize the context asynchronously
     */
    async initialize() {
        this.zkpProofData = await this.initializeZkpData();
    }
    // since it doesn't make sense to create a different zkp for each transaction (since we are submitting 50 or mote transactions per second), we define here the zkp data that will be used for all transactions
    async initializeZkpData() {
        const [proofBytes, provingKeyBytes, verificationKeyBytes] = await Promise.all([
            fs.readFile(path.join(__dirname, "../util/zokrates/proof.json")),
            fs.readFile(path.join(__dirname, "../util/zokrates/proving.key")),
            fs.readFile(path.join(__dirname, "../util/zokrates/verification.key")),
        ]);
        const DELIMITER = Buffer.from("UNIQUE_DELIMITER_SEQUENCE");
        const DELIMITER2 = Buffer.from("UNIQUE_DELIMITER_SEQUENCE2");
        return Buffer.concat([proofBytes, DELIMITER, provingKeyBytes, DELIMITER2, verificationKeyBytes]);    
    }

    /**
     * @returns {string} the url of the replica to connect to
     */
    getUrl()
    {
        return this.url;
    }

    /**
     * @returns {KeyPair} the key pair of this worker
     */
    getKeyPair()
    {
        return this.keyPair;
    }

    /**
     * @returns {string} the encoded public key of this worker
     */
    getEncodedPublicKey()
    {
        return this.encodedPublicKey;
    }

    /**
     * @returns {Buffer[]} the destination addresses for generated transactions
     */
    getDestAddresses()
    {
        return this.destAddresses;
    }

    getInstalledContracts()
    {
        return this.installedContracts;
    }

    getZkpProofData()
    {
        return this.zkpProofData;
    }
}

module.exports = Context;