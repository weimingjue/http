package com.wang.http.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.wang.http.Interfaceabstract.DialogPopwindowInterface;
import com.wang.http.Interfaceabstract.HttpInterface;
import com.wang.http.Interfaceabstract.OKHttpListener;
import com.wang.http.base.BaseBean;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * okhttp的经典封装类,目前不支持文件
 * 所有的bean必须继承于basebean
 * 最好不要把interface传null
 * <p/>
 * HttpUtils.postDialog(this, Constans.A, MapUtils.getHttpInstance("a", 1).put("b", 2), XXX.class(继承BaseBean的), new OKHttpListener<XXX>() {
 *
 * @Override public void onSuccess(XXX bean) {
 * //此处是服务器返回10000
 * }
 * @Override public void onNetworkError(BaseBean baseBean) {
 * super.onNetworkError(baseBean);
 * //此处是网络连接失败,super是吐司
 * }
 * @Override public void onServiceError(String info) {
 * super.onServiceError(info);
 * //此处是服务器返回其他状态,super是吐司
 * }
 * @Override public void onNext(BaseBean baseBean) {
 * //此处无论成功失败必走
 * }
 * });
 */
public final class HttpUtils {

    //json中的字段
    public static final String KEY_USERTOKEN = "userToken", KEY_USERID = "userId", KEY_PAGENUM = "pageNum", KEY_PAGESIZE = "pageSize", KEY_STOREID = "storeId";

    public static final OkHttpClient mClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    //类型 utf-8
    public static final MediaType mMediaType = MediaType.parse("application/json;charset=UTF-8");

    ///////////////////////////////////////////////////////////////////////////
    // 以下是http公共方法
    ///////////////////////////////////////////////////////////////////////////

    //get
    public static <T extends BaseBean> void getDefault(HttpInterface httpInterface, String httpUrl, Class<T> mClass,
                                                       @NonNull OKHttpListener<T> listener) {
        httpCustom(httpInterface, httpUrl, new Request.Builder(), null, mClass, mClient, listener);
    }

    //dialog
//    public static <T extends BaseBean> void getDialog(HttpInterface httpInterface, String httpUrl, Class<T> mClass,
//                                                      @NonNull OKHttpListener<T> listener) {
//        httpCustom(httpInterface, httpUrl, new Request.Builder(), new LoadingDialog(httpInterface.getActivity())/*此处创建一个默认的dialog*/, mClass, mClient, listener);
//    }

    //postDefault
    public static <T extends BaseBean> void postDefault(HttpInterface httpInterface, String httpUrl, MapUtils mapUtils,
                                                        Class<T> mClass, @NonNull OKHttpListener<T> listener) {
        if (mapUtils.get(KEY_USERID) == null) {
            mapUtils.put(KEY_USERID, "用户id");
        }
        postCustom(httpInterface, httpUrl, RequestBody.create(mMediaType, mapUtils.toString()), null, mClass, listener);
    }

    //dialog
//    public static <T extends BaseBean> void postDialog(HttpInterface httpInterface, String httpUrl, MapUtils mapUtils,
//                                                       Class<T> mClass, @NonNull OKHttpListener<T> listener) {
//        if (mapUtils.get(KEY_USERID) == null) {
//            mapUtils.put(KEY_USERID, "用户id");
//        }
//        postCustom(httpInterface, httpUrl, RequestBody.create(mMediaType, mapUtils.toString()),
//                new LoadingDialog(httpInterface.getActivity())/*此处创建一个默认的dialog*/, mClass, listener);
//    }

    //RequestBody  Dialog可以传null
    public static <T extends BaseBean> void postCustom(HttpInterface httpInterface, String httpUrl, RequestBody requestBody,
                                                       DialogPopwindowInterface dialog, Class<T> mClass, @NonNull OKHttpListener<T> listener) {
        httpCustom(httpInterface, httpUrl, new Request.Builder().post(requestBody), dialog, mClass, mClient, listener);
    }

    /**
     * 自定义异步请求，增加client入参，没有默认数据需要自己手动增加
     */
    public static <T extends BaseBean> void httpCustom(final HttpInterface httpInterface, final String httpUrl,
                                                       final Request.Builder builder, final DialogPopwindowInterface dialog,
                                                       final Class<T> mClass, final OkHttpClient client, final OKHttpListener<T> listener) {
        if (dialog != null) dialog.show();
        new AsyncTask<Void, Void, BaseBean>() {
            @Override
            protected void onPostExecute(BaseBean baseBean) {
                super.onPostExecute(baseBean);
                if (dialog != null && dialog.getActivity() != null && !dialog.getActivity().isFinishing()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception ignored) {
                    }
                }
                //如果activity要求丢弃数据
                if (httpInterface != null && httpInterface.isDiscardHttp()) return;
                if (baseBean.httpCode == OKHttpListener.CODE_200) {
                    if (baseBean.code == OKHttpListener.CODE_SUCCESS)
                        listener.onSuccess((T) baseBean);
                    else listener.onServiceError(baseBean);
                } else {
                    listener.onNetworkError(baseBean);
                }
                listener.onNext(baseBean);
            }

            @Override
            protected BaseBean doInBackground(Void... params) {
                builder.tag(httpUrl).url(httpUrl)

                        //添加头信息
                        .addHeader(KEY_USERTOKEN, "token");

                try {
                    Response response = client.newCall(builder.build()).execute();
                    String body = response.body().string();
                    if (response.code() == OKHttpListener.CODE_200) {
                        try {
                            BaseBean bean = JSON.parseObject(body, mClass, Feature.SupportNonPublicField);//支持私有变量
                            bean.httpCode = response.code();
                            bean.response = body;
                            bean.httpUrl = httpUrl;
                            return bean;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new BaseBean(OKHttpListener.CODE_JSONEXCEPTION, null, body, httpUrl, response.headers());
                        }
                    } else {
                        return new BaseBean(response.code(), null, body, httpUrl, response.headers());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return new BaseBean(OKHttpListener.CODE_CONNECTXCEPTEION, null, null, httpUrl, null);
                }
            }
        }.execute();
    }
}
