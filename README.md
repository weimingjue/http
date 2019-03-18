# 示例如下
```
        HttpUtils.postDefault(this, "填写你的url",
                MapUtils.getHttpInstance().put("type", 1),
                BaseBean.class, new OKHttpListener<BaseBean>() {
                    @Override
                    public void onSuccess(BaseBean bean) {
                        Utils.toast(bean.response);
                    }
                });
```
也许你不敢相信上面几行代码已经做了：
1.添加默认header：userId
2.追加默认body：token
3.不弹出加载中的对话框（也可以自定义选择弹出）
4.回调结果就是ui线程，不需要切换线程
5.网络问题会回调OKHttpListener的onNetworkError
6.已经帮您解析成指定的bean了
7.服务器返回失败状态会回调OKHttpListener的onServiceError
8.Activity finish时默认不做任何回调（当然也可以修改让它回调）
9.当然还有onNext等等，思路给你任你自定义

## 对于一个失败的封装，要想做到上面的效果，只能这样
```
final Dialog loading=new LaodingDialog(this);//添加进度条
Body body=new Body();
body.put("token","xxx");
body.put("activityId",1);
XXHttp.getInstance()//单例(自己还单独封装了一层，设置了请求时间等)
        .addHeader(new Header().add("userId",1).add("type","json")...)//添加header
        .url("http...").tag("http...").post()//添加url并设置post
        .body(body.build())//添加body
        .build().execute(new XXListener(){//回调
            @Override
            public void onError(int errorCode) {//网络失败相关
                loading.dismiss();//关闭对话框
                switch (errorCode){
                    case 1:
                        //提示1
                        break;
                ...
                    default:
                        break;
                }
            }
            @Override
            public void onSuccess(final Response response) {//网络畅通
                runOnUiThread(new Runnable() {//需要回到主线程，有的甚至还用了handler
                    @Override
                    public void run() {
                        loading.dismiss();
                        if (response.code()==200){//需要判断请求状态
                            XXBean bean = JSON.parseObject(body, XXBean.Class);//解析后台数据
                            if (bean.code==200){//后台状态码
                                if (!XXActivity.this.isFinish()){//还得判断Activity是否挂了
                                    mTv.setText(bean.data.text);//终于完成了
                                }
                            }else {
                                //提示2
                            }
                        }else {
                            //提示3
                        }
                    }
                });
            }
        });
```
你还在等什么，下载下来修改一下在你的项目中尽情驰骋吧
