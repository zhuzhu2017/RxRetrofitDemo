package com.zhuzhu.allen.rxretrofitlib.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zhuzhu.allen.rxretrofitlib.RxRetrofitApp;
import com.zhuzhu.allen.rxretrofitlib.cookie.CookieResult;
import com.zhuzhu.allen.rxretrofitlib.cookie.CookieResultDao;
import com.zhuzhu.allen.rxretrofitlib.cookie.DaoMaster;
import com.zhuzhu.allen.rxretrofitlib.cookie.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 数据缓存，实现增删改查
 * 数据库工具--greendao使用
 * Created by allen on 2017/8/23.
 */

public class CookieDbUtil {
    private static CookieDbUtil db;
    private static final String dbName = "http_cache_db";
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public CookieDbUtil() {
        context = RxRetrofitApp.getApplication();
        openHelper = new DaoMaster.DevOpenHelper(context, dbName);
    }

    /**
     * 获取单例
     *
     * @return 返回CookieDBUtil单例对象
     */
    public static CookieDbUtil getInstance() {
        if (db == null) {
            synchronized (CookieDbUtil.class) {
                if (db == null) {
                    db = new CookieDbUtil();
                }
            }
        }
        return db;
    }

    /**
     * 获取可读数据库
     *
     * @return readable database
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName);
        }
        return openHelper.getReadableDatabase();
    }

    /**
     * 获取可写数据库
     *
     * @return writable database
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName);
        }
        return openHelper.getWritableDatabase();
    }

    /**
     * 保存数据对象
     *
     * @param cookieResult 需要保存的数据对象
     */
    public void saveCookie(CookieResult cookieResult) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        CookieResultDao cookieResultDao = daoSession.getCookieResultDao();
        cookieResultDao.insert(cookieResult);
    }

    /**
     * 更新保存的数据对象
     *
     * @param cookieResult 新的数据对象
     */
    public void updateCookie(CookieResult cookieResult) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        CookieResultDao cookieResultDao = daoSession.getCookieResultDao();
        cookieResultDao.update(cookieResult);
    }

    /**
     * 删除对应的数据对象
     *
     * @param cookieResult 需要删除的数据对象
     */
    public void deleteCookie(CookieResult cookieResult) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        CookieResultDao cookieResultDao = daoSession.getCookieResultDao();
        cookieResultDao.delete(cookieResult);
    }

    /**
     * 根据URL查询对应的CookieResult对象
     *
     * @param url 条件URL
     * @return 查询的数据对象
     */
    public CookieResult queryCookieBy(String url) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        CookieResultDao cookieResultDao = daoSession.getCookieResultDao();
        QueryBuilder<CookieResult> qb = cookieResultDao.queryBuilder();
        qb.where(CookieResultDao.Properties.Url.eq(url));
        List<CookieResult> list = qb.list();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * 查询所有数据对象
     *
     * @return 查询得到的数据对象集合
     */
    public List<CookieResult> queryAllCookie() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        CookieResultDao cookieResultDao = daoSession.getCookieResultDao();
        return cookieResultDao.queryBuilder().list();
    }

}
