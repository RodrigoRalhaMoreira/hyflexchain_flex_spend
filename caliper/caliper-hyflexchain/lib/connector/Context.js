'use strict';

const { CaliperUtils } = require('@hyperledger/caliper-core');


const CryptoUtils = require("../util/crypto/Crypto");
const KeyPair = require("../util/crypto/KeyPair");
const WorkerArgs = require("./WorkerArgs");


const Logger = CaliperUtils.getLogger('Context');

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
        this.encodedPrivateKey = encodedWorkerKeyPair[1];
        Logger.error("Encoded public key: " + this.encodedPublicKey);
        Logger.error("Encoded private key: " + this.encodedPrivateKey);
        this.keyPair = crypto.decodeKeyPair(encodedWorkerKeyPair);

        const {x,y} = crypto.extractPublicKeyCoordinates(this.encodedPublicKey);
        const xDecimal = crypto.hexToDecimal(x);
        const yDecimal = crypto.hexToDecimal(y);
        const publicKeyParts = [0,0,xDecimal,yDecimal];
        const message = publicKeyParts.join("");
        const hashInstance = crypto.getHashInstance();
        hashInstance.update(message);
        const hash = hashInstance.digest('hex');
        const lowerHashPart = crypto.hexToDecimal(hash.slice(0, 32));
        const upperHashPart = crypto.hexToDecimal(hash.slice(32));
        const privateKeyHex = this.keyPair.getPrivateKey().export({
            format: 'der',
            type: 'pkcs8',
            cipher: 'aes-256-cbc',
            passphrase: 'top secret',
        }).toString('hex').slice(-64);
        const privateKeyDecimal = crypto.hexToDecimal(privateKeyHex);
        Logger.error("Private key decimal: " + privateKeyDecimal);
        Logger.error("Public key x decimal: " + xDecimal);
        Logger.error("Public key y decimal: " + yDecimal);

        this.destAddresses = workerArgs.getDestAddresses()
            .map(a => Buffer.from(a.substring(2), "hex"));

        const installedContracts = new Map(Object.entries(workerArgs.getInstalledContracts()));
        // console.log(installedContracts);
        this.installedContracts = new Map();
        installedContracts.forEach((v, k) => {
            this.installedContracts.set(k, Buffer.from(v.substring(2), "hex"));
        });

        // console.log(Object.fromEntries(this.installedContracts));
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
}

module.exports = Context;