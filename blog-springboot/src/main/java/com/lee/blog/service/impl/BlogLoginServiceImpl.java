package com.lee.blog.service.impl;

import com.lee.blog.dto.UserDetailsDto;
import com.lee.blog.entity.User;
import com.lee.blog.service.BlogLoginService;
import com.lee.blog.util.BeanCopyUtils;
import com.lee.blog.util.JwtUtils;
import com.lee.blog.util.RedisCache;
import com.lee.blog.vo.BlogUserLoginVo;
import com.lee.blog.vo.ResponseResult;
import com.lee.blog.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 博客登陆
 *
 * @author: zhicheng lee
 * @date: 2022/9/17 9:24
 */

@Service
public class BlogLoginServiceImpl implements BlogLoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //判断是否认证通过
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userid 生成token
        UserDetailsDto userDetailsDto = (UserDetailsDto) authenticate.getPrincipal();
        String userId = userDetailsDto.getUser().getId().toString();
        String jwt = JwtUtils.createJWT(userId);
        //把用户信息存入redis
        redisCache.setCacheObject("bloglogin:" + userId, userDetailsDto);

        //把token和userinfo封装 返回
        //把User转换成UserInfoVo
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(userDetailsDto.getUser(), UserInfoVo.class);
        BlogUserLoginVo vo = new BlogUserLoginVo(jwt, userInfoVo);
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult logout() {
        //获取token 解析获取userid
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto userDetailsDto = (UserDetailsDto) authentication.getPrincipal();
        //获取userid
        Long userId = userDetailsDto.getUser().getId();
        //删除redis中的用户信息
        redisCache.deleteObject("bloglogin:" + userId);
        return ResponseResult.okResult();
    }
}
