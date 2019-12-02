package com.example.boardgametimer.data;

public class RegisterRepository {

    private static volatile RegisterRepository instance;

    public static RegisterRepository getInstance() {
        if (instance == null) {
            instance = new RegisterRepository();
        }
        return instance;
    }

}
