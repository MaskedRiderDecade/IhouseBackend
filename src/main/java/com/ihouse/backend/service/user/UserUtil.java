package com.ihouse.backend.service.user;

import com.ihouse.backend.constants.JwtConstants;
import com.ihouse.backend.domain.User;
import com.ihouse.backend.exception.DataNotFoundException;
import com.ihouse.backend.repository.UserRepo;
import com.ihouse.backend.util.JwtUtil;
import com.ihouse.backend.vo.UserVo;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class UserUtil {

    @Autowired
    private UserRepo userRepo;

    private JwtUtil jwtUtil=new JwtUtil();

    public UserVo getUser(String token){
        String accessToken = token.replace(JwtConstants.prefix, "");
        String username=jwtUtil.getUsername(accessToken);
        if(username!=null){
            User user=userRepo.findByUsername(username);
            if(user!=null){
                return UserVo.builder()
                        .username(user.getUsername())
                        .mobile(user.getMobile())
                        .email(user.getEmail())
                        .name(user.getName())
                        .build();
            }
        }
        return null;
    }

    public Long getUserId(String token){
        String accessToken = token.replace(JwtConstants.prefix, "");
        String username=jwtUtil.getUsername(accessToken);
        if(username!=null){
            User user=userRepo.findByUsername(username);
            if(user!=null){
                return user.getId();
            }
        }
        return null;
    }

    public String getUsername(String token){
        String accessToken = token.replace(JwtConstants.prefix, "");
        return jwtUtil.getUsername(accessToken);
    }

    public User getUserById(Long userId){
        return userRepo.findById(userId).orElseThrow(()->{
            throw new DataNotFoundException();
        });
    }
}
