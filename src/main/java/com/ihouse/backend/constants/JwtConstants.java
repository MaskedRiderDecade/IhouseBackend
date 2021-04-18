package com.ihouse.backend.constants;

public class JwtConstants {

    public static String header="Authorization";
    public static String prefix="Bearer ";
    //访问令牌过期时间
    public static Long accessTokenExpireTime=60_00L;
    //刷新令牌过期时间
    public static Long refreshTokenExpireTime=30*24*60*3600L;

}
