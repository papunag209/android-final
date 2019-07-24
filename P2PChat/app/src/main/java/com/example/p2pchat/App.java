package com.example.p2pchat;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    static Context context = new Application();

    public static Context getContext() {
        return context;
    }
}
