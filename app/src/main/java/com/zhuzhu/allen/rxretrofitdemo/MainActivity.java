package com.zhuzhu.allen.rxretrofitdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhuzhu.allen.rxretrofitdemo.api.SubjectResultApi;
import com.zhuzhu.allen.rxretrofitlib.base.BaseResultEntity;
import com.zhuzhu.allen.rxretrofitlib.listener.HttpOnNextListener;
import com.zhuzhu.allen.rxretrofitlib.manager.HttpManager;

import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends RxAppCompatActivity {

    private Button btnGetData;
    private TextView tvShowData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGetData = (Button) findViewById(R.id.btn_getData);
        tvShowData = (TextView) findViewById(R.id.tv_showData);
        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleGetData();
            }
        });
    }

    /**
     * 获取数据
     */
    private void simpleGetData() {
        SubjectResultApi resultApi = new SubjectResultApi(listener, this);
        resultApi.setAll(true);
        HttpManager manager = HttpManager.getInstance();
        manager.connToServer(resultApi);
    }

    /*监听回调*/
    HttpOnNextListener listener = new HttpOnNextListener<List<SubjectResult>>() {

        @Override
        public void onNext(List<SubjectResult> subjectResults) {
            tvShowData.setText("网络返回：\n" + subjectResults.toString());
            Log.d("MainActivity","onNext");
        }

        @Override
        public void onCacheNext(String cache) {
            super.onCacheNext(cache);
            Log.d("MainActivity","onCacheNext");
            /*缓存回调*/
            Gson gson = new Gson();
            Type type = new TypeToken<BaseResultEntity<List<SubjectResult>>>() {
            }.getType();
            BaseResultEntity resultEntity = gson.fromJson(cache, type);
            tvShowData.setText("缓存返回：\n" + resultEntity.getData().toString());
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            Log.d("MainActivity","onError");
            tvShowData.setText("失败：\n" + e.toString());
        }

        @Override
        public void onCancel() {
            super.onCancel();
            Log.d("MainActivity","onCancel");
            tvShowData.setText("取消请求");
        }

    };
}
