package org.openspotlight.jcr.provider.test;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.common.util.JCRUtil;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

@Ignore
public class JcrVersioningTest {

	private static final String VERSIONABLE_NODE = "newVersionableNode";
	private static JcrConnectionProvider provider;

	@BeforeClass
	public static void setup() throws Exception {
		JcrVersioningTest.provider = JcrConnectionProvider
				.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		final Session session = JcrVersioningTest.provider.openSession();
		Node newVersionableNode;
		try {
			newVersionableNode = session.getRootNode().getNode(
					JcrVersioningTest.VERSIONABLE_NODE);
		} catch (final PathNotFoundException e) {
			newVersionableNode = session.getRootNode().addNode(
					JcrVersioningTest.VERSIONABLE_NODE);
			JCRUtil.makeReferenceable(newVersionableNode);
			JCRUtil.makeVersionable(newVersionableNode);
			session.save();
			newVersionableNode.checkin();
		}
	}

	@Test
	public void shouldCommitOnlyOneVersion() throws Exception {
		Assert.fail();

	}

	@Test
	public void shouldCommitTwoVersions() throws Exception {
		// Session session = provider.openSession();
		Assert.fail();

	}

	@Test
	public void shouldCreateTwoVersionsIndepententOnEachOther()
			throws Exception {
		Assert.fail();

	}

	@Test
	public void shouldDismissTwoVersions() throws Exception {
		Assert.fail();
	}

	@Test
	public void shouldMergeTwoCorrelatedChanges() throws Exception {
		Assert.fail();
	}

}