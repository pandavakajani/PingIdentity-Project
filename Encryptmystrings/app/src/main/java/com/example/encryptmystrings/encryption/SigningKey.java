package com.example.encryptmystrings.encryption;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Base64;
import javax.crypto.Cipher;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This class is responsible for signing and verifying a string.
 * it generates RSA key to the keyStore and PKCS1Padding.
 */
public class SigningKey implements IEncryption {
    private String KEY_STORE = "AndroidKeyStore";
    private String ENCRYPTION_ALIAS = "SIGNING_ALIAS";
    private String TRANSFORMATION = "RSA/EBC/PKCS1Padding";

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
        return null;
    }

    @Override
    public Cipher getDecryptionCipher(Key key) throws Exception {
        return null;
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
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);//get the key store
            keyStore.load(null);
            if (!keyStore.containsAlias(getAlias())) { // Keystore not available.
                // Generate certificate for this new alias.
                generateCertificateForAlias();
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
     * and the padding is PKCS1Padding
     * It is also generates an alias for the keyStore for storing the keys
     */
    private void generateCertificateForAlias() throws Exception{
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE);

        final KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                getAlias(),
                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .build();
        keyGen.initialize(spec);
        keyGen.generateKeyPair();
    }

    /**
     * Signs the given text and generate a signature
     * @param plainText text to sign
     * @param privateKey key with which we sign
     * @return signature to verify with
     * @throws Exception
     */
    public String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(UTF_8));

        byte[] signature = privateSignature.sign();

        return Base64.getEncoder().encodeToString(signature);
    }

    /**
     * Verifies decrypted text using the public key and previously created signature
     * @param plainText decrypted text
     * @param signature signature to verify with
     * @param publicKey public key to verify with
     * @return TRUE on verified
     *         FALSE on not verified
     * @throws Exception
     */
    public boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }

}
