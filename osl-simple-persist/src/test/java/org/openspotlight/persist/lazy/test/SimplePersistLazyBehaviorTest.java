/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.persist.lazy.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import javax.jcr.Node;

import org.junit.Test;
import org.openspotlight.common.LazyType;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.provider.SessionWithLock;
import org.openspotlight.persist.internal.LazyProperty;
import org.openspotlight.persist.support.SimplePersistSupport;

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
                                                        .<SerializablePojoProperty>create(bean));
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
                                                    .<SerializablePojoProperty>create(bean));
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
                                                        .<SerializablePojoProperty>create(bean));
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
