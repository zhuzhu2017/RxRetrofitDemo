package com.zhuzhu.allen.rxretrofitdemo.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhuzhu.allen.rxretrofitdemo.HttpPostService;
import com.zhuzhu.allen.rxretrofitlib.base.BaseApi;
import com.zhuzhu.allen.rxretrofitlib.listener.HttpOnNextListener;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by allen on 2017/8/24.
 */

public class SubjectResultApi extends BaseApi {

    //需要传递的参数
    private boolean all;

    public SubjectResultApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(true);
        setCanCancelProgress(true);
        setCacheNeeded(true);
        setBaseUrl("https://www.izaodao.com/Api/");
        setMethodName("AppFiftyToneGraph/videoLink");
        setCookieNetWorkTime(10);
        setCookieNoNetWorkTime(24 * 60 * 60);
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService httpPostService = retrofit.create(HttpPostService.class);
        return httpPostService.getAllVideoBy(isAll());
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }
}
