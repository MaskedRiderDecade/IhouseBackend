package com.ihouse.backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;

@RequiredArgsConstructor
@Component
public class JwtUtil {
    //访问令牌签名算法
    public static final Key key= Keys.secretKeyFor(SignatureAlgorithm.HS512);
    //刷新令牌签名算法
    public static final Key refreshKey= Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public boolean validateAccessToken(String token){
        return validateToken(token,key,true);
    }

    public boolean validateWithoutExpiration(String token){
        return validateToken(token,key,false);
    }

    public boolean validateRefreshToken(String token){
        return validateToken(token,refreshKey,true);
    }

    public boolean validateToken(String token,Key key,boolean isExpiredInvalid){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parse(token);
            return true;
        }catch(ExpiredJwtException | SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e){
            if(e instanceof ExpiredJwtException){
                return !isExpiredInvalid;
            }
            return false;
        }

    }
}
