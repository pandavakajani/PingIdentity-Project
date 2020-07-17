package com.example.encryptmystrings.encryption;

import java.security.KeyPair;
import java.util.concurrent.Executor;

public class EncryptionManager {
    public interface EncryptionManagerListener{
        void onKeyCreated(boolean isCreated);
        void onEncryptFinished(String result);
        void onDecryptedFinished(String result);
        void onSignFinished(String result);
        void onVerifyFinished(boolean result);
    }

    EncryptionKey encryptionHelper;
    SigningKey signingHelper;
    EncryptionManagerListener listener;

    public EncryptionManager(EncryptionManagerListener listener){
        encryptionHelper = new EncryptionKey();
        signingHelper = new SigningKey();
        this.listener = listener;
    }

    public void encryptString(final String text){
        EncryptionExecutor executor = new EncryptionExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String encryptedStr = "";
                    KeyPair pair = encryptionHelper.getKey();
                    listener.onKeyCreated(true);
                    if(text!=null && !text.isEmpty()){
                        encryptedStr = encryptionHelper.encrypt(text, pair.getPublic());
                        listener.onEncryptFinished(encryptedStr);
                    }
                } catch (Exception e) {
                    listener.onEncryptFinished(null);
                    e.printStackTrace();
                }
            }
        });
    }

    public void decryptString(final String text){
        EncryptionExecutor executor = new EncryptionExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String decryptedStr = "";

                try {
                    KeyPair pair = encryptionHelper.getKey();
                    if(text!=null && !text.isEmpty()){
                        decryptedStr = encryptionHelper.decrypt(text, pair.getPrivate());
                        listener.onDecryptedFinished(decryptedStr);
                    }
                } catch (Exception e) {
                    listener.onDecryptedFinished(null);
                    e.printStackTrace();
                }
            }
        });

    }

    public void sign(final String text){
        EncryptionExecutor executor = new EncryptionExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String signedStr = "";

                try {
                    KeyPair pair = signingHelper.getKey();
                    if(text!=null && !text.isEmpty()){
                        signedStr = signingHelper.sign(text, pair.getPrivate());
                        listener.onSignFinished(signedStr);
                    }
                } catch (Exception e) {
                    listener.onSignFinished(null);
                    e.printStackTrace();
                }
            }
        });

    }

    public void verify(final String signature, final String plainText){
        EncryptionExecutor executor = new EncryptionExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                boolean isVerified = false;
                try {
                    KeyPair pair = signingHelper.getKey();
                    isVerified = signingHelper.verify(plainText,signature,pair.getPublic());
                    listener.onVerifyFinished(isVerified);
                } catch (Exception e) {
                    listener.onVerifyFinished(false);
                    e.printStackTrace();
                }

            }
        });
    }

    private class EncryptionExecutor implements Executor{

        @Override
        public void execute(Runnable runnable) {
            runnable.run();
        }
    }
}
