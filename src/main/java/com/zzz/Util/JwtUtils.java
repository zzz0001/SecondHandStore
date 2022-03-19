package com.zzz.Util;

/**
 * @author zzz
 * @date 2022/3/1 13:44
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * jwt工具类，实现前后端分离的Token
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "zzz.jwt")
public class JwtUtils {

    private String secret;
    private long expire;
    private String header;

    /**
     * 生成jwt token
     */
    public String CreateToken(long userId) {
        Date nowDate = new Date();
        //过期时间，因为是毫秒为单位，所以过期时间需要乘1000
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userId+"")
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

    }

    public Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            log.debug("validate is token error ", e);
            return null;
        }
    }

    public Long getStudentId(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String studentId = null;
        try {
            studentId = getClaimByToken(token).getSubject();
        } catch (Exception e) {
            throw new ExpiredCredentialsException("token已失效，请重新登录！");
        }
        return Long.valueOf(studentId);
    }

    /**
     * token是否过期
     * @return  true：过期
     */
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }
}