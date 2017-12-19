import org.apache.log4j.Logger;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.*;


/**
 * Created by keyez on 2017/11/23.
 */
public class test {
    private static Logger logger = Logger.getLogger(test.class);

    @Test
    public void test() {
        logger.info("OK");
    }

    public static void main(String[] args) {
        logger.info("OK");
    }

    @Test
    public void getUserAndGroupInfoDemo() throws NamingException {
        //登录用..
        String host = "192.168.0.27";
        String domain = "@messcat.cc";
        String url = "ldap://@@url@@".replaceAll("@@url@@", host);
        String username = "OsbornChen";
        String password = "Keyblade520";
        //获取用户登录用Token

        //组合为登录AD域的用户
        String user = username.indexOf(domain) > 0 ? username : username + domain;
        //执行AD域登录,并获取登录上下文
        LdapContext ctx = loginLdapContext(user, password, url);
        //todo : 主要..

        //获取分组信息
        Map<String, List<String>> userMap = searchGroup(ctx, user, url, InputValue.ByUesr);
        Map<String, List<String>> groupMap = searchGroup(ctx, user, url, InputValue.ByGroup);

        //todo : end
        System.out.println(userMap);
        System.out.println(groupMap);
    }

    public enum InputValue {
        ByUesr {
            @Override
            public void put(Map<String, List<String>> map, String user, String group) {
                putToMap(map, user, group);
            }
        }, ByGroup {
            @Override
            public void put(Map<String, List<String>> map, String user, String group) {
                putToMap(map, group, user);
            }
        };

        abstract void put(Map<String, List<String>> map, String user, String group);

        private static void putToMap(Map<String, List<String>> groups, String key, String value) {
            List<String> list = groups.get(key) == null ? new ArrayList<>() : groups.get(key);
            if (list.indexOf(value) < 0) {
                list.add(value);
            }
            groups.put(key, list);
        }
    }

    public Map<String, List<String>> searchGroup(LdapContext ctx, String user, String url, InputValue inputValue) throws NamingException {

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String searchFilter = "(objectClass=person)";
        String searchBase = "DC=messcat,DC=cc";
        String returnAttrs[] = {"memberOf", "CN", "sAMAccountName"};
        searchControls.setReturningAttributes(returnAttrs);
        NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter, searchControls);
        return getMap(answer, inputValue);
    }

    private LdapContext loginLdapContext(String principal, String credentials, String url) throws NamingException {
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

    private static Map<String, List<String>> getMap(NamingEnumeration<SearchResult> answer, InputValue inputValue) throws NamingException {
        Map<String, List<String>> userMap = new HashMap<>();
        while (answer.hasMoreElements()) {
            SearchResult searchResult = answer.next();
            if (searchResult.getAttributes().get("memberOf") == null) {
                continue;
            }
            String user = (String) searchResult.getAttributes().get("sAMAccountName").get();
            NamingEnumeration<String> elements = (NamingEnumeration<String>) searchResult.getAttributes().get("memberOf").getAll();
            while (elements.hasMoreElements()) {
                String item = elements.next();
                inputValueIntoMap(userMap, item, user, inputValue);
            }
        }
        return userMap;
    }

    private static final String CN_START_BASE = "CN=";

    private static void inputValueIntoMap(Map<String, List<String>> map, String item, String user, InputValue inputValue) {
        String[] strs = item.split(",");
        String group = "";
        for (String str : strs) {
            if (str.trim().startsWith(CN_START_BASE)) {
                group = str.substring(str.indexOf(CN_START_BASE) + CN_START_BASE.length());
                inputValue.put(map, user, group);
                break;
            }
        }
    }


}
