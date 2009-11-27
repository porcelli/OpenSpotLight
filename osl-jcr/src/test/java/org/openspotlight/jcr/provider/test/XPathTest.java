package org.openspotlight.jcr.provider.test;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.hamcrest.core.Is;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class XPathTest {

	private static JcrConnectionProvider provider;
	private static Session session;

	@AfterClass
	public static void close() throws Exception {
		XPathTest.session.logout();
	}

	@BeforeClass
	public static void setup() throws Exception {
		XPathTest.provider = JcrConnectionProvider
				.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		XPathTest.provider.openRepository();
		XPathTest.session = XPathTest.provider.openSession();

	}

	@Test
	public void shouldExecuteXPath() throws Exception {
		final Node rootNode = XPathTest.session.getRootNode();
		rootNode.addNode("abc");
		XPathTest.session.save();
		final QueryResult result = XPathTest.session.getWorkspace()
				.getQueryManager().createQuery("abc", Query.XPATH).execute();
		Assert.assertThat(result.getNodes().hasNext(), Is.is(true));

	}

}
