package no.ntnu.network.message.encryption.cipher.decrypt;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.message.encryption.cipher.StandardCipher;

import javax.crypto.Cipher;
import java.security.PrivateKey;

/**
 * A decryption strategy using the RSA algorithm.
 */
public class RSADecryption extends StandardCipher implements DecryptionStrategy {
    /**
     * Creates a new RSADecryption.
     *
     * @param key the private key to use for encryption
     */
    public RSADecryption(PrivateKey key) {
        super("RSA", Cipher.DECRYPT_MODE, key);
    }

    @Override
    public byte[] decrypt(byte[] data) throws EncryptionException {
        return transform(data);
    }
}
