'use strict';

const Util = require('../util/Util');

const Buffer = require('buffer').Buffer;

/**
 * Represents a HyFlexChain Node Transaction
 */
class HyFlexChainPrivateTransaction
{
	static TRANSFER = ["TRANSFER", Buffer.from([1])];

	/**
	 * Contract deployment transactions: a transaction that creates
	 * and installs a smart contract on the chain. After being installed
	 * it can be referenced in future transactions for execution.
	 */
	static CONTRACT_CREATE = ["CONTRACT_CREATE", Buffer.from([2])];

	/**
	 * Revoke a contract previously installed on the chain.
	 * After this transaction is approved/executed it is no longer
	 * possible to reference and execute the revoked smart contract.
	 */
	static CONTRACT_REVOKE = ["CONTRACT_REVOKE", Buffer.from([3])];

	/**
	 * An internal type used by HyFlexChain nodes to propose
	 * committees for the future.
	 */
	static COMMITTEE_ELECTION = ["COMMITTEE_ELECTION", Buffer.from([4])];

	/**
	 * An internal type used by HyFlexChain nodes to rotate
	 * the currently executing committee.
	 */
	static COMMITTEE_ROTATION = ["COMMITTEE_ROTATION", Buffer.from([5])];

	
	// ZKP transaction type

	static ZKSNARKS_ZOKRATES_GROTH_16 = "ZKSNARKS_ZOKRATES_GROTH_16";
	


	

	/**
	 * Create a new transaction
	 * @param {(string | Buffer)[]} txType
	 * @param {Buffer} origin 
	 * @param {object[]} inputTxs 
	 * @param {object[]} outputTxs 
	 * @param {Buffer []} zkpProofData
	 */
	constructor(txType, origin, inputTxs, outputTxs, zkpProofData)
	{
		this.version = "V1_0";
		this.sender = {address : origin};
		this.signatureType = undefined;
		this.signature = undefined;
		this.nonce = Util.getRandomInt(0, Number.MAX_SAFE_INTEGER);
		this.transactionType = txType;
		this.smartContract = undefined;
		this.inputTxs = inputTxs;
		this.outputTxs = outputTxs;
		this.data = Buffer.alloc(1);
		this.zkpType = HyFlexChainPrivateTransaction.ZKSNARKS_ZOKRATES_GROTH_16
		this.zkpProofData = zkpProofData;
	}

	/**
	 * Sign transaction
	 * @param {crypto.KeyObject} privKey 
	 * @param {CryptoUtils} cryptoUtils 
	 */
	sign(privKey, cryptoUtils)
	{
		// to understand if this can be done without signing because the proof should be enough
	}

	toJson() {
		return {
			version: this.version,
			sender: { address: this.sender.address.toString("base64") },
			signatureType: this.signatureType,
			signature: this.signature ? this.signature.toString("base64") : null,
			nonce: this.nonce,
			transactionType: Array.isArray(this.transactionType) ? this.transactionType.map(t => t.toString()) : this.transactionType.toString(),
			smartContract: this.smartContract ? HyFlexChainPrivateTransaction.toJsonSmartContract(this.smartContract) : null,
			inputTxs: this.inputTxs.map(v => HyFlexChainPrivateTransaction.toJsonInputTx(v)),
			outputTxs: this.outputTxs.map(v => HyFlexChainPrivateTransaction.toJsonOutputTx(v)),
			data: this.data.toString("base64"),
			zkpType: this.zkpType,
			zkpProofData: Array.isArray(this.zkpProofData) ? this.zkpProofData.map(data => data.toString("base64")) : this.zkpProofData.toString("base64")
		};
	}

	static smartContract(ref, code)
	{
		return {id : {address : ref}, code : code};
	}

	static smartContractRef(ref)
	{
		return HyFlexChainPrivateTransaction.smartContract(ref, Buffer.alloc(0));
	}

	static smartContractCode(code)
	{
		return HyFlexChainPrivateTransaction.smartContract(Buffer.alloc(0), code);
	}

	static toJsonSmartContract(contract)
	{
		return {id : {address : contract.id.address.toString("base64")}, code : contract.code.toString("base64")};
	}

	static toJsonInputTx(inputTx)
	{
		return {
			txId : inputTx.txId.toString("base64"),
			outputIndex : inputTx.outputIndex
		};
	}

	static toJsonOutputTx(outputTx)
	{
		return {recipient : {address : outputTx.recipient.address.toString("base64")}, value : outputTx.value};
	}

	static createInputTx(txHash, outputIndex)
	{
		return {
			txId : txHash,
			outputIndex : outputIndex
		};
	}

	static createOutputTx(address, value)
	{
		return {recipient : {address : address}, value : value};
	}
	
}

module.exports = HyFlexChainPrivateTransaction;