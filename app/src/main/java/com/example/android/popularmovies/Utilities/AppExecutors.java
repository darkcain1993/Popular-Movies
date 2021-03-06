package com.example.android.popularmovies.Utilities;

import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AppExecutors {

    //For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor diskIO;
    //private final Executor mainThread;
    //private final Executor networkIO;

    private AppExecutors(Executor diskIO, Executor mainThread, Executor networkIO){
        this.diskIO = diskIO;
        //this.mainThread = mainThread;
        //this.networkIO = networkIO;
    }

    public static AppExecutors getInstance(){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(1), new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor diskIO(){return diskIO;}

    private static class MainThreadExecutor implements Executor{
        private android.os.Handler mainThreadHandler = new android.os.Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
