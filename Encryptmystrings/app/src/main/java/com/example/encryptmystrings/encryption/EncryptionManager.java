package com.example.encryptmystrings.encryption;

import com.google.crypto.tink.KeysetWriter;

import java.security.KeyPair;
import java.util.concurrent.Executor;

public class EncryptionManager {
    public interface EncryptionManagerListener{
        void onKeyCreated(boolean isCreated);
        void onEncryptFinished(boolean isSuccess);
        void onDecryptedFinished(boolean isSuccess);
        void onSignFinished(boolean isSuccess);
        void onVerifyFinished(boolean result);
        void onEncryptionSignFinished(boolean isSuccess, String encrypted, String signature);
        void onDecryptedVerifyFinished(boolean isSuccess, String decrypted);
    }

    EncryptionKey encryptionHelper;
    SigningKey signingHelper;
    EncryptionManagerListener listener;

    public EncryptionManager(EncryptionManagerListener listener){
        encryptionHelper = new EncryptionKey();
        signingHelper = new SigningKey();
        this.listener = listener;
    }

    /**
     * This method run in the background doing:
     * 1. decrypting the encrypted text
     * 2. verifying the plain text
     * 3. callback to end the process
     * @param encryptedText - the encrypted text
     * @param signature - the signature on the plain text
     */
    public void decryptAndVerify(final String encryptedText, final String signature){
        EncryptionExecutor executor = new EncryptionExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String decryptString = decryptString(encryptedText);
                if(verify(signature, decryptString)){
                    listener.onDecryptedVerifyFinished(true, decryptString);
                }
            }
        });
    }

    public void encryptAndSign(final String text){
        EncryptionExecutor executor = new EncryptionExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String encrypted = encryptString(text);
                if(!encrypted.isEmpty()){
                    String signature = sign(text);
                    if(!signature.isEmpty()){
                        listener.onEncryptionSignFinished(true, encrypted, signature);
                    }
                }
            }
        });
    }

    private String encryptString(String text){
        String encryptedStr = "";
        try {
            KeyPair pair = encryptionHelper.getKey();
            listener.onKeyCreated(true);
            if(text!=null && !text.isEmpty()){
                encryptedStr = encryptionHelper.encrypt(text, pair.getPublic());
                listener.onEncryptFinished(true);
            }
        } catch (Exception e) {
            listener.onEncryptFinished(false);
            e.printStackTrace();
        }
        return encryptedStr;
    }

    private String decryptString(final String text){
        String decryptedStr = "";
        try {
            KeyPair pair = encryptionHelper.getKey();
            if(text!=null && !text.isEmpty()){
                decryptedStr = encryptionHelper.decrypt(text, pair.getPrivate());
                listener.onDecryptedFinished(true);
            }
        } catch (Exception e) {
            listener.onDecryptedFinished(false);
            e.printStackTrace();
        }
        return decryptedStr;
    }

    public String sign(final String text){
        String signedStr = "";
        try {
            KeyPair pair = signingHelper.getKey();
            if(text!=null && !text.isEmpty()){
                signedStr = signingHelper.sign(text, pair.getPrivate());
                listener.onSignFinished(true);
            }
        } catch (Exception e) {
            listener.onSignFinished(false);
            e.printStackTrace();
        }
        return signedStr;
    }

    private boolean verify(final String signature, final String plainText){
        boolean isVerified = false;
        try {
            KeyPair pair = signingHelper.getKey();
            isVerified = signingHelper.verify(plainText,signature,pair.getPublic());
            listener.onVerifyFinished(isVerified);
        } catch (Exception e) {
            listener.onVerifyFinished(false);
            e.printStackTrace();
        }
        return isVerified;
    }

    private class EncryptionExecutor implements Executor{

        @Override
        public void execute(Runnable runnable) {
            runnable.run();
        }
    }
}
