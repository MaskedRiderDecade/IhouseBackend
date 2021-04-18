package com.ihouse.backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtUtil {
    //访问令牌签名算法
    public static final Key key= Keys.hmacShaKeyFor(Decoders.BASE64.decode("cuAihCz53DZGundam001sGcZJ2Ai6AUnicornuphtJMsk7iQ="));
    //刷新令牌签名算法
    public static final Key refreshKey= Keys.hmacShaKeyFor(Decoders.BASE64.decode("cuAihCz53DZGundam001sGcZJ2Ai6AUnicornuphtJMsk7iQ="));

    public boolean validateWithoutExpiration(String token){
        return validateToken(token,key,false);
    }

    public boolean validateAccessToken(String token){
        return validateToken(token,key,true);
    }

    public boolean validateAdminAccessToken(String token){
        return validateAdminToken(token,key,true);
    }

    public boolean validateAdminWithoutExpiration(String token){
        return validateAdminToken(token,key,false);
    }

    public boolean validateRefreshToken(String token){
        return validateToken(token,refreshKey,true);
    }

    public boolean validateToken(String token,Key key,boolean isExpiredInvalid){
        try{
//            Jwts.parserBuilder().setSigningKey(key).build().parse(token);
            Claims body= (Claims) Jwts.parserBuilder().setSigningKey(key).build().parse(token).getBody();
            List<String> authorities= (List<String>) body.get("authorities");
            for(String authority:authorities){
                log.info(authority+"\n");
            }
            return true;
        }catch(ExpiredJwtException |SignatureException| MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e){
            if(e instanceof ExpiredJwtException){
                return !isExpiredInvalid;
            }
            return false;
        }

    }

    public boolean validateAdminToken(String token,Key key,boolean isExpiredInvalid){
        try{
            Jwt jwt=Jwts.parserBuilder().setSigningKey(key).build().parse(token);
            Claims body= (Claims) jwt.getBody();
            List<String> authorities= (List<String>) body.get("authorities");
            if(authorities!=null&&authorities.contains("ROLE_ADMIN")){
                return true;
            }
            return  false;
        }catch(ExpiredJwtException |SignatureException| MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e){
            if(e instanceof ExpiredJwtException){
                return !isExpiredInvalid;
            }
            return false;
        }
    }

    public String getUsername(String token){
        try{
            Jwt jwt=Jwts.parserBuilder().setSigningKey(key).build().parse(token);
            Claims body= (Claims) jwt.getBody();
            return body.getSubject();
        }catch(ExpiredJwtException | SignatureException| MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e){
            return null;
        }
    }
}
