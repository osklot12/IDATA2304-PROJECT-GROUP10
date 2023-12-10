package no.ntnu.network.message.encryption;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.message.encryption.cipher.decrypt.DecryptionStrategy;
import no.ntnu.network.message.encryption.cipher.encrypt.EncryptionStrategy;
import no.ntnu.network.message.serialize.tool.ByteHandler;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvFrame;

/**
 * Encrypts Type-Length-Value structures of bytes.
 * When TLVs are encrypted, only their value field are changed, making them still valid TLVs.
 * However, the length field may change due to the change of the value field.
 */
public class TlvEncryption {
    /**
     * Does not allow creating instances of the class.
     */
    private TlvEncryption() {}

    /**
     * Encrypts a Tlv.
     *
     * @param tlv the tlv to encrypt
     * @param strategy the encryption strategy
     * @return the encrypted tlv
     * @throws EncryptionException thrown if encryption fails
     */
    public static Tlv encryptTlv(Tlv tlv, EncryptionStrategy strategy) throws EncryptionException {
        if (tlv == null) {
            throw new IllegalArgumentException("Cannot encrypt tlv, because tlv is null.");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("Cannot encrypt tlv, because encryption strategy is null.");
        }

        byte[] newValueField = strategy.encrypt(tlv.valueField());
        byte[] newLengthField = generateLengthField(newValueField, tlv.getFrame());

        return new Tlv(tlv.typeField(), newLengthField, newValueField);
    }

    /**
     * Decrypts a Tlv.
     *
     * @param tlv the tlv to decrypt
     * @param strategy the decryption strategy
     * @return the decrypted tlv
     * @throws EncryptionException thrown if decryption fails
     */
    public static Tlv decryptTlv(Tlv tlv, DecryptionStrategy strategy) throws EncryptionException {
        if (tlv == null) {
            throw new IllegalArgumentException("Cannot decrypt tlv, because tlv is null.");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("Cannot decrypt tlv, because decryption strategy is null.");
        }

        byte[] newValueField = strategy.decrypt(tlv.valueField());
        byte[] newLengthField = generateLengthField(newValueField, tlv.getFrame());

        return new Tlv(tlv.typeField(), newLengthField, newValueField);
    }

    /**
     * Generates a length field for a Tlv with a specified value field and TLV frame.
     *
     * @param valueField the value field to generate length field for
     * @param tlvFrame the tlv frame deciding the length of the length field
     * @return the generated length field
     */
    private static byte[] generateLengthField(byte[] valueField, TlvFrame tlvFrame) {
        byte[] lengthAsBytes = ByteHandler.intToBytes(valueField.length);
        return ByteHandler.addLeadingPadding(lengthAsBytes, tlvFrame.lengthFieldLength());
    }
}
