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
	ZKSNARKS_GROTH_16 ((byte) 1);


	public static final Serializer SERIALIZER = new Serializer();

	public final byte id;

	/**
	 * @param id
	 */
	private ZeroKnowledgeProofType(byte id) {
		this.id = id;
	}

	public static Optional<ZeroKnowledgeProofType> decode(byte id)
    {
        return Stream.of(values())
            .filter((alg) -> alg.id == id)
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
            out.writeByte(t.id);
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
