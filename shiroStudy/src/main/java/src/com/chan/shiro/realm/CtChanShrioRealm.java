package com.chan.shiro.realm;

import com.chan.shiro.token.CtChanChildToken;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;
import com.chan.shiro.token.CtChanShiroToken;

/**
 * Created by keyez on 2017/11/15.
 */
public class CtChanShrioRealm implements Realm {


    private Class<? extends CtChanShiroToken> supportsTokenClass = CtChanShiroToken.class;

    @Override
    public String getName() {
        return CtChanShrioRealm.class.getName();
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && supportsTokenClass.isAssignableFrom(token.getClass());
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        CtChanShiroToken ctToken = (CtChanShiroToken) token;
        if (!verify(ctToken.getPrincipal(), ctToken.getCredentials())) {
            return null;
        }
        return new SimpleAuthenticationInfo(ctToken.getPrincipal(), ctToken.getCredentials(), getName());
    }

    private String[][] users = {{"xiaoming", "111111"}, {"xiaohong", "222222"}};

    public boolean verify(String principal, String credentials) {
        for (String[] user : users) {
            if (isLegal(user, principal, credentials)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLegal(String[] user, String principal, String credentials) {
        if (user[0].equals(principal) && user[1].equals(credentials)) {
            return true;
        }
        return false;
    }

}
