package no.ntnu.network.message.common;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * A serializable double.
 */
public class ByteSerializableDouble implements ByteSerializable {
    private final double theDouble;

    /**
     * Creates a new ByteSerializableDouble.
     *
     * @param theDouble the double value
     */
    public ByteSerializableDouble(double theDouble) {
        this.theDouble = theDouble;
    }

    /**
     * Returns the double.
     *
     * @return the double
     */
    public double getDouble() {
        return theDouble;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return new byte[0];
    }
}
