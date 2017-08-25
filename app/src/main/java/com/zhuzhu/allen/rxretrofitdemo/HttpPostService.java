package com.zhuzhu.allen.rxretrofitdemo;

import com.zhuzhu.allen.rxretrofitlib.base.BaseResultEntity;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 测试接口
 * Created by allen on 2017/8/24.
 */

public interface HttpPostService {
    @FormUrlEncoded
    @POST("AppFiftyToneGraph/videoLink")
    Observable<BaseResultEntity<List<SubjectResult>>> getAllVideoBy(@Field("once") boolean once);
}
