package no.ntnu.network.message.serialize.tool;

import no.ntnu.network.message.serialize.NofspSerializationConstants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit testing for the TlvReader class.
 */
public class TlvReaderTest {
    SimpleByteBuffer byteBuffer;
    TlvReader tlvReader;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        byteBuffer = new SimpleByteBuffer();
        tlvReader = new TlvReader(byteBuffer, NofspSerializationConstants.tlvFrame);
    }

    /**
     * Tests that the getNextTlv() method returns the next TLV as expected.
     */
    @Test
    public void testGetNextTlv() {
        byte[] tlvs = new byte[] {0, 0, 0, 0, 0, 4, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 2, 0, 0};

        byteBuffer.addBytes(tlvs);

        byte[] firstTlv = tlvReader.readNextTlv();
        byte[] secondTlv = tlvReader.readNextTlv();

        assertArrayEquals(new byte[] {0, 0, 0, 0, 0, 4, 0, 0, 0, 0}, firstTlv);

        assertArrayEquals(new byte[] {0, 0, 0, 0, 0, 2, 0, 0}, secondTlv);
    }
}