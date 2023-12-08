package no.ntnu.network.message.encryption.cipher.encrypt;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.message.encryption.cipher.StandardCipher;

import javax.crypto.Cipher;
import java.security.Key;

/**
 * An encryption strategy using the AES algorithm.
 */
public class AESEncryptor extends StandardCipher implements EncryptionStrategy {
    /**
     * Creates a new AESEncryptor.
     *
     * @param key the secret key to use for encryption
     */
    public AESEncryptor(Key key) {
        super("AES", Cipher.ENCRYPT_MODE, key);
    }

    @Override
    public byte[] encrypt(byte[] data) throws EncryptionException {
        return transform(data);
    }
}
