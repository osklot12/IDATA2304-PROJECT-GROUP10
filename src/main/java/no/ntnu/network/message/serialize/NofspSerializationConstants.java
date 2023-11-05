package no.ntnu.network.message.serialize;

/**
 * A class holding constants for serialization using the technique described by NOFSP.
 */
public class NofspSerializationConstants {
    // length constants
    public static final int TYPE_FIELD_LENGTH = 2;
    public static final int LENGTH_FIELD_LENGTH = 4;

    // byte constants
    public static final byte[] INTEGER_BYTES = new byte[] {0, 0};
}
