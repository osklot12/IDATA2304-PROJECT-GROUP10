package no.ntnu.network.message.serialize.tool.tlv;

/**
 * Creates a new TlvFrame.
 *
 * @param typeFieldLength the length of the type field
 * @param lengthFieldLength the length of the length field
 */
public record TlvFrame(int typeFieldLength, int lengthFieldLength) {
}
