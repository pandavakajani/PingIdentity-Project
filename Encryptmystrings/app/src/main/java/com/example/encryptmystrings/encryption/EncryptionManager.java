package com.example.encryptmystrings.encryption;

import java.security.KeyPair;
import java.util.concurrent.Executor;

/**
 * This class is managing the work for encrypt/decrypt and sign/validate
 * it provides an interface: EncryptionManagerListener that enables the communication
 * with the requester.
 * It executes the work in the background using Executor.
 */
public class EncryptionManager {
    //This interface allows the EncryptionManager to callback requester and notify the progress
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
     * This method runs in the background and doing the following:
     * 1. decrypting the encrypted text
     * 2. verifying the plain text
     * 3. callback to end the process
     * It runs in the background
     * @param encryptedText - the encrypted text
     * @param signature - the signature on the plain text
     */
    public void decryptAndVerify(final String encryptedText, final String signature){
        EncryptionExecutor executor = new EncryptionExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String decryptString = decryptString(encryptedText);//decrypt
                if(verify(signature, decryptString)){//verify
                    listener.onDecryptedVerifyFinished(true, decryptString);
                }
            }
        });
    }

    /**
     * This method runs in the background and doing the following:
     * 1. encrypting the plain text
     * 2. signing the plain text
     * 3. callback to end the process
     * It runs in the background
     * @param text - the plain text
     */
    public void encryptAndSign(final String text){
        EncryptionExecutor executor = new EncryptionExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String encrypted = encryptString(text);//encrypt
                if(!encrypted.isEmpty()){
                    String signature = sign(text);//sign
                    if(!signature.isEmpty()){
                        listener.onEncryptionSignFinished(true, encrypted, signature);
                    }
                }
            }
        });
    }

    /**
     * This method encrypt a given string
     * It uses its' member (encryptionHelper) to provide the needed keys and cypher
     * Uses the listener to update the caller on the progress
     * @param text - text to encrypt
     * @return encrypted string on SUCCESS
     *         empty string on FAILURE
     */
    private String encryptString(String text){
        String encryptedStr = "";
        try {
            KeyPair pair = encryptionHelper.getKey();//keyPair create
            listener.onKeyCreated(true);//notify caller class
            if(text!=null && !text.isEmpty()){ //validation on result
                encryptedStr = encryptionHelper.encrypt(text, pair.getPublic());//encrypt
                listener.onEncryptFinished(true);//notify caller on SUCCESS
            }
        } catch (Exception e) {
            listener.onEncryptFinished(false);
            e.printStackTrace();//notify caller on FAILURE
        }
        return encryptedStr;
    }

    /**
     * This method decrypt a given string
     * It uses its' member (encryptionHelper) to provide the needed keys and cypher
     * Uses the listener to update the caller on the progress
     * @param text - text to decrypt
     * @return decrypted string on SUCCESS
     *         empty string on FAILURE
     */
    private String decryptString(final String text){
        String decryptedStr = "";
        try {
            KeyPair pair = encryptionHelper.getKey();//provide keyPair
            if(text!=null && !text.isEmpty()){//validate input
                decryptedStr = encryptionHelper.decrypt(text, pair.getPrivate());//decrypt
                if(decryptedStr!=null && !decryptedStr.isEmpty()) {//validate response
                    listener.onDecryptedFinished(true);//notify caller on SUCCESS
                }else{

                }listener.onDecryptedFinished(false);//notify caller on FAILURE
            }
        } catch (Exception e) {
            listener.onDecryptedFinished(false);//notify caller on FAILURE
            e.printStackTrace();
        }
        return decryptedStr;
    }

    /**
     * This method signs the text
     * @param text text to sign
     * @return the signature to be used in verification
     */
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

    /**
     * The method verifies a plain decrypted text with its signature
     * @param signature signature of the plain text
     * @param plainText decrypted text
     * @return TRUE on success
     *         FALSE on failure
     */
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
