package com.wang.http.base;

import android.support.v7.app.AppCompatActivity;

import com.wang.http.Interfaceabstract.HttpInterface;

public class BaseActivity extends AppCompatActivity implements HttpInterface {
    @Override
    public BaseActivity getActivity() {
        return this;
    }

    @Override
    public boolean isDiscardHttp() {
        return isFinishing();
    }
}
