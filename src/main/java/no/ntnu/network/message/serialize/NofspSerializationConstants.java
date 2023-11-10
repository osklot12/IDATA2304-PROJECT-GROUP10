package no.ntnu.network.message.serialize;

import no.ntnu.network.message.serialize.tool.TlvFrame;

/**
 * A class holding constants for serialization using the technique described by NOFSP.
 */
public class NofspSerializationConstants {
    // version of protocol
    public static final String VERSION = "1.0";

    // a tlv frame with type-field length of 2 bytes, and length-field length of 4 bytes
    public static final TlvFrame TLV_FRAME = new TlvFrame(2, 4);

    // TLV type-fields (bytes)
    public static final byte[] INTEGER_BYTES = new byte[] {0, 0};
    public static final byte[] STRING_BYTES = new byte[] {0, 1};
    public static final byte[] LIST_BYTES = new byte[] {0, 2};
    public static final byte[] MAP_BYTES = new byte[] {0, 3};
    public static final byte[] NULL_BYTES = new byte[] {0, 4};
    public static final byte[] REQUEST_BYTES = new byte[] {1, 0};

    // request commands (UTF8 String)
    public static final String REGISTER_CONTROL_PANEL_COMMAND = "REGCP";
}
