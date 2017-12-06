import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class TestDemo3 {
    public static void main(String args[]) {
        Hashtable HashEnv = new Hashtable();
        String LDAP_URL = "ldap://192.168.0.27"; // LDAP访问地址
        String adminName = "OsbornChen@messcat.cc"; // 注意用户名的写法：domain\User 或
        // User@domain.com
        String adminPassword = "Abc123_&"; // 密码
        HashEnv.put(Context.SECURITY_AUTHENTICATION, "simple"); // LDAP访问安全级别
        HashEnv.put(Context.SECURITY_PRINCIPAL, adminName); // AD User
        HashEnv.put(Context.SECURITY_CREDENTIALS, adminPassword); // AD Password
        HashEnv.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory"); // LDAP工厂类
        HashEnv.put(Context.PROVIDER_URL, LDAP_URL);

        try {
            LdapContext ctx = new InitialLdapContext(HashEnv, null);

            // Create the search controls
            SearchControls searchCtls = new SearchControls();

            // Specify the search scope
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // specify the LDAP search filter
            String searchFilter = "(&(|(objectClass=user)(objectClass=organizationalUnit)(objectClass=group))(|(cn=" + adminName + ")(dn=" + adminName + ")(sAMAccountName=" + adminName + ")(userPrincipalName=" + adminName + ")))";
            // Specify the Base for the search 搜索域节点
            String searchBase = "DC=messcat,DC=cc";

            int totalResults = 0;
            String returnedAtts[] = {"url", "whenChanged", "employeeID",
                    "name",
                    "userPrincipalName",
                    "physicalDeliveryOfficeName",
                    "departmentNumber", "telephoneNumber", "homePhone",
                    "mobile", "department",
                    "sAMAccountName",
                    "whenChanged",
                    "mail", "path"}; // 定制返回属性
            searchCtls.setReturningAttributes(returnedAtts); // 设置返回属性集

            // Search for objects using the filter
            NamingEnumeration answer = ctx.search(searchBase, searchFilter, searchCtls);
            if (answer == null || answer.equals(null)) {
                System.out.println("answer is null");
            } else {
                System.out.println("answer not null");
            }
            System.out.println(answer.hasMoreElements());
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                System.out.println(sr.getName());
                System.out.println(getOU(sr.getName()));
                Attributes Attrs = sr.getAttributes();
                if (Attrs != null) {
                    try {
                        for (NamingEnumeration ne = Attrs.getAll(); ne.hasMore(); ) {
                            Attribute Attr = (Attribute) ne.next();
                            System.out.println(" AttributeID=" + Attr.getID().toString());
                            // 读取属性值
                            for (NamingEnumeration e = Attr.getAll(); e
                                    .hasMore(); totalResults++) {
                                System.out.println(" AttributeValues="
                                        + e.next().toString());
                            }
                            System.out.println(" ---------------");
                            // 读取属性值
                            Enumeration values = Attr.getAll();
                            if (values != null) { // 迭代
                                while (values.hasMoreElements()) {
                                    System.out.println(" AttributeValues="
                                            + values.nextElement());
                                }
                            }
                            System.out.println(" ---------------");
                        }
                    } catch (NamingException e) {
                        System.err.println("Throw Exception : " + e);
                    }
                }
            }
            System.out.println("Number: " + totalResults);
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
            System.err.println("Throw Exception : " + e);
        }
    }

    public static String getOU(String path) {
        String OU = "";
        for (String item : path.split(",")) {
            if (!item.contains("OU=")) {
                continue;
            }
            OU = item.substring(item.indexOf('=') + 1);
        }
        return OU;
    }
}