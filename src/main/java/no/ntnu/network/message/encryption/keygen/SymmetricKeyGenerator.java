package no.ntnu.network.message.encryption.keygen;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

/**
 * A generator for symmetric keys.
 * The class acts as a strategy pattern interface, allowing for switching of key generation
 * algorithm during runtime.
 */
public interface SymmetricKeyGenerator {
    /**
     * Generates a secret key.
     *
     * @throws NoSuchAlgorithmException thrown if the algorithm cant be found
     */
    void createKey() throws NoSuchAlgorithmException;

    /**
     * Returns the last generated secret key.
     *
     * @return the secret key
     */
    SecretKey getKey();
}
