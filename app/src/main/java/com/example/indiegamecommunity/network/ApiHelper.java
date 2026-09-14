package com.example.indiegamecommunity.network;

import android.os.Handler;
import android.os.Looper;

public class ApiHelper {

    public interface Callback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    public static <T> void execute(SupplierWithException<T> supplier, Callback<T> callback) {
        new Thread(() -> {
            try {
                T result = supplier.get();
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    if (callback != null) callback.onSuccess(result);
                });
            } catch (Exception e) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    if (callback != null) callback.onError(e.getMessage());
                });
            }
        }).start();
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }
}
