package no.ntnu.network.message.encryption.cipher.decrypt;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.message.encryption.cipher.StandardCipher;

import javax.crypto.Cipher;
import java.security.Key;

/**
 * A decryption strategy using the AES decryption strategy.
 */
public class AESDecryption extends StandardCipher implements DecryptionStrategy {
    /**
     * Creates a new AESDecryption.
     *
     * @param key the secret key to use for encryption
     */
    public AESDecryption(Key key) {
        super("AES", Cipher.DECRYPT_MODE, key);
    }

    @Override
    public byte[] decrypt(byte[] data) throws EncryptionException {
        return transform(data);
    }
}
