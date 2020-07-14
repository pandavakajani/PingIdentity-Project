package com.example.encryptmystrings.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.encryptmystrings.R;
import com.example.encryptmystrings.databinding.DecrypedLayoutBindingImpl;

public class DecryptedFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DecrypedLayoutBindingImpl binding = DataBindingUtil.inflate(inflater, R.layout.decryped_layout, container, false);
        binding.textMessage.setText(DecryptedFragmentArgs.fromBundle(getArguments()).getDecryptedText());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

}
