package no.ntnu.network.message.encryption.keygen;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

/**
 * A symmetric key generator using the AES algorithm.
 */
public class AESKeyGenerator implements SymmetricKeyGenerator {
    private SecretKey key;

    @Override
    public void createKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // Use 128 or 256 bits key size
        key = keyGenerator.generateKey();
    }

    @Override
    public SecretKey getKey() {
        return key;
    }
}
