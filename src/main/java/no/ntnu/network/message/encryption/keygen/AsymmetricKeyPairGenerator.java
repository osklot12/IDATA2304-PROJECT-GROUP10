package no.ntnu.network.message.encryption.keygen;

import java.security.KeyPair;

/**
 * A generator for asymmetric key pairs.
 * The class acts as a strategy pattern interface, allowing for switching of key-pair generation
 * algorithm during runtime.
 */
public interface AsymmetricKeyPairGenerator {
    /**
     * Creates a public and private key to be used for asymmetric encryption.
     */
    void createKeys();

    /**
     * Returns the last created key pair.
     *
     * @return the key pair
     */
    KeyPair getKeyPair();
}
