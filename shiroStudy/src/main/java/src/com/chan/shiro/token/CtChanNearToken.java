package com.chan.shiro.token;

import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;


/**
 * 这是第一个尝试性编写的 ShiroToken 类
 * 实现了
 * {@link HostAuthenticationToken},{@link RememberMeAuthenticationToken}
 * 分别用于确认是否执行 会话发起地址记录,和会话跟踪服务
 *
 * @author keyez
 */
public class CtChanNearToken implements HostAuthenticationToken, RememberMeAuthenticationToken {
    private String username;
    private String password;
    private boolean isRememberMe = false;

    /**
     * 暂时不知道要干嘛
     *
     * @return
     */
    @Override
    public String getHost() {
        return null;
    }

    /**
     * 设置是否进行会话跟踪
     *
     * @return
     */
    @Override
    public boolean isRememberMe() {
        return this.isRememberMe;
    }

    /**
     * 区分登录用户的标识,俗称用户名
     */
    @Override
    public String getPrincipal() {
        return username;
    }

    /**
     * 用户进行用户标识认证的方法,要与服务器端产生的验证一直.才算登录成功
     */
    @Override
    public String getCredentials() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRememberMe(boolean rememberMe) {
        isRememberMe = rememberMe;
    }
}
