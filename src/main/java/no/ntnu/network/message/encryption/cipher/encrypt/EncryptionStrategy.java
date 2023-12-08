package no.ntnu.network.message.encryption.cipher.encrypt;

import no.ntnu.exception.EncryptionException;

/**
 * An interface for encryption algorithms.
 */
public interface EncryptionStrategy {
    /**
     * Encrypts bytes.
     *
     * @param data the bytes to encrypt
     * @return the encrypted bytes
     */
    byte[] encrypt(byte[] data) throws EncryptionException;
}
