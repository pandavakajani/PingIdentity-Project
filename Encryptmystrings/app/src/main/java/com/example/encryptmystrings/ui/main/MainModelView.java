package com.example.encryptmystrings.ui.main;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.RequestQueue;
import com.example.encryptmystrings.firebase.FirebaseMessagingHelper;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainModelView extends ViewModel {

    //declaration of members
    private LiveData<Boolean> toggleEncryption;
    private LiveData<String> operationStatus;
    private LiveData<String> inputText;
    private LiveData<String> textView;

    private MainModel model;

    //init our members
    public void init(){
        //init model if needed
        if(model == null){
            model = MainModel.getInstance();
        }
        //init the live data if not done already
        if(toggleEncryption == null){
            toggleEncryption = model.getToggleEncryption();
        }

        if(operationStatus == null){
            operationStatus = model.getOperationStatus();
        }

        if(inputText == null){
            inputText = model.getInputText();
        }

        if(textView == null){
            textView = model.getTextView();
        }
    }

    /* the liveDate getter methods return LiveData in order to prevent writing
       to the live data from outside */

    public LiveData<Boolean> getToggleEncryption() {
        return toggleEncryption;
    }

    public LiveData<String> getOperationStatus() {
        return operationStatus;
    }

    public LiveData<String> getInputText() {
        return inputText;
    }

    public LiveData<String> getTextView(){
        return textView;
    }

    public void updateInputText(String inputText){
        model.setInputText(inputText);
    }

    public void updateTextView(String text){
        model.setTextView(text);
    }

    // bind method was done in the layout (main_fragment)
    public void onEncryptionToggled(){
        boolean isOn = toggleEncryption.getValue();
        model.setOperationStatus("Toggle button is now turned " + (isOn==false? "ON" : "OFF"));
        model.setToggleEncryption(!toggleEncryption.getValue());
    }

    // bind method was done in the layout (main_fragment)
    public void onButtonSendClicked(View v){
        //1. encrypt
        NavController navController = Navigation.findNavController(v);
        navigateToDecryptedFragment(navController);

//        if(navController!=null){
//            MainFragmentDirections.ActionMainFragmentToDecryptedFragment action = MainFragmentDirections.actionMainFragmentToDecryptedFragment().setDecryptedText(getInputText().getValue());
//            navController.navigate(action);
//        }
    }

    public void navigateToDecryptedFragment(NavController navController){
        if(navController!=null){
            MainFragmentDirections.ActionMainFragmentToDecryptedFragment action = MainFragmentDirections.actionMainFragmentToDecryptedFragment().setDecryptedText(getInputText().getValue());
            navController.navigate(action);
        }
    }
}
