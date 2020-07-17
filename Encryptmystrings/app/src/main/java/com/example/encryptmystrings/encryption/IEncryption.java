package com.example.encryptmystrings.encryption;

import java.security.Key;
import java.security.KeyPair;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public interface IEncryption {
    String getAlias();
    String getTransformation();
    Cipher getEncryptionCipher(Key key) throws Exception;
    Cipher getDecryptionCipher(Key key) throws Exception;
    KeyPair getKey() throws Exception;
}
