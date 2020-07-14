package com.example.encryptmystrings.ui.main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.encryptmystrings.R;
import com.example.encryptmystrings.databinding.MainFragmentBinding;

public class MainFragment extends Fragment {
    MainModelView modelView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        modelView = new ViewModelProvider(this.getActivity()).get(MainModelView.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(modelView);
        initViews(binding);
        return binding.getRoot();
    }

    //init the liveData observers
    private void initViews(MainFragmentBinding binding) {
        EditText input = binding.getRoot().findViewById(R.id.edit_text_input);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                modelView.updateInputText(editable.toString());
            }
        });
    }

}
