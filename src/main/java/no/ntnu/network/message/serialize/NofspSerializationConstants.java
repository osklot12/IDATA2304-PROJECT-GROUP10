package no.ntnu.network.message.serialize;

import no.ntnu.network.message.serialize.tool.tlv.TlvFrame;

/**
 * A class holding shared constants for serialization using the technique described by NOFSP.
 */
public class NofspSerializationConstants {
    /**
     * Does not allow creating instances of this class.
     */
    private NofspSerializationConstants() {}

    // version of protocol
    public static final String VERSION = "1.0";

    // a tlv frame with type-field length of 2 bytes, and length-field length of 4 bytes
    public static final TlvFrame TLV_FRAME = new TlvFrame(2, 4);

    // TLV type-fields (bytes)
    public static final byte[] INTEGER_BYTES = new byte[] {0, 0};
    public static final byte[] STRING_BYTES = new byte[] {0, 1};
    public static final byte[] NULL_BYTES = new byte[] {0, 4};
    public static final byte[] SET_BYTES = new byte[] {0, 10};
    public static final byte[] LIST_BYTES = new byte[] {0, 11};
    public static final byte[] MAP_BYTES = new byte[] {0, 12};
    public static final byte[] CONTAINER_TLV = new byte[] {0, 13};
    public static final byte[] REQUEST_BYTES = new byte[] {1, 0};
    public static final byte[] RESPONSE_BYTES = new byte[] {1, 1};

    // request commands (UTF-8 String)
    public static final String HEART_BEAT = "HBEAT";
    public static final String REGISTER_FIELD_NODE_COMMAND = "REGFN";
    public static final String REGISTER_CONTROL_PANEL_COMMAND = "REGCP";
    public static final String FIELD_NODE_POOL_PULL_COMMAND = "PPULL";
    public static final String SUBSCRIBE_TO_FIELD_NODE_COMMAND = "FNSUB";
    public static final String ADL_UPDATE_COMMAND = "ADLUPD";
    public static final String ACTUATOR_NOTIFICATION_COMMAND = "ACTNOT";
    public static final String DISCONNECT_CLIENT_COMMAND = "DISC";

    // status codes - successful requests
    public static final int HEART_BEAT_CODE = 0;
    public static final int NODE_REGISTRATION_CONFIRMED_CODE = 1;
    public static final int SUBSCRIBED_TO_FIELD_NODE_CODE = 2;
    public static final int FIELD_NODE_POOL_CODE = 10;
    public static final int ADL_UPDATED_RESPONSE = 20;

    // status codes - error
    public static final int AUTHENTICATION_FAILED_CODE = 100;
    public static final int NODE_REGISTRATION_DECLINED_CODE = 101;
    public static final int SUBSCRIPTION_FAILED_CODE = 102;
    public static final int ADL_UPDATE_REJECTED = 103;
}
