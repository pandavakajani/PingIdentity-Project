package com.example.encryptmystrings.encryption;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Base64;

import javax.crypto.Cipher;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * This class is responsible for encrypting and decrypting a string.
 * it generates RSA key to the keyStore and uses EBC block and PKCS1Padding.
 */
public class EncryptionKey implements IEncryption{
    private static final String TAG = EncryptionKey.class.getName();

    //Constants
    private String KEY_STORE = "AndroidKeyStore";
    private String ENCRYPTION_ALIAS = "ENCRTPTION_ALIAS";
    private String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    //Interface implementation
    @Override
    public String getAlias() {
        return ENCRYPTION_ALIAS;
    }

    @Override
    public String getTransformation() {
        return TRANSFORMATION;
    }

    /**
     * Creates a cypher to encrypt the text
     * @param key This is the public key we use for encryption
     * @return cypher object that will do the actual encryption job
     * @throws Exception
     */
    @Override
    public Cipher getEncryptionCipher(Key key) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(getTransformation());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("getEncryptionCipher: exception" + ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Creates a cypher to decrypt the text
     * @param key This is the private key we use for decryption
     * @return cypher object that will do the actual decryption job
     * @throws Exception
     */
    @Override
    public Cipher getDecryptionCipher(Key key) throws Exception {
        try {
            final Cipher cipher = Cipher.getInstance(getTransformation());
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("getDecryptionCipher: exception" + ex.getMessage(), ex.getCause());
        }
    }

    /**
     * This method is responsible for providing the caller RSA keyPair.
     * It will get it from key store if exists or call a creator if not.
     * @return KeyPair - private and public key.
     * @throws Exception
     */
    @Override
    public KeyPair getKey() throws Exception {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");//get the key store
            keyStore.load(null);
            if (!keyStore.containsAlias(getAlias())) { // Keystore not available.
                generateCertificateForAlias();// Generate certificate for this new alias.
            }

            //pull out private keyEntry from keystore using alias
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(getAlias(), null);

            //pull RSA private and public key to be returned
            Certificate cert = keyStore.getCertificate(getAlias());
            PublicKey publicKey = cert.getPublicKey();
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();

            return new KeyPair(publicKey, privateKey);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("GetKey: exception" + ex.getMessage(), ex.getCause());
        }
    }

    /**
     * @throws Exception
     * This Method creates a key pair of RSA type
     * the block that is used is CBC
     * and the padding is PKCS1Padding
     * It is also generates an alias for the keyStore for storing the keys
     */
    private void generateCertificateForAlias() throws Exception {
        final String ALGORITHM_KEYSTORE = "RSA";
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM_KEYSTORE, KEY_STORE);

        final KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                    getAlias(),//the name of the entry in the keyStore
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)//why do we need this keys for
                    .setKeySize(1024)
                    .setBlockModes(KeyProperties.BLOCK_MODE_ECB) //block mode
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1) //padding
                    .build();
            keyGen.initialize(spec);
            keyGen.generateKeyPair();
    }

    /**
     * This method encrypt a provided text with provided public key
     * @param plainText text to encrypt
     * @param publicKey key for encryption
     * @return encrypted string
     * @throws Exception
     */
    public String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher encryptCipher = getEncryptionCipher(publicKey); //get cypher

        byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));

        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     * This method decrypt a provided text with provided private key
     * @param cipherText encrypted text
     * @param privateKey private key for decryption
     * @return plain text after decryption
     * @throws Exception
     */
    public String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(cipherText);

        Cipher decriptCipher = getDecryptionCipher(privateKey);
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(decriptCipher.doFinal(bytes), UTF_8);
    }

}
