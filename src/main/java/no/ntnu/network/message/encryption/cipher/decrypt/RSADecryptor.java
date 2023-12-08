package no.ntnu.network.message.encryption.cipher.decrypt;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.message.encryption.cipher.StandardCipher;

import javax.crypto.Cipher;
import java.security.PrivateKey;

/**
 * A decryption strategy using the RSA algorithm.
 */
public class RSADecryptor extends StandardCipher implements DecryptionStrategy {
    /**
     * Creates a new RSADecryptor.
     *
     * @param key the private key to use for encryption
     */
    public RSADecryptor(PrivateKey key) {
        super("RSA", Cipher.DECRYPT_MODE, key);
    }

    @Override
    public byte[] decrypt(byte[] data) throws EncryptionException {
        return transform(data);
    }
}
