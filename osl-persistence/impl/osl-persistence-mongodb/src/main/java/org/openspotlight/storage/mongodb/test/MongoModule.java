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
package org.openspotlight.storage.mongodb.test;

import org.openspotlight.storage.DefaultSTPartitionFactory;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.mongodb.MongoMaxCacheSize;

import com.google.inject.AbstractModule;
import com.mongodb.Mongo;

/**
 * Created by User: feu - Date: Jun 11, 2010 - Time: 10:42:37 AM
 */
public class MongoModule extends AbstractModule {

    private final STStorageSession.STFlushMode flushMode;

    private final Mongo                        mongo;

    private final STRepositoryPath             repositoryPath;

    private final STPartitionFactory           factory;

    public MongoModule( STStorageSession.STFlushMode flushMode, Mongo mongo, STRepositoryPath repositoryPath ) {
        this.flushMode = flushMode;
        this.mongo = mongo;
        this.repositoryPath = repositoryPath;
        this.factory = new DefaultSTPartitionFactory();
    }

    public MongoModule( STStorageSession.STFlushMode flushMode, Mongo mongo, STRepositoryPath repositoryPath,
                        STPartitionFactory factory ) {
        this.flushMode = flushMode;
        this.mongo = mongo;
        this.repositoryPath = repositoryPath;
        this.factory = factory;
    }

    @Override
    protected void configure() {
        bind(STPartitionFactory.class).toInstance(factory);
        bind(Mongo.class).toInstance(mongo);
        bind(STRepositoryPath.class).toInstance(repositoryPath);
        bind(STStorageSession.STFlushMode.class).toInstance(flushMode);
        bind(STStorageSession.class).toProvider(MongoSTStorageSessionProvider.class);
        bind(int.class)
                       .annotatedWith(MongoMaxCacheSize.class).toInstance(256);

    }
}
