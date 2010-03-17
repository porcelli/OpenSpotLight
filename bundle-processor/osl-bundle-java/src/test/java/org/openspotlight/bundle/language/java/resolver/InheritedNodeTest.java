package org.openspotlight.bundle.language.java.resolver;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.bundle.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.common.concurrent.NeedsSyncronizationList;
import org.openspotlight.federation.context.DefaultExecutionContextFactory;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLInvalidQueryElementException;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryException;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;

public class InheritedNodeTest {

	@SuppressWarnings("unchecked")
	private <T extends SLNode> T findByProperty(final SLGraphSession session,
			final Class<T> type, final String propertyName,
			final String propertyValue) throws SLGraphSessionException,
			SLQueryException, SLInvalidQuerySyntaxException,
			SLInvalidQueryElementException {
		final SLQueryApi query1 = session.createQueryApi();
		query1.select().type(type.getName()).subTypes().selectEnd().where()
				.type(type.getName()).subTypes().each().property(propertyName)
				.equalsTo().value(propertyValue).typeEnd().whereEnd();
		final NeedsSyncronizationList<SLNode> result1 = query1.execute()
				.getNodes();
		if (result1.size() > 0) {
			synchronized (result1.getLockObject()) {
				for (final SLNode found : result1) {
					return (T) found;
				}
			}
		}

		final SLQueryApi query = session.createQueryApi();
		query.select().type(type.getName()).selectEnd().where().type(
				type.getName()).each().property(propertyName).equalsTo().value(
				propertyValue).typeEnd().whereEnd();
		final NeedsSyncronizationList<SLNode> result = query.execute()
				.getNodes();
		if (result.size() > 0) {
			synchronized (result.getLockObject()) {
				for (final SLNode found : result) {
					return (T) found;
				}
			}
		}

		return null;
	}

	private Repository repository = new Repository();
	{
		repository.setActive(true);
		repository.setName("name");
	}
	
	@Test
	public void shouldFindNodesByItsProperties() throws Exception {
		final ExecutionContextFactory factory = DefaultExecutionContextFactory
				.createFactory();
		final ExecutionContext context = factory.createExecutionContext("sa",
				"sa", DefaultJcrDescriptor.TEMP_DESCRIPTOR, repository);
		SLGraphSession graphSession = context.getGraphSession();
		JavaTypeClass newClass = graphSession.createContext("context")
				.getRootNode().addNode(JavaTypeClass.class, "newClass");
		newClass.setQualifiedName("qualifiedName");
		newClass.setSimpleName("simpleName");
		graphSession.save();
		context.closeResources();
		factory.closeResources();
		graphSession = context.getGraphSession();
		newClass = graphSession.getContext("context").getRootNode().addNode(
				JavaTypeClass.class, "newClass");
		Assert.assertThat(newClass.getQualifiedName(), Is.is("qualifiedName"));
		Assert.assertThat(newClass.getSimpleName(), Is.is("simpleName"));
		newClass = (JavaTypeClass) findByProperty(context.getGraphSession(),
				JavaType.class, "qualifiedName", "qualifiedName");
		Assert.assertThat(newClass.getQualifiedName(), Is.is("qualifiedName"));
		Assert.assertThat(newClass.getSimpleName(), Is.is("simpleName"));

	}

}
