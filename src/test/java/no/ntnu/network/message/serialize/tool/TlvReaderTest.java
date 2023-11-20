package no.ntnu.network.message.serialize.tool;

import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;
import org.junit.Before;

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
        tlvReader = new TlvReader(byteBuffer, NofspSerializationConstants.TLV_FRAME);
    }
}