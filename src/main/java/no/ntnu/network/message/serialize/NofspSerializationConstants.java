package no.ntnu.network.message.serialize;

import no.ntnu.network.message.serialize.tool.TlvFrame;

/**
 * A class holding constants for serialization using the technique described by NOFSP.
 */
public class NofspSerializationConstants {
    // setting a tlv frame with type-field length of 2 bytes, and length-field length of 4 bytes
    public static final TlvFrame tlvFrame = new TlvFrame(2, 4);

    // byte constants
    public static final byte[] INTEGER_BYTES = new byte[] {0, 0};
    public static final byte[] STRING_BYTES = new byte[] {0, 1};
    public static final byte[] LIST_BYTES = new byte[] {0, 2};
}
