package workaround;

import java.text.Collator;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.core.TransientRepository;
import org.openspotlight.common.util.StringBuilderUtil;

public class PropertyDoesNotExist {

    public static void main( String[] args ) {
        try {

            Repository repo = new TransientRepository();
            Session session = repo.login(new SimpleCredentials("user", "pw".toCharArray()));
            Node root = session.getRootNode();
            Node hello = null;

            try {
                hello = root.getNode("hello");
                hello.remove();
            } catch (PathNotFoundException e) {
            }

            Collator collator = Collator.getInstance(Locale.US);
            collator.setStrength(Collator.PRIMARY);

            byte[] arr = collator.getCollationKey("java.util.Set").toByteArray();
            String s = new String(arr);

            System.out.println(arr);

            hello = root.addNode("hello");
            hello.setProperty("caption", s);
            //hello.setProperty("prop1", "test");
            //hello.setProperty("prop2", "test");

            session.save();

            StringBuilder buffer = new StringBuilder();
            StringBuilderUtil.append(buffer, "jcr:like(@", "caption", ", '%", new String(collator.getCollationKey("SET").toByteArray()), "%')");

            String xpath = "//hello[" + buffer.toString() + "]";
            System.out.println(xpath);
            Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
            QueryResult result = query.execute();
            NodeIterator iter = result.getNodes();
            while (iter.hasNext()) {
                System.out.println(iter.nextNode().getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
