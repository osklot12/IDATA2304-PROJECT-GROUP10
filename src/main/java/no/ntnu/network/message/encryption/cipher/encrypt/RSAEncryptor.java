package no.ntnu.network.message.encryption.cipher.encrypt;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.message.encryption.cipher.StandardCipher;

import javax.crypto.Cipher;
import java.security.PublicKey;

/**
 * An encryption strategy using the RSA algorithm.
 */
public class RSAEncryptor extends StandardCipher implements EncryptionStrategy {
    /**
     * Creates a new RSAEncryptor.
     *
     * @param key the key public to use for encryption
     */
    public RSAEncryptor(PublicKey key) {
        super("RSA", Cipher.ENCRYPT_MODE, key);
    }

    @Override
    public byte[] encrypt(byte[] data) throws EncryptionException {
        return transform(data);
    }
}
