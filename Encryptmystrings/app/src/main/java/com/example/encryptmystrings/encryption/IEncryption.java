package com.example.encryptmystrings.encryption;

import java.security.Key;
import java.security.KeyPair;

import javax.crypto.Cipher;

/**
 * This interface creates a standard for working with KeyGen and creating keys and cyphers.
 * It might not be complete and can be enhance or modified in the future according to the needs.
 */
public interface IEncryption {
    String getAlias();
    String getTransformation();
    Cipher getEncryptionCipher(Key key) throws Exception;
    Cipher getDecryptionCipher(Key key) throws Exception;
    KeyPair getKey() throws Exception;
}
