package com.youwu.tool.app;

import com.youwu.tool.bean.TencentBean;

import java.util.Map;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseBean;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiServices {
    //post请求:第一个参数url，可以传递为空，不能不写，第二个map集合为参数
    @POST
    Observable<BaseBean> postPage(@Url String url, @QueryMap Map<String,String> map);
    //get请求:直接把参数拼接成url即可
    @GET
    Observable<BaseBean<TencentBean>> getPage(@Url String url);
}