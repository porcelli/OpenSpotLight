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
package org.openspotlight.web.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.openspotlight.common.util.ClassPathResource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.loader.ConfigurationManager;
import org.openspotlight.federation.loader.XmlConfigurationManagerFactory;
import org.openspotlight.web.WebGlobalSettingsSupport;

/**
 * The Class ConfigurationSupport contains methods to be used on {@link ConfigurationManager} saved data.
 */
public class ConfigurationSupport {

    /**
     * Initialize configuration.
     * 
     * @param forceReload the force reload
     * @param jcrSession the jcr session
     * @return true, if successful
     * @throws SLException the SL exception
     */
    public static boolean initializeConfiguration( final boolean forceReload,
                                                   final ConfigurationManager jcrConfigurationManager ) throws Exception {
        final boolean firstTime = jcrConfigurationManager.getAllRepositories().size() == 0;
        boolean reloaded = false;
        if (firstTime || forceReload) {
            saveXmlOnJcr(jcrConfigurationManager);
            reloaded = true;
        }
        return reloaded;
    }

    /**
     * Save xml on jcr.
     * 
     * @param manager the manager
     * @return the configuration
     * @throws SLException the SL exception
     */
    private static void saveXmlOnJcr( final ConfigurationManager manager ) throws Exception {
        GlobalSettings settings;
        Set<Repository> repositories;
        final InputStream is = ClassPathResource.getResourceFromClassPath("osl-configuration.xml");
        final StringWriter writter = new StringWriter();
        IOUtils.copy(is, writter);
        final String xmlContent = writter.toString();
        is.close();
        final ConfigurationManager xmlManager = XmlConfigurationManagerFactory.loadImmutableFromXmlContent(xmlContent);
        settings = xmlManager.getGlobalSettings();
        WebGlobalSettingsSupport.initializeSettings(settings);
        repositories = xmlManager.getAllRepositories();
        manager.saveGlobalSettings(settings);
        for (final Repository repository : repositories) {
            manager.saveRepository(repository);
        }
    }
}
