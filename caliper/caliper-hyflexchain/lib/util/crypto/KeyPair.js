'use strict';

const {KeyObject} = require("node:crypto");

/**
 * A key pair of public/private keys
 */
class KeyPair
{
	/**
	 * 
	 * @param {KeyObject} pubKey 
	 * @param {KeyObject} privKey 
	 */
	constructor(pubKey, privKey)
	{
		
		this.pubKey = pubKey;
		this.privKey = privKey;
	}

	/**
	 * 
	 * @returns public key
	 */
	getPublicKey()
	{
		return this.pubKey;
	}

	/**
	 * 
	 * @returns private key
	 */
	getPrivateKey()
	{
		return this.privKey;
	}

	/**
     * 
     * @returns {string} public key in PEM format
     */
    getPublicKeyString() {
        return this.pubKey.export({type: 'spki', format: 'pem'});
    }

    /**
     * 
     * @returns {string} private key in PEM format
     */
    getPrivateKeyString() {
        return this.privKey.export({type: 'pkcs8', format: 'pem'});
    }
}

module.exports = KeyPair;