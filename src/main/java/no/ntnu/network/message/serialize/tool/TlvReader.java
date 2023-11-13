package no.ntnu.network.message.serialize.tool;

import no.ntnu.exception.TlvReadingException;

import java.io.IOException;
import java.util.Arrays;

/**
 * Reads Type-Length-Value (TLV) structures from a {@code ByteSource}.
 */
public class TlvReader {
    private final ByteSource byteSource;
    private final SimpleByteBuffer buffer;
    private final TlvFrame tlvFrame;

    /**
     * Creates a new TlvReader.
     *
     * @param source source of bytes to read from
     * @param tlvFrame Tlv frame to use for parsing segments
     */
    public TlvReader(ByteSource source, TlvFrame tlvFrame) {
        this.byteSource = source;
        this.buffer = new SimpleByteBuffer();
        this.tlvFrame = tlvFrame;
    }

    /**
     * Creates a new TlvReader.
     *
     * @param bytes bytes to read from
     * @param tlvFrame Tlv frame to use for parsing segments
     */
    public TlvReader(byte[] bytes, TlvFrame tlvFrame) {
        this.byteSource = new SimpleByteBuffer(bytes);
        this.buffer = new SimpleByteBuffer();
        this.tlvFrame = tlvFrame;
    }

    /**
     * Reads the next TLV from the buffer.
     * The method will block until a complete TLV is read, the byte source is out of bytes or an exception is thrown.
     *
     * @return next TLV, null if there are no more TlVs to read
     * @throws IOException thrown if an I/O exception occurs
     */
    public byte[] readNextTlv() throws IOException {
        byte[] tlv = null;

        if (waitForBytes(tlvFrame.typeFieldLength() + tlvFrame.lengthFieldLength())) {
            byte[] lengthField = getLengthField(buffer.toArray(), tlvFrame);
            int valueLength = ByteHandler.bytesToInt(lengthField);

            if (waitForBytes(valueLength)) {
                tlv = buffer.toArray();
            }
        }
        buffer.reset();

        return tlv;
    }

    /**
     * Reads a given amount of bytes and indicates whether the given amount was successfully read.
     *
     * @param numberOfBytes amount of bytes to read
     * @return true if number of bytes were successfully read, false if end of source was met before all bytes could
     * be read
     * @throws IOException thrown if an I/O exception occurs
     */
    private boolean waitForBytes(int numberOfBytes) throws IOException {
        boolean endOfSource = false;
        int bytesRead = 0;

        while ((bytesRead < numberOfBytes) && !endOfSource) {
            int nextByte = byteSource.read();
            if (nextByte != -1) {
                buffer.addByte((byte) nextByte);
            } else {
                endOfSource = true;
            }

            bytesRead++;
        }

        return !endOfSource;
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
    public static byte[] getValueField(byte[] bytes, TlvFrame frame) {
        int valueStartIndex = frame.typeFieldLength() + frame.lengthFieldLength();
        int valueEndIndex = valueStartIndex + ByteHandler.bytesToInt(getLengthField(bytes, frame));

        return Arrays.copyOfRange(bytes, valueStartIndex, valueEndIndex);
    }
}
