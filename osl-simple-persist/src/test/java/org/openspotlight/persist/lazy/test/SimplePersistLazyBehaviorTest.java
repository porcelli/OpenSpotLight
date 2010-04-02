package org.openspotlight.persist.lazy.test;

import org.junit.Test;
import org.openspotlight.common.LazyType;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.provider.SessionWithLock;
import org.openspotlight.persist.internal.LazyProperty;
import org.openspotlight.persist.support.SimplePersistSupport;

import javax.jcr.Node;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class SimplePersistLazyBehaviorTest {

	@Test
	public void shouldLoadSavedValue() throws Exception {
		final JcrConnectionProvider provider = JcrConnectionProvider
		.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		final SessionWithLock session = provider.openSession();
		try {
			ClassWithLazyProperty bean = new ClassWithLazyProperty();
			bean.setTest("test");

			bean.setBigPojoProperty(LazyProperty.Factory
					.<SerializablePojoProperty> create(bean));
			bean.getBigPojoProperty().setTransient(
					new SerializablePojoProperty());
			bean.getBigPojoProperty().get(session).setAnotherProperty("test");
			assertThat(bean.getBigPojoProperty().getMetadata().getTransient(),
					is(notNullValue()));
			final Node node = SimplePersistSupport.convertBeanToJcr("a/b/c",
					session, bean);
			session.save();
			bean = SimplePersistSupport.convertJcrToBean(session, node,
					LazyType.LAZY);
			assertThat(bean.getBigPojoProperty().get(session),
					is(notNullValue()));
		} finally {
			session.logout();
			provider.closeRepositoryAndCleanResources();
		}
	}

	@Test
	public void shouldLooseWeakValue() throws Exception {
		final ClassWithLazyProperty bean = new ClassWithLazyProperty();
		bean.setTest("test");
		bean.setBigPojoProperty(LazyProperty.Factory
				.<SerializablePojoProperty> create(bean));
		bean.getBigPojoProperty().getMetadata().setCached(
				new SerializablePojoProperty());
		bean.getBigPojoProperty().get(null).setAnotherProperty("test");
		assertThat(bean.getBigPojoProperty().get(null), is(notNullValue()));
		System.gc();
		assertThat(bean.getBigPojoProperty().getMetadata().getTransient(),
				is(nullValue()));

	}

	@Test
	public void shouldSaveTransientValue() throws Exception {
		final JcrConnectionProvider provider = JcrConnectionProvider
		.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		final SessionWithLock session = provider.openSession();
		try {
			ClassWithLazyProperty bean = new ClassWithLazyProperty();
			bean.setTest("test");

			bean.setBigPojoProperty(LazyProperty.Factory
					.<SerializablePojoProperty> create(bean));
			bean.getBigPojoProperty().setTransient(
					new SerializablePojoProperty());
			bean.getBigPojoProperty().get(session).setAnotherProperty("test");
			assertThat(bean.getBigPojoProperty().getMetadata().getTransient(),
					is(notNullValue()));
			final Node node = SimplePersistSupport.convertBeanToJcr("a/b/c",
					session, bean);
			session.save();
			bean = SimplePersistSupport.convertJcrToBean(session, node,
					LazyType.LAZY);
			assertThat(bean.getBigPojoProperty().getMetadata().getCached(
					session), is(notNullValue()));
			assertThat(bean.getBigPojoProperty().getMetadata().getTransient(),
					is(nullValue()));
		} finally {
			session.logout();
			provider.closeRepositoryAndCleanResources();
		}
	}

}
