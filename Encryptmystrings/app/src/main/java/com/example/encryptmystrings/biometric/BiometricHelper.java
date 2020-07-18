package com.example.encryptmystrings.biometric;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import com.example.encryptmystrings.R;

import java.util.concurrent.Executor;


/**
 * This class is a simple biometric helper that encapsulate some of the biometric abilities
 * provided by the OS.
 * it provides the ability to verify the biometric authentication with minimum code in the app
 */
public class BiometricHelper {
    /**
     * Interface to communicate back with the caller after the work is done
     */
    public interface BiometricAuthenticationResponseListener{
        void authenticationResolutionSuccess();
    }

    /**
     *
     * @param ctx - Context that allows us to "talk" to the ui
     * @return - 'true' if authentication succeeded.
     *           'false' if from some reason it failed
     */
    public static boolean canUseBiometricAuthentication(Context ctx){
        BiometricManager biometricManager = BiometricManager.from(ctx);
        boolean canAuthenticate = false;
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                canAuthenticate = true;
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(ctx.getApplicationContext(), ctx.getString(R.string.biometric_error_no_hardware),Toast.LENGTH_LONG);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(ctx.getApplicationContext(), ctx.getString(R.string.biometric_error_hw_unavailable),Toast.LENGTH_LONG);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(ctx.getApplicationContext(), ctx.getString(R.string.biometric_error_non_enrolled),Toast.LENGTH_LONG);
                break;
        }
        return canAuthenticate;
    }

    /**
     * Shows a biometric prompt to the user
     * @param ctx  - Context that allows us to "talk" to the ui
     * @param activity - needed for the biometric prompt
     * @param listener - communicate back with the caller
     */
    public static void showBiometricPrompt(final Context ctx, FragmentActivity activity, final BiometricAuthenticationResponseListener listener){
        Executor executor;
        BiometricPrompt biometricPrompt;
        BiometricPrompt.PromptInfo promptInfo;
        executor = ContextCompat.getMainExecutor(ctx);
        biometricPrompt = new BiometricPrompt(activity,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(ctx.getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(ctx.getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                listener.authenticationResolutionSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(ctx.getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
