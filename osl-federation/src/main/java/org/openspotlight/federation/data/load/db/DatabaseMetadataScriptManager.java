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

package org.openspotlight.federation.data.load.db;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.io.InputStream;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.common.exception.ConfigurationException;

import com.thoughtworks.xstream.XStream;

/**
 * Singleton for guarding the database metadata scripts, and to load this only
 * once or when asked. The xml with all select statments to get the metadata is
 * stored on a private static final field inside this class.
 * 
 * This script metadata information could be on the same structure as we have
 * the configuration metadata for instance. The reason that it is not that way
 * is that this metadata information from databases should be common for all
 * instalations of OSL. It's not a instalation specific information. So, it
 * needs to be on classpath instead of a file, and of course it wont go to the
 * Jcr metadata repository.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@ThreadSafe
public enum DatabaseMetadataScriptManager {
    
    /**
     * Single instance.
     */
    INSTANCE;
    
    /**
     * {@link DatabaseMetadataScript} cache.
     */
    private DatabaseMetadataScripts scripts = null;
    
    private static final String DATABASE_METADATA_SCRIPT_LOCATION = "/configuration/dbMetadataScripts.xml"; //$NON-NLS-1$
    
    /**
     * Returns a database metadata script by its type.
     * 
     * @param type
     * @return a database metadata script
     * @throws ConfigurationException
     */
    public DatabaseMetadataScript getScriptByType(final DatabaseType type)
            throws ConfigurationException {
        for (final DatabaseMetadataScript script : this.getScripts()
                .getScripts()) {
            if (script.getName().equals(type.name())) {
                return script;
            }
        }
        return null;
    }
    
    /**
     * Load the {@link DatabaseMetadataScripts} if needed and return the loaded
     * script metadata.
     * 
     * @return the {@link DatabaseMetadataScripts}
     * @throws ConfigurationException
     */
    public synchronized DatabaseMetadataScripts getScripts()
            throws ConfigurationException {
        if (this.scripts == null) {
            this.reloadScripts();
        }
        return this.scripts;
    }
    
    /**
     * Force the scripts reload. Should no be called in a common way, since the
     * configuration file stays on classpath.
     * 
     * @throws ConfigurationException
     */
    public synchronized void reloadScripts() throws ConfigurationException {
        try {
            final XStream xstream = new XStream();
            xstream.alias("scripts", DatabaseMetadataScripts.class); //$NON-NLS-1$
            xstream.alias("script", DatabaseMetadataScript.class); //$NON-NLS-1$
            xstream.addImplicitCollection(DatabaseMetadataScripts.class,
                    "scripts"); //$NON-NLS-1$
            xstream.alias("customType", CustomTypeScript.class); //$NON-NLS-1$
            InputStream stream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(DATABASE_METADATA_SCRIPT_LOCATION);
            if (stream == null) {
                stream = ClassLoader.getSystemClassLoader()
                        .getResourceAsStream(DATABASE_METADATA_SCRIPT_LOCATION);
            }
            if (stream == null) {
                stream = this.getClass().getResourceAsStream(
                        DATABASE_METADATA_SCRIPT_LOCATION);
            }
            this.scripts = (DatabaseMetadataScripts) xstream.fromXML(stream);
            this.scripts.setImmutable();
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
        
    }
    
}
