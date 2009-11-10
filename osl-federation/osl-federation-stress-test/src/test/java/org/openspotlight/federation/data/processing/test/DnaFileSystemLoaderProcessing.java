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
 * OpenSpotLight - Plataforma de Governan�a de TI de C�digo Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribui��o de direito autoral declarada e atribu�da pelo autor.
 * Todas as contribui��es de terceiros est�o distribu�das sob licen�a da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo sob os 
 * termos da Licen�a P�blica Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU ADEQUA��O A UMA
 * FINALIDADE ESPEC�FICA. Consulte a Licen�a P�blica Geral Menor do GNU para mais detalhes.  
 * 
 * Voc� deve ter recebido uma c�pia da Licen�a P�blica Geral Menor do GNU junto com este
 * programa; se n�o, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */

package org.openspotlight.federation.data.processing.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createOslValidDnaFileConnectorConfiguration;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.data.load.DNAFileSystemArtifactLoader;
import org.openspotlight.federation.data.processing.BundleProcessorManager;
import org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingContext;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.loader.ArtifactLoaderGroup;
import org.openspotlight.federation.loader.ConfigurationManager;
import org.openspotlight.federation.loader.ConfigurationManagerProvider;
import org.openspotlight.federation.loader.ArtifactLoader.ArtifactProcessingCount;
import org.openspotlight.graph.SLInvalidCredentialException;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityException;

@SuppressWarnings( "all" )
public class DnaFileSystemLoaderProcessing {
    private static AuthenticatedUser user;

    /**
     * Inits the.
     * 
     * @throws AbstractFactoryException the abstract factory exception
     */
    @BeforeClass
    public static void init() throws AbstractFactoryException, SLInvalidCredentialException, IdentityException {

        final SecurityFactory securityFactory = AbstractFactory.getDefaultInstance(SecurityFactory.class);
        final User simpleUser = securityFactory.createUser("testUser");
        user = securityFactory.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser, "password");
    }

    public static Configuration loadAllFilesFromThisConfiguration( final Configuration configuration ) throws Exception {
        final ArtifactLoaderGroup group = new ArtifactLoaderGroup(new DNAFileSystemArtifactLoader());
        final Set<ArtifactSource> bundles = findAllNodesOfType(configuration, ArtifactSource.class);
        for (final ArtifactSource bundle : bundles) {
            final ArtifactProcessingCount count = group.loadArtifactsFromMappings(bundle);
            assertThat(count.getErrorCount(), is(0L));
        }
        return configuration;

    }

    @Test
    public void shouldLoadAllArtifactsFromOslSourceCode() throws Exception {
        final Configuration configuration = this.loadAllFilesFromThisConfiguration(createOslValidDnaFileConnectorConfiguration("DnaFileSystemLoaderProcessing"));
        final Set<ArtifactSource> bundles = findAllNodesOfType(configuration, ArtifactSource.class);
        for (final ArtifactSource bundle : bundles) {
            assertThat(bundle.getStreamArtifacts().size() > 0, is(true));
        }
    }

    @Test
    public void shouldProcessAllValidOslSourceCode() throws Exception {
        final Configuration configuration = this.loadAllFilesFromThisConfiguration(createOslValidDnaFileConnectorConfiguration("DnaFileSystemLoaderProcessing"));
        final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        final ConfigurationManagerProvider configurationManagerProvider = new JcrConfigurationManagerProvider(provider);
        final BundleProcessorManager manager = new BundleProcessorManager(user, provider, configurationManagerProvider);
        final ConfigurationManager configurationManager = configurationManagerProvider.getNewInstance();
        configurationManager.save(configuration);
        configurationManager.closeResources();

        final BundleProcessingContext graphContext = mock(BundleProcessingContext.class);
        final Set<StreamArtifact> artifacts = findAllNodesOfType(configuration, StreamArtifact.class);
        final Repository repository = configuration.getRepositoryByName("OSL Group");
        final Set<ArtifactSource> bundles = ConfigurationNodes.findAllNodesOfType(repository, ArtifactSource.class);
        manager.processBundles(bundles);
        assertThat(LogPrinterBundleProcessor.count.get(), is(artifacts.size()));
    }

}
