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
public class CtChanChildToken extends CtChanShiroToken {
}

