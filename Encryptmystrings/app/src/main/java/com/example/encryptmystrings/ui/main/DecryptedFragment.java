package com.example.encryptmystrings.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.encryptmystrings.R;
import com.example.encryptmystrings.biometric.BiometricHelper;
import com.example.encryptmystrings.databinding.DecrypedLayoutBindingImpl;

/**
 * Simple fragment to show the decrypted string.
 * It uses biometric authentication when needed.
 * It initiates the decryption and verifying of the decrypted string.
 * Binds data from {@link MainModel} to {@link com.example.encryptmystrings.R.layout#decryped_layout} using {@link MainModelView}
 */
public class DecryptedFragment extends Fragment implements BiometricHelper.BiometricAuthenticationResponseListener{
    MainModelView modelView;

    @Override
    public void onResume() {
        super.onResume();
        boolean shouldUseBiometric = modelView.getToggleEncryption().getValue();//flag for biometric authentication
        if(shouldUseBiometric) {
            goThroughBiometric();//open biometric dialog
        }else{
            authenticationResolutionSuccess();//go directly to decrypting and verifying
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DecrypedLayoutBindingImpl binding = DataBindingUtil.inflate(inflater, R.layout.decryped_layout, container, false);
        modelView = new ViewModelProvider(this.getActivity()).get(MainModelView.class);

        //init text view to not show text until we are sure that it is safe
        modelView.updateTextView("");

        binding.setLifecycleOwner(this);
        binding.setViewModel(modelView);

        return binding.getRoot();
    }

    /**
     * Opens up a biometric dialog
     */
    private void goThroughBiometric(){
        if(BiometricHelper.canUseBiometricAuthentication(getContext())){
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    BiometricHelper.showBiometricPrompt(getContext(), getActivity(), DecryptedFragment.this);
                }
            });

        }
    }

    /**
     * Callback from biometric dialog to enable the view and decrypt text
     */
    @Override
    public void authenticationResolutionSuccess() {
        //decrypt and show field
        modelView.decryptAndVerify(DecryptedFragmentArgs.fromBundle(getArguments()).getDecryptedText(),
                DecryptedFragmentArgs.fromBundle(getArguments()).getSignature());
    }
}
