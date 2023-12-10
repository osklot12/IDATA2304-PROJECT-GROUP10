package no.ntnu.network.message.serialize.tool.tlv;

import no.ntnu.network.message.encryption.cipher.decrypt.DecryptionStrategy;
import no.ntnu.network.message.encryption.cipher.encrypt.EncryptionStrategy;
import no.ntnu.network.message.serialize.tool.ByteHandler;

import java.util.Arrays;

/**
 * A Type-Length-Value structure for storing data in bytes.
 *
 * @param typeField the type of data stored
 * @param lengthField the length of the value field
 * @param valueField the data itself
 */
public record Tlv(byte[] typeField, byte[] lengthField, byte[] valueField) {
    /**
     * Returns the corresponding TlvFrame for the Tlv.
     *
     * @return the corresponding TlvFrame
     */
    public TlvFrame getFrame() {
        return new TlvFrame(typeField.length, lengthField.length);
    }

    /**
     * Returns the TLV as an array of bytes.
     *
     * @return tlv as array of bytes
     */
    public byte[] toBytes() {
        return ByteHandler.combineBytes(typeField, lengthField, valueField);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Tlv t)) {
            return false;
        }

        return Arrays.equals(typeField, t.typeField) && Arrays.equals(lengthField, t.lengthField) && Arrays.equals(valueField, t.valueField);
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + Arrays.hashCode(typeField);
        result = result * 31 + Arrays.hashCode(lengthField);
        result = result * 31 + Arrays.hashCode(valueField);

        return result;
    }

    @Override
    public String toString() {
        return ByteHandler.bytesToString(ByteHandler.combineBytes(typeField, lengthField, valueField));
    }
}
