package com.wang.http.utils;

import android.widget.Toast;

import com.wang.http.base.MApp;

public class Utils {

    //null判断主要是view视图看不到，此处根本不可能为null
    private static Toast mToast = MApp.mApp == null ? null : Toast.makeText(MApp.mApp, "", Toast.LENGTH_SHORT);

    public static void toast(CharSequence cs) {
        Toast.makeText(MApp.mApp, cs, Toast.LENGTH_SHORT).show();
    }

    /**
     * 单例的吐司,只会展示一个
     */
    public static void toastInstance(CharSequence cs) {
        mToast.setText(cs);
        mToast.show();
    }
}
