package com.example.encryptmystrings.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainModel {
    //singleton pattern
    private static MainModel instance;
    public static MainModel getInstance(){
        if(instance == null){
            instance = new MainModel();
        }
        return instance;
    }

    private MutableLiveData<Boolean> toggleEncryption = new MutableLiveData<>(false);
    private MutableLiveData<String> operationStatus = new MutableLiveData<>("NOT STARTED");
    private MutableLiveData<String> inputText = new MutableLiveData<>("");

    public LiveData<Boolean> getToggleEncryption(){
        return toggleEncryption;
    }

    public LiveData<String> getOperationStatus(){
        return operationStatus;
    }

    public LiveData<String> getInputText(){
        return inputText;
    }

    public void setToggleEncryption(Boolean toggleEncryption) {
        this.toggleEncryption.postValue(toggleEncryption);
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus.postValue(operationStatus);
    }

    public void setInputText(String inputText) {
        this.inputText.postValue(inputText);
    }
}