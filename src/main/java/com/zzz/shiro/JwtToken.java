package com.zzz.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author zzz
 * @date 2022/3/1 13:43
 */

public class JwtToken implements AuthenticationToken {

    // 密钥
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}