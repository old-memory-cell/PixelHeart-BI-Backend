package com.zzsd.inolibi.interceptor;


import com.zzsd.inolibi.utils.CurrentHolder;
import com.zzsd.inolibi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 令牌拦截器
 */
@Slf4j
@Component
public class TokenIntercepter implements HandlerInterceptor {

    /**
     * 方法执行之后的逻辑
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //删除线程当中存储的员工id
        CurrentHolder.remove();
    }

    // 方法执行之前的逻辑
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1 .获取请求头中的令牌

        String token = request.getHeader("token");
        //2 .判断令牌是否存在，如果不存在响应401状态码
        if (token == null || token.isEmpty()) {
            log.info("请求头中不存在token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        //3 .解析token，如果解析失败，响应401状态码
        try {
            Claims claims = JwtUtils.parseJWT(token);
            log.info("令牌合法");
            //获取当前登录员工的id
            Integer userId = Integer.valueOf(claims.get("id").toString());
            CurrentHolder.setCurrentId(userId);
        } catch (Exception e) {
            //解析失败
            log.info("解析非法");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        //4 .解析成功，放行
        log.info("令牌解析成功，放行");
        return true;
    }
}
