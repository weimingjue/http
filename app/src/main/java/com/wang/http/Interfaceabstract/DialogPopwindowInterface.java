package com.wang.http.Interfaceabstract;

/**
 * dialog和popwindow的基类,涉及到其他类型的dialog,最好实现这个接口
 */
public interface DialogPopwindowInterface extends HttpInterface {

    boolean isShowing();

    void show();

    void dismiss();
}
