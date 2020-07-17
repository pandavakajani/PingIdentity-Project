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
import javax.crypto.SecretKey;

import static java.nio.charset.StandardCharsets.UTF_8;

public class EncryptionKey implements IEncryption{
    private static final String TAG = EncryptionKey.class.getName();

    private String KEY_STORE = "AndroidKeyStore";
    private String ENCRYPTION_ALIAS = "ENCRTPTION_ALIAS";
    private String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    @Override
    public String getAlias() {
        return ENCRYPTION_ALIAS;
    }

    @Override
    public String getTransformation() {
        return TRANSFORMATION;
    }

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

    @Override
    public KeyPair getKey() throws Exception {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            if (!keyStore.containsAlias(getAlias())) { // Keystore not available.
                // Generate certificate for this new alias.
                generateCertificateForAlias();
            }

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(getAlias(), null);

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
     * It is also generates an alias for the keyStore for storing the keys
     */
    private void generateCertificateForAlias() throws Exception {
        final String ALGORITHM_KEYSTORE = "RSA";
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM_KEYSTORE, KEY_STORE);

        final KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                    getAlias(),
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setKeySize(1024)
                    .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build();
            keyGen.initialize(spec);
            keyGen.generateKeyPair();
    }

    public String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher encryptCipher = getEncryptionCipher(publicKey);

        byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));

        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(cipherText);

        Cipher decriptCipher = getDecryptionCipher(privateKey);
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(decriptCipher.doFinal(bytes), UTF_8);
    }

}
