package no.ntnu.network.message.common;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * A serializable String.
 */
public class ByteSerializableString implements ByteSerializable {
    private final String string;

    /**
     * Creates a new ByteSerializableString.
     *
     * @param string the string
     */
    public ByteSerializableString(String string) {
        this.string = string;
    }

    /**
     * Returns the string.
     *
     * @return the string
     */
    public String getString() {
        return string;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ByteSerializableString s)) {
            return false;
        }

        return s.string.equals(string);
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + string.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return string;
    }
}
