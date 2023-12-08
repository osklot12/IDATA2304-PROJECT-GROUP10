package no.ntnu.network.message.encryption.keygen;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * An asymmetric key-pair generator using the RSA encryption algorithm.
 */
public class RSAKeyPairGenerator implements AsymmetricKeyPairGenerator {
    private final KeyPairGenerator keyGen;
    private KeyPair pair;

    /**
     * Creates a new RSAKeyPairGenerator.
     *
     * @throws NoSuchAlgorithmException thrown if the RSA algorithm is not found
     */
    public RSAKeyPairGenerator() throws NoSuchAlgorithmException {
        this.keyGen = KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(2048);
    }

    @Override
    public void createKeys() {
        pair = keyGen.generateKeyPair();
    }

    @Override
    public KeyPair getKeyPair() {
        return pair;
    }
}
