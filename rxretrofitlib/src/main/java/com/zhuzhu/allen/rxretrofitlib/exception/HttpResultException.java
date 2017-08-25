package com.zhuzhu.allen.rxretrofitlib.exception;

/**
 * 自定义错误信息，统一处理服务器端返回的异常信息
 * Created by allen on 2017/8/23.
 */

public class HttpResultException extends RuntimeException {
    /*无数据标志*/
    private static final int NO_DATA = 0x2;

    /**
     * 根据返回的状态码处理异常信息
     *
     * @param resultCode 状态码
     */
    public HttpResultException(int resultCode) {
        this(getApiEcceptionMessage(resultCode));
    }

    /**
     * 错误信息直接处理
     *
     * @param resultMessage 出错信息
     */
    public HttpResultException(String resultMessage) {
        super(resultMessage);
    }

    /**
     * 根据错误码返回异常信息，此处可以自定义不同状态码的异常信息返回
     *
     * @param code 状态码
     * @return 异常信息
     */
    private static String getApiEcceptionMessage(int code) {
        String msg = "";
        switch (code) {
            case NO_DATA:
                msg = "无数据";
                break;
            default:
                msg = "error";
                break;
        }
        return msg;
    }
}
