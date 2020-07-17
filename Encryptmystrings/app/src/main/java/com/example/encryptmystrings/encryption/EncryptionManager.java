package com.example.encryptmystrings.encryption;

import androidx.room.util.StringUtil;

import java.security.Key;
import java.security.KeyPair;

import javax.crypto.SecretKey;

public class EncryptionManager {
    EncryptionKey encryptionHelper;
    SigningKey signingHelper;

    public EncryptionManager(){
        encryptionHelper = new EncryptionKey();
        signingHelper = new SigningKey();
    }

    public String encryptString(String text){
        String encryptedStr = "";

        try {
            KeyPair pair = encryptionHelper.getKey();
            if(text!=null && !text.isEmpty()){
                encryptedStr = encryptionHelper.encrypt(text, pair.getPublic());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedStr;
    }

    public String decryptString(String text){
        String decryptedStr = "";

        try {
            KeyPair pair = encryptionHelper.getKey();
            if(text!=null && !text.isEmpty()){
                decryptedStr = encryptionHelper.decrypt(text, pair.getPrivate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedStr;
    }

    public String sign(String text){
        String signedStr = "";

        try {
            KeyPair pair = signingHelper.getKey();
            if(text!=null && !text.isEmpty()){
                signedStr = signingHelper.sign(text, pair.getPrivate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return signedStr;
    }

    public boolean verify(String signature, String plainText){
        boolean isVerified = false;
        try {
            KeyPair pair = signingHelper.getKey();
            isVerified = signingHelper.verify(plainText,signature,pair.getPublic());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isVerified;
    }
}
