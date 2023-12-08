package no.ntnu.network.message.encryption.cipher.decrypt;

import no.ntnu.exception.EncryptionException;

/**
 * A decryption strategy that does not perform any decryption.
 */
public class PlainTextDecryptor implements DecryptionStrategy {
    @Override
    public byte[] decrypt(byte[] data) throws EncryptionException {
        return data;
    }
}
