package com.chan.shiro.realm;

import com.chan.service.SearchRolesExecutor;
import com.chan.shiro.token.CtChanShiroToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Created by keyez on 2017/11/15.
 */
public class CtChanShrioRealm extends AuthorizingRealm {
    private static final String host;
    private static final String domain;
    private static final String url;
    private static final String URL_BASE = "ldap://@@url@@";

    private static class Principal {
        private String user;
        private String OU;

        public String getOU() {
            return OU;
        }

        public Principal setOU(String OU) {
            this.OU = OU;
            return this;
        }

        public String getUser() {
            return user;
        }

        public Principal setUser(String user) {
            this.user = user;
            return this;
        }
    }

    public SearchRolesExecutor searchRolesExecutor;

    static {
        host = "192.168.0.27";
        domain = "@messcat.cc";
        url = URL_BASE.replaceAll("@@url@@", host);
    }


    private Class<CtChanShiroToken> supportsTokenClass = CtChanShiroToken.class;

    @Override
    public String getName() {
        return CtChanShrioRealm.class.getName();
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && supportsTokenClass.isAssignableFrom(token.getClass());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        LdapContext ctx = null;
        try {
            //获取用户登录用Token
            CtChanShiroToken ctToken = (CtChanShiroToken) token;
            //组合为登录AD域的用户
            String user = ctToken.getPrincipal().indexOf(domain) > 0 ? ctToken.getPrincipal() : ctToken.getPrincipal() + domain;
            //执行AD域登录,并获取登录上下文
            ctx = loginLdapContext(user, ctToken.getCredentials());
            //获取分组信息
            String OU = searchUserOU(ctx, user);
            System.out.println(OU);
            return new SimpleAuthenticationInfo(new Principal().setOU(OU).setUser(user), ctToken.getCredentials(), getName());
        } catch (Exception e) {
            System.out.println("登录失败!");
            e.printStackTrace();
        } finally {
            if (null != ctx) {
                try {
                    ctx.close();
                    ctx = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String searchUserOU(LdapContext ctx, String user) throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String searchFilter = "(&(|(objectClass=user)(objectClass=organizationalUnit)(objectClass=group))(|(cn=" + user + ")(dn=" + user + ")(sAMAccountName=" + user + ")(userPrincipalName=" + user + ")))";
        String searchBase = "DC=messcat,DC=cc";
        String returnAttrs[] = {};
        searchControls.setReturningAttributes(returnAttrs);
        NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter, searchControls);
        String OU = null;
        while (answer.hasMoreElements()) {
            SearchResult sr = answer.next();
            OU = getOU(sr.getName());
        }
        return OU;
    }


    private LdapContext loginLdapContext(String principal, String credentials) throws NamingException {
        //todo CtChan:验证逻辑;
        Hashtable<String, String> env = new Hashtable<>();
        LdapContext ctx = null;
        //todo:这些参数暂时不配置在property中.
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, principal);
        env.put(Context.SECURITY_CREDENTIALS, credentials);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);
        return new InitialLdapContext(env, null);

    }

    private boolean isLegal(String[] user, String principal, String credentials) {
        if (user[0].equals(principal) && user[1].equals(credentials)) {
            return true;
        }
        return false;
    }

    private String getOU(String path) {
        String OU = "";
        for (String item : path.split(",")) {
            if (!item.contains("OU=")) {
                continue;
            }
            OU = item.substring(item.indexOf('=') + 1);
        }
        return OU;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println(principals);
        CtChanShrioRealm.Principal principal = (Principal) principals.getPrimaryPrincipal();
        String OU = principal.getOU();
        //获取系统权限
        List roles = null;
        if (searchRolesExecutor != null) {
            roles = searchRolesExecutor.searchRoles(OU);
        }
        //略...
        return null;
    }
}