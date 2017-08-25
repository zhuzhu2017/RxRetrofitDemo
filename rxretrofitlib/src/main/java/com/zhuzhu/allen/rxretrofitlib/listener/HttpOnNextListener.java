package com.zhuzhu.allen.rxretrofitlib.listener;

import rx.Observable;

/**
 * 请求结果回调处理
 * Created by allen on 2017/8/23.
 */

public abstract class HttpOnNextListener<T> {
    /**
     * 成功后回调方法
     *
     * @param t 返回的请求对象
     */
    public abstract void onNext(T t);

    /**
     * 缓存回调结果
     *
     * @param cache 缓存内容
     */
    public void onCacheNext(String cache) {
    }

    /**
     * 成功后的Observable返回，扩展链式调用
     *
     * @param observable 返回的observable，供链式调用
     */
    public void onNext(Observable observable) {
    }

    /**
     * 失败或者错误的回调
     *
     * @param e 错误对象
     */
    public void onError(Throwable e) {
    }

    /**
     * 用户取消的回调
     */
    public void onCancel() {
    }
}
