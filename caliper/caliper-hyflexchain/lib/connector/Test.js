const axios = require('axios');
const https = require('https');
const fs = require('fs');

// Replace these paths and values with your actual configuration
const hyflexchainConfig = {
    truststore_ca: '../../crypto/hyflexchain_root_cert.pem',
    connection_timeout: 800000 // Example timeout
};

const httpsAgent = new https.Agent({
    rejectUnauthorized: false, // NOTE: this will disable client verification
    ca: fs.readFileSync(hyflexchainConfig.truststore_ca)
});

const httpClient = axios.create({
    baseURL: 'https://0.0.0.0:18000/api/rest', // Replace with your actual URL
    timeout: hyflexchainConfig.connection_timeout,
    httpsAgent: httpsAgent
});

// Define the request data and headers as needed
const requestData = {
    "version": "V1_0",
    "sender": {
      "address": "ATBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABCeBstTR+yq/2u17omGVlwyeZXMlhKB433T7oKrxuRC8NSxonCtRByzXCYNq3loDh6NzmgU8mlBsNBWiuHz2+mI="
    },
    "signatureType": "SHA256withECDSA",
    "signature": "MEUCIQD2t5pOnrh585+zBAx7EcB1Th38UK4Iy/exwlo+E7j02gIgZ7DSmevnW62yr5JXlqcPaHzopZouHF58PQalGnm9Sw4=",
    "nonce": 60,
    "transactionType": "TRANSFER",
    "smartContract": {
      "id": {
        "address": ""
      },
      "code": "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"
    },
    "inputTxs": [
      {
        "txId": "c29tZSBoYXNo",
        "outputIndex": 0
      }
    ],
    "outputTxs": [
      {
        "recipient": {
          "address": "ATBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABKPiYkftMJWuf/dHMdqSXdGNIoeLzcX3SjJSez2an2L3VAbcNsfMh9yhmfq7kwyyqZEYAIqZmhewLgTi9I8VKko="
        },
        "value": 1197202128
      }
    ],
    "data": "AA=="
};
const headers = {
    "Content-Type": "application/json"
};

// Make the POST request
httpClient.post('/hyflexchain/ti/transaction-json', requestData, { headers })
  .then(response => {
    // Handle successful response
    console.log(response.data);
  })
  .catch(error => {
    if (error.response) {
      // The request was made and the server responded with a status code
      // that falls out of the range of 2xx, like a 500 Internal Server Error
      console.error("Error status:", error.response.status);
      console.error("Error status text:", error.response.statusText);
      // Optionally, log more details about the error
      console.error("Error headers:", error.response.headers);
    } else if (error.request) {
      // The request was made but no response was received
      console.error("The request was made but no response was received", error.request);
    } else {
      // Something happened in setting up the request that triggered an Error
      console.error("Error", error.message);
    }
  });