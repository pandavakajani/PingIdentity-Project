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

    @Override
    public KeyPair getKey() throws Exception {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
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

    public String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(UTF_8));

        byte[] signature = privateSignature.sign();

        return Base64.getEncoder().encodeToString(signature);
    }

    public boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }

}
