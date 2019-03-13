package com.wang.http.Interfaceabstract;

import android.text.TextUtils;

import com.wang.http.BuildConfig;
import com.wang.http.base.BaseBean;
import com.wang.http.utils.Utils;

/**
 * okHttp的回调监听
 */
public abstract class OKHttpListener<T extends BaseBean> {
    public static final String TAG = OKHttpListener.class.getSimpleName();

    //后台的状态码             成功                  身份认证过期
    public static final int CODE_SUCCESS = 10000, CODE_GUOQI = 20100, CODE_FAILED = 20000;

    //其他状态码                  json解析异常                网络连接异常
    public static final int CODE_JSONEXCEPTION = -999, CODE_CONNECTXCEPTEION = -998, CODE_200 = 200, CODE_404 = 404, CODE_500 = 500, CODE_502 = 502;

    public void onNetworkError(BaseBean baseBean) {//连接失败返回
        String st;
        switch (baseBean.httpCode) {
            case CODE_CONNECTXCEPTEION:
                st = "网络连接失败,请检查网络再试!";
                break;
            case CODE_JSONEXCEPTION:
                st = "数据解析异常,请更新版本或稍后再试!";
                break;
            case CODE_404:
                st = "网址不存在,请更新版本或稍后再试!";
                break;
            case CODE_500:
                st = "服务器异常,请稍后再试!";
                break;
            case CODE_502:
                st = "服务器打了个哈欠,等一会再看看吧";
                break;
            default:
                st = "连接失败!错误码:" + baseBean.httpCode;
                break;
        }
        Utils.toastInstance(st);
    }

    public void onServiceError(BaseBean baseBean) {//服务器返回非success,例如message="无效请求"
        Utils.toastInstance(TextUtils.isEmpty(baseBean.message) ? "获取信息失败!" : BuildConfig.DEBUG ? (baseBean.message + baseBean.detailMessage) : baseBean.message);
    }

    //此方法必走
    public void onNext(BaseBean bean) {
    }

    public abstract void onSuccess(T bean);

    /**
     * 此bean是否是一条成功的数据
     */
    public static boolean isSuccessedBean(BaseBean bean) {
        return bean.httpCode == CODE_200 && bean.code == CODE_SUCCESS;
    }
}