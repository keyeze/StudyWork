package src.com.chan.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.Realm;
import src.com.chan.shiro.token.CtChanShiroToken;

/**
 * Created by keyez on 2017/11/15.
 */
public class CtChanShrioRealm implements Realm {
    private Class<? extends CtChanShiroToken> supportsTokenClass;

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
        return null;
    }
}
