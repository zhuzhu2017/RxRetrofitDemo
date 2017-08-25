package com.zhuzhu.allen.rxretrofitlib.cookie;

import com.zhuzhu.allen.rxretrofitlib.utils.CookieDbUtil;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * gson持久化截取保存数据
 * Created by allen on 2017/8/23.
 */

public class CookieInterceptor implements Interceptor {
    //数据库处理缓存工具类
    private CookieDbUtil dbUtil;
    //是否需要缓存处理
    private boolean cacheNeeded;
    //缓存URL
    private String cacheUrl;

    public CookieInterceptor(boolean cacheNeeded, String url) {
        dbUtil = CookieDbUtil.getInstance();
        this.cacheNeeded = cacheNeeded;
        this.cacheUrl = url;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (cacheNeeded) {    //需要缓存处理了才进行本地数据处理
            ResponseBody body = response.body();
            if (body != null) {
                BufferedSource source = body.source();
                //设置缓存全部
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();
                //设置编码格式
                Charset charset = Charset.defaultCharset();
                MediaType contentType = body.contentType();
                if(contentType != null){
                    charset = contentType.charset(charset);
                }
                String bodyString = null;
                if(charset != null){
                    bodyString = buffer.clone().readString(charset);
                }
                //根据URL获取查询结果
                CookieResult cookieResult = dbUtil.queryCookieBy(cacheUrl);
                //获取当前时间
                long time = System.currentTimeMillis();
                //保存和更新本地数据
                if(cookieResult == null){   //数据库里面没有的话就保存，有的话就更新
                    cookieResult = new CookieResult(cacheUrl,bodyString,time);
                    dbUtil.saveCookie(cookieResult);
                }else {
                    cookieResult.setResult(bodyString);
                    cookieResult.setTime(time);
                    dbUtil.updateCookie(cookieResult);
                }
            }
        }
        return response;
    }
}
