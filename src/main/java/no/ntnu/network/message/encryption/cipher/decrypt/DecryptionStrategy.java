package no.ntnu.network.message.encryption.cipher.decrypt;

import no.ntnu.exception.EncryptionException;

/**
 * An interface for decryption algorithms.
 */
public interface DecryptionStrategy {
    /**
     * Decrypts bytes.
     *
     * @param data the bytes to decrypt
     * @return the decrypted bytes
     */
    byte[] decrypt(byte[] data) throws EncryptionException;
}
