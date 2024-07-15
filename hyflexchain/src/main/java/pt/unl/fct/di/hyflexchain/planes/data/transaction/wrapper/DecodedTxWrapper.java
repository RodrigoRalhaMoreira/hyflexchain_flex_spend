package pt.unl.fct.di.hyflexchain.planes.data.transaction.wrapper;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.unl.fct.di.hyflexchain.planes.application.ti.InvalidTransactionException;
import pt.unl.fct.di.hyflexchain.planes.data.transaction.HyFlexChainTransaction;
import pt.unl.fct.di.hyflexchain.planes.data.transaction.SerializedTx;

final class DecodedTxWrapper implements TxWrapper {

    private final HyFlexChainTransaction tx;

    private SerializedTx serializedTx;

    protected static final Logger LOGGER = LoggerFactory.getLogger(DecodedTxWrapper.class);

    /**
     * @param tx
     */
    DecodedTxWrapper(HyFlexChainTransaction tx) {
        this.tx = tx;
    }

    @Override
    public HyFlexChainTransaction tx() throws InvalidTransactionException {
        LOGGER.error("ERROR7");
        return tx;
    }

    @Override
    public SerializedTx serializedTx() throws InvalidTransactionException {
        LOGGER.error("ERROR8");
        if (serializedTx != null)
            return serializedTx;

        try {
            return this.serializedTx = SerializedTx.from(tx);
        } catch (IOException e) {
            var ite = new InvalidTransactionException(e.getMessage(), e);
            ite.printStackTrace();
            throw ite;
        }
    }
    
}
