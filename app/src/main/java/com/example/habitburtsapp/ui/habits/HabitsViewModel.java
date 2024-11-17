package com.example.habitburtsapp.ui.habits;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HabitsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HabitsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}