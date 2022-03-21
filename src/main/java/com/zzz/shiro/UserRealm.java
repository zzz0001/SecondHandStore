package com.zzz.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.zzz.Util.JwtUtils;
import com.zzz.pojo.entity.User;
import com.zzz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;

/**
 *	Realm配置类：负责认证和授权的逻辑
 */
@Slf4j
@Component
public class UserRealm extends AuthorizingRealm {

    @Resource
    JwtUtils jwtUtils;

    @Resource
    private UserService userService;

    // 修改Shiro默认的token格式，使用JWT实现Token
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    // 进行授权操作
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Subject subject = SecurityUtils.getSubject();
        UserProfile userProfile = (UserProfile) subject.getPrincipal();
        User user = userService.getById(userProfile.getStudentId());
        HashSet<String> roles = new HashSet<>();
        roles.add(user.getRoles());
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo(roles);
        authorizationInfo.addStringPermission(user.getPerms());
        return authorizationInfo;
    }

    /*
        当controller中调用了 subject.login(token)时，会自动调用该方法，实现用户的认证操作。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtToken jwt = (JwtToken) authenticationToken;
        log.info("jwt------{}", jwt);
        String studentId = jwtUtils.getClaimByToken((String) jwt.getPrincipal()).getSubject();
        log.info("StudentId:{} 正在登录...",studentId);
        User user = userService.getById(Long.parseLong(studentId));
        if(user == null) {
            throw new UnknownAccountException("账户不存在！");
        }
        if(user.getStatus() == null || user.getStatus() == 1) {
            throw new LockedAccountException("账户已被锁定！");
        }

        log.info("用户登录成功------{}",user);

        UserProfile userProfile = new UserProfile();
        BeanUtil.copyProperties(user,userProfile);
        // 可以通过 SecurityUtils.getSubject().getPrincipal() 获得userProfile 对象
        // UserProfile实体类包括 User对象的基本属性，不应该包括密码等敏感信息
        return new SimpleAuthenticationInfo(userProfile, jwt.getCredentials(), getName());
    }
}