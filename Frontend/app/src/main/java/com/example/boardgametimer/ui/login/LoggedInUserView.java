package com.example.boardgametimer.ui.login;

import com.example.boardgametimer.data.model.LoggedInUser;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {

    private LoggedInUser user;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(LoggedInUser displayName) {
        this.user = displayName;
    }

    String getDisplayName() {
        return user.getDisplayName();
    }

    LoggedInUser getUser() {
        return user;
    }

}
