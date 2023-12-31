package no.ntnu.network.message.common;

import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

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
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitDouble(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ByteSerializableDouble b)) {
            return false;
        }

        return b.getDouble() == theDouble;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + Double.hashCode(theDouble);

        return result;
    }
}
