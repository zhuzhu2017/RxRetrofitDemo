package com.zhuzhu.allen.rxretrofitlib.subscriber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhuzhu.allen.rxretrofitlib.RxRetrofitApp;
import com.zhuzhu.allen.rxretrofitlib.base.BaseApi;
import com.zhuzhu.allen.rxretrofitlib.cookie.CookieResult;
import com.zhuzhu.allen.rxretrofitlib.exception.HttpResultException;
import com.zhuzhu.allen.rxretrofitlib.listener.HttpOnNextListener;
import com.zhuzhu.allen.rxretrofitlib.utils.AppUtil;
import com.zhuzhu.allen.rxretrofitlib.utils.CookieDbUtil;

import java.lang.ref.SoftReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Observable;
import rx.Subscriber;

/**
 * 用于在http请求开始时，自动显示一个加载框
 * 请求结束时关闭
 * Created by allen on 2017/8/24.
 */

public class ProgressSubscriber<T> extends Subscriber<T> {
    //是否显示加载框
    private boolean showProgress = true;
    //软引用回调接口
    private SoftReference<HttpOnNextListener> mSubscriberOnNextListener;
    //软引用防止内存泄漏
    private SoftReference<RxAppCompatActivity> mActivity;
    //加载框对象——可以自定义
    private ProgressDialog dialog;
    //请求的封装数据
    private BaseApi baseApi;

    public ProgressSubscriber(BaseApi baseApi) {
        this.baseApi = baseApi;
        this.mSubscriberOnNextListener = baseApi.getListener();
        this.mActivity = new SoftReference<RxAppCompatActivity>(baseApi.getRxAppCompatActivity());
        setShowProgress(baseApi.isShowProgress());
        if (baseApi.isShowProgress()) {   //设置加载框显示的时候，初始化加载框
            initProgressDialog(baseApi.isCanCancelProgress());
        }
    }

    /**
     * 初始化加载框
     *
     * @param canCancelProgress 是否能取消显示加载框
     */
    private void initProgressDialog(boolean canCancelProgress) {
        Context context = mActivity.get();
        if (dialog == null && context != null) {
            dialog = new ProgressDialog(context);
            dialog.setCancelable(canCancelProgress);
            if (canCancelProgress) {
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        //触发联网取消动作
                        if (mSubscriberOnNextListener.get() != null) {
                            mSubscriberOnNextListener.get().onCancel();
                        }
                        onCancelProgress();
                    }
                });
            }
        }
    }

    /**
     * 取消加载框的时候取消对Observable的订阅，同时也取消了Http请求
     */
    private void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }

    /**
     * 显示加载框
     */
    private void showProgressDialog() {
        //没有设置显示进度框的话直接返回
        if (!isShowProgress()) return;
        Context context = mActivity.get();
        if (dialog == null || context == null) return;
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 隐藏加载框
     */
    private void dismissProgressDialog() {
        if (!isShowProgress()) return;
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 订阅开始时调用
     * 显示加载框
     */
    @Override
    public void onStart() {
        super.onStart();
        showProgressDialog();
        //需要缓存处理并且有网络
        if (baseApi.isCacheNeeded() && AppUtil.isNetworkAvailable(RxRetrofitApp.getApplication())) {
            //获取缓存数据
            CookieResult cookieResult = CookieDbUtil.getInstance().queryCookieBy(baseApi.getUrl());
            if (cookieResult != null) {
                //有缓存，检查缓存时间，看缓存是否可用
                long time = (System.currentTimeMillis() - cookieResult.getTime()) / 1000;
                if (time < baseApi.getCookieNetWorkTime()) {
                    //触发获取缓存数据监听
                    if (mSubscriberOnNextListener.get() != null) {
                        mSubscriberOnNextListener.get().onCacheNext(cookieResult.getResult());
                    }
                    //触发完成并解除订阅
                    onCompleted();
                    unsubscribe();
                }
            }
        }
    }

    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    @Override
    public void onError(Throwable e) {
        dismissProgressDialog();
        //需要缓存处理，并且本地有缓存数据
        if (baseApi.isCacheNeeded()) {
            Observable.just(baseApi.getUrl()).subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    //处理异常信息
                    dealError(e);
                }

                @Override
                public void onNext(String s) {
                    //获取缓存数据
                    CookieResult cookieResult = CookieDbUtil.getInstance().queryCookieBy(s);
                    if (cookieResult == null) {
                        throw new HttpResultException("网络错误");
                    }
                    long time = (System.currentTimeMillis() - cookieResult.getTime()) / 1000;
                    if (time < baseApi.getCookieNoNetWorkTime()) {
                        //触发获取缓存数据结果回调
                        if (mSubscriberOnNextListener.get() != null) {
                            mSubscriberOnNextListener.get().onCacheNext(cookieResult.getResult());
                        }
                    } else {
                        //缓存数据已经过期了，删除
                        CookieDbUtil.getInstance().deleteCookie(cookieResult);
                        throw new HttpResultException("网络错误");
                    }
                }
            });
        } else {
            dealError(e);
        }
    }

    /**
     * 将onNext方法中返回的结果交给Activity或者Fragment自己处理
     * @param t 创建Subscriber时候的泛型类型
     */
    @Override
    public void onNext(T t) {
        //触发请求结果回调
        if(mSubscriberOnNextListener.get() != null){
            mSubscriberOnNextListener.get().onNext(t);
        }
    }

    /**
     * 统一处理异常信息
     *
     * @param e 异常
     */
    private void dealError(Throwable e) {
        Context context = mActivity.get();
        if (context == null) return;
        if (e instanceof SocketTimeoutException) {
            Toast.makeText(context, "网络中断，请检查您的网络状态", Toast.LENGTH_SHORT).show();
        } else if (e instanceof ConnectException) {
            Toast.makeText(context, "网络中断，请检查您的网络状态", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "错误" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //触发错误回调
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onError(e);
        }
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }
}
