package com.zhuzhu.allen.rxretrofitlib.manager;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.zhuzhu.allen.rxretrofitlib.base.BaseApi;
import com.zhuzhu.allen.rxretrofitlib.cookie.CookieInterceptor;
import com.zhuzhu.allen.rxretrofitlib.exception.RetryWhenNetworkException;
import com.zhuzhu.allen.rxretrofitlib.listener.HttpOnNextListener;
import com.zhuzhu.allen.rxretrofitlib.subscriber.ProgressSubscriber;

import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * http交互处理类
 * Created by allen on 2017/8/23.
 */

public class HttpManager {
    /*HttpManager对象*/
    private volatile static HttpManager INSTANCE;

    /*构造私有方法*/
    private HttpManager() {
    }

    /*获取单例对象*/
    public static HttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 网络请求，处理http请求
     *
     * @param baseApi 封装的请求数据
     */
    public void connToServer(BaseApi baseApi) {
        /*手动创建一个OkHttpClient并设置超时时间*/
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(baseApi.getConnTimeout(), TimeUnit.SECONDS);
        //添加拦截器--处理缓存的保存和更新
        builder.addInterceptor(new CookieInterceptor(baseApi.isCacheNeeded(), baseApi.getUrl()));

        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseApi.getBaseUrl())
                .build();

        /*rxjava处理*/
        //加载框设置
        ProgressSubscriber subscriber = new ProgressSubscriber(baseApi);
        Observable observable = baseApi.getObservable(retrofit)
               /*失败后的retry配置*/
                .retryWhen(new RetryWhenNetworkException(baseApi.getRetryCount(),
                        baseApi.getRetryDelay(), baseApi.getRetryIncreaseDelay()))
                /*生命周期管理*/
                .compose(baseApi.getRxAppCompatActivity().bindUntilEvent(ActivityEvent.PAUSE))
                /*http请求线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                /*结果判断*/
                .map(baseApi);
        //链接式对象返回
        SoftReference<HttpOnNextListener> httpOnNextListener = baseApi.getListener();
        if (httpOnNextListener != null && httpOnNextListener.get() != null) {
            httpOnNextListener.get().onNext(observable);
        }
        //数据回调
        observable.subscribe(subscriber);
    }

}
