package com.example.boardgametimer.ui.register;

import androidx.annotation.Nullable;

/**
 * Data validation state of the register form.
 */
class RegisterFormState {

    @Nullable
    private Integer emailError;

    @Nullable
    private Integer passwordError;

    @Nullable
    private Integer repeatedPasswordError;

    private boolean isDataValid;

    RegisterFormState(@Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer repeatedPasswordError) {
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.repeatedPasswordError = repeatedPasswordError;
        this.isDataValid = false;
    }

    RegisterFormState(boolean isDataValid) {
        this.emailError = null;
        this.passwordError = null;
        this.repeatedPasswordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getRepeatedPasswordError() {
        return repeatedPasswordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }

}
