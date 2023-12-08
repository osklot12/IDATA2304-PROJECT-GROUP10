package no.ntnu.network.message.encryption.cipher.encrypt;

import no.ntnu.exception.EncryptionException;

/**
 * An encryption strategy that does not perform any encryption.
 */
public class PlainTextEncryptor implements EncryptionStrategy {
    @Override
    public byte[] encrypt(byte[] data) throws EncryptionException {
        return data;
    }
}
