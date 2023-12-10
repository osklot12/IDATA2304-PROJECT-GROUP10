package no.ntnu.network.message.encryption.cipher;

import no.ntnu.exception.EncryptionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * A base class for standard ciphers.
 */
public abstract class StandardCipher {
    private final String transformation;
    private final int mode;
    private final Key key;

    /**
     * Creates a new StandardCipher.
     *
     * @param transformation the algorithm to use for encryption
     * @param mode the mode of the cipher
     * @param key the key to use for encryption
     */
    protected StandardCipher(String transformation, int mode, Key key) {
        if (transformation == null) {
            throw new IllegalArgumentException("Algorithm cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        this.transformation = transformation;
        this.mode = mode;
        this.key = key;
    }

    /**
     * Returns a cipher using the RSA algorithm.
     *
     * @return the cipher
     * @throws EncryptionException thrown if cipher could not be established
     */
    private Cipher getCipher() throws EncryptionException {
        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new EncryptionException(e.getMessage());
        }

        return cipher;
    }

    /**
     * Initializes the cipher.
     *
     * @param cipher the cipher to initialize
     * @throws EncryptionException thrown if cipher cannot be initialized
     */
    private void initializeCipher(Cipher cipher) throws EncryptionException {
        try {
            cipher.init(mode, key);
        } catch (InvalidKeyException e) {
            throw new EncryptionException(e.getMessage());
        }
    }

    /**
     * Transforms data, assuming that a cipher has already been established.
     *
     * @param data the data to transform
     * @param cipher the cipher to use for transformation
     * @return the transformed data
     * @throws EncryptionException thrown if data cannot be transformed
     */
    private static byte[] transformData(byte[] data, Cipher cipher) throws EncryptionException {
        byte[] result = null;

        try {
            result = cipher.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException(e.getMessage());
        }

        return result;
    }

    /**
     * Transforms data.
     *
     * @param data the data to transform
     * @return the transformed data
     * @throws EncryptionException thrown if data transformation fails
     */
    protected byte[] transform(byte[] data) throws EncryptionException {
        Cipher cipher = getCipher();
        initializeCipher(cipher);
        return transformData(data, cipher);
    }
}
