package com.ihouse.backend.interceptor;

import com.ihouse.backend.constants.JwtConstants;
import com.ihouse.backend.exception.AccessDeniedException;
import com.ihouse.backend.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AdminTokenInterceptor implements HandlerInterceptor {

    private JwtUtil jwtUtil=new JwtUtil();

    private boolean validateAdminToken(String token){
        return jwtUtil.validateAdminAccessToken(token);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization=request.getHeader(JwtConstants.header);
        if(authorization==null){
            throw new AccessDeniedException();
        }
        String accessToken = authorization.replace(JwtConstants.prefix, "");
        if(!validateAdminToken(accessToken)){
            throw new AccessDeniedException();
        }
        return true;
    }
}
