package no.ntnu.network.message.common;

import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

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
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
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
