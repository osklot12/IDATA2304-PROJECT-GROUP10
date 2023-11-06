package no.ntnu.network.message.serialize.tool;

import no.ntnu.exception.TlvReadingException;

import java.util.Arrays;

/**
 * Reads Type-Length-Value (TLV) structures from a {@code SimpleByteBuffer}.
 */
public class TlvReader {
    private SimpleByteBuffer buffer;
    private final TlvFrame tlvFrame;

    /**
     * Creates a TlvReader.
     *
     * @param buffer buffer to read from
     * @param tlvFrame tlv frame used for reading TLVs
     */
    public TlvReader(SimpleByteBuffer buffer, TlvFrame tlvFrame) {
        this.buffer = buffer;
        this.tlvFrame = tlvFrame;
    }

    /**
     * Creates a TlvReader.
     *
     * @param bytes bytes to read
     * @param tlvFrame tlv frame used for reading TLVs
     */
    public TlvReader(byte[] bytes, TlvFrame tlvFrame) {
        this.buffer = new SimpleByteBuffer(bytes);
        this.tlvFrame = tlvFrame;
    }

    /**
     * Reads the next TLV from the buffer.
     *
     * @return next tlv
     */
    public byte[] readNextTlv() {
        byte[] result = null;

        try {
            result = constructTlv();
            removeTlvFromBuffer(result);
        } catch (TlvReadingException e) {
            result = null;
        }

        return result;
    }

    private byte[] constructTlv() throws TlvReadingException {
        byte[] result = null;

        SimpleByteBuffer tlv = new SimpleByteBuffer();

        try {
            tlv.addBytes(getTypeField(buffer.toArray(), tlvFrame));
            tlv.addBytes(getLengthField(buffer.toArray(), tlvFrame));
            tlv.addBytes(getValueField(buffer.toArray(), tlvFrame));

            result = tlv.toArray();
        } catch (TlvReadingException e) {
            throw new TlvReadingException("Cannot construct TLV: " + e.getMessage());
        }

        return result;
    }

    private void removeTlvFromBuffer(byte[] tlv) {
        buffer = new SimpleByteBuffer(Arrays.copyOfRange(buffer.toArray(), tlv.length, buffer.toArray().length));
    }

    /**
     * Returns the first type-field for a TLV in an array of bytes.
     *
     * @param bytes array to read from
     * @param frame the frame defining the TLV
     * @return the first type-field
     * @throws TlvReadingException thrown when field cannot be read
     */
    public static byte[] getTypeField(byte[] bytes, TlvFrame frame) throws TlvReadingException {
        byte[] result = null;

        try {
            result = Arrays.copyOfRange(bytes, 0, frame.typeFieldLength());
        } catch (Exception e) {
            throw new TlvReadingException("Cannot read type-field for TLV: " + e.getMessage());
        }

        return result;
    }

    /**
     * Returns the first length-field for a TLV in an array of bytes.
     *
     * @param bytes array to read from
     * @param frame the frame defining the TLV
     * @return the first length-field
     * @throws TlvReadingException thrown when field cannot be read
     */
    public static byte[] getLengthField(byte[] bytes, TlvFrame frame) throws TlvReadingException {
        byte[] result = null;
        
        try {
            result = Arrays.copyOfRange(bytes, frame.typeFieldLength(), frame.typeFieldLength() + frame.lengthFieldLength());
        } catch (Exception e) {
            throw new TlvReadingException("Cannot read length-field for TLV: " + e.getMessage());
        }

        return result;
    }

    /**
     * Returns the first value-field for a TLV in an array of bytes.
     *
     * @param bytes array to read from
     * @param frame the frame defining the TLV
     * @return the first value-field
     * @throws TlvReadingException thrown when field cannot be read
     */
    public static byte[] getValueField(byte[] bytes, TlvFrame frame) throws TlvReadingException {
        byte[] result = null;

        int valueStartIndex = frame.typeFieldLength() + frame.lengthFieldLength();
        int valueEndIndex = valueStartIndex + ByteHandler.bytesToInt(getLengthField(bytes, frame));

        try {
            result = Arrays.copyOfRange(bytes, valueStartIndex, valueEndIndex);
        } catch (Exception e) {
            throw new TlvReadingException("Cannot read value-field for TLV: " + e.getMessage());
        }

        return result;
    }
}
