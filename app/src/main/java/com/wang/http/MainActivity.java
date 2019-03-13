package com.wang.http;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.wang.http.Interfaceabstract.OKHttpListener;
import com.wang.http.base.BaseActivity;
import com.wang.http.base.BaseBean;
import com.wang.http.utils.HttpUtils;
import com.wang.http.utils.MapUtils;
import com.wang.http.utils.Utils;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpUtils.postDefault(this, "填写你的url",
                MapUtils.getHttpInstance().put("type", 1),
                BaseBean.class, new OKHttpListener<BaseBean>() {
                    @Override
                    public void onSuccess(BaseBean bean) {
                        Utils.toast(bean.response);
                    }
                });
    }
}
