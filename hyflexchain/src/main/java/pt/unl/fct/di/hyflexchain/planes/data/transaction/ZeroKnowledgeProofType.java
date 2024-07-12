package pt.unl.fct.di.hyflexchain.planes.data.transaction;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import io.netty.buffer.ByteBuf;
import pt.unl.fct.di.hyflexchain.util.BytesOps;
import pt.unl.fct.di.hyflexchain.util.serializer.ISerializer;

/**
 * The different types of transactions.
 */
public enum ZeroKnowledgeProofType implements BytesOps {
	
	/**
	 * Regular transactions: a transaction that transfers assets/tokens.
	 */
	ZKSNARKS_ZOKRATES_GROTH_16("ZKSNARKS_ZOKRATES_GROTH_16", (byte) 1);


	public static final Serializer SERIALIZER = new Serializer();

    private final String name;
    private final byte zkpId;

	/**
     * @param name
	 * @param id
	 */
	private ZeroKnowledgeProofType(String name, byte zkpId) {
        this.name = name;
		this.zkpId = zkpId;
	}

    /**
     * The name of this zk mechanism.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * The id of this zk mechanism.
     * @return the zkpId
     */
    public byte getZkpId() {
        return zkpId;
    }

    @Override
    public String toString() {
        return this.name;
    }

	public static Optional<ZeroKnowledgeProofType> decode(byte id)
    {
        return Stream.of(values())
            .filter((zkp) -> zkp.zkpId == id)
            .findAny();
    }

    public static ZeroKnowledgeProofType decodeOrThrow(byte id)
    {
        return decode(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid zero knowledge proof type identifier"));
    }

	static final class Serializer implements ISerializer<ZeroKnowledgeProofType>
    {
        @Override
        public void serialize(ZeroKnowledgeProofType t, ByteBuf out) throws IOException {
            out.writeByte(t.zkpId);
        }

        @Override
        public ZeroKnowledgeProofType deserialize(ByteBuf in) throws IOException {
            try {
                return decodeOrThrow(in.readByte());
            } catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

	@Override
	public int serializedSize() {
		return Byte.BYTES;
	}

}
