package com.example.administrator.myinstaapp.Services;


import com.example.administrator.myinstaapp.Pojo.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2/22/2016.
 */
public interface GetTokenService {

//    @POST("/oauth/access_token")
//      Call<TokenResponse> getAccessToken(@Body TokenRequest tokenRequest); //so this returns a token response and we need to build a pojo for that.

    @FormUrlEncoded
    @POST("/oauth/access_token")
    Call<TokenResponse> getAccessToken(@Field("client_id") String client_id, @Field("client_secret") String client_secret,
                                       @Field("redirect_uri") String redirect_uri, @Field("grant_type") String grant_type,
                                       @Field("code") String code);
}
