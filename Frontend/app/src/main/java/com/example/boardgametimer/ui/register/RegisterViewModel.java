package com.example.boardgametimer.ui.register;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.boardgametimer.R;
import com.example.boardgametimer.data.RegisterRepository;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private RegisterRepository registerRepository;

    public RegisterViewModel(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    void registerDataChanged(String email, String password) {
        if (!isEmailValid(email)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_email, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password, null));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    void registerDataChanged(String email, String password, String repeatedPassword) {
        if (!isEmailValid(email)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_email, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password, null));
        } else if (!password.equals(repeatedPassword)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.not_same_password));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    // A placeholder email validation check
    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 0;
//        return password != null && password.trim().length() > 5;
    }

}
