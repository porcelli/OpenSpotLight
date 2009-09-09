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

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.ClassPathResource.getResourceFromClassPath;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.impl.DatabaseType;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript.PreferedType;

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

	private static class MapKey {
		private final DatabaseType databaseType;
		private final int hashCode;
		private final ScriptType scriptType;

		public MapKey(final DatabaseType databaseType,
				final ScriptType scriptType) {
			this.databaseType = databaseType;
			this.scriptType = scriptType;
			this.hashCode = hashOf(databaseType, scriptType);
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) {
				return true;
			}
			if (!(o instanceof MapKey)) {
				return false;
			}
			final MapKey that = (MapKey) o;
			return eachEquality(of(this.databaseType, this.scriptType), andOf(
					that.databaseType, that.scriptType));
		}

		@Override
		public int hashCode() {
			return this.hashCode;
		}

	}

	private final Map<MapKey, DatabaseMetadataScript> scriptMap = new HashMap<MapKey, DatabaseMetadataScript>();

	/**
	 * Load the {@link DatabaseMetadataScripts} if needed and return the loaded
	 * script metadata.
	 * 
	 * @param databaseType
	 * @param scriptType
	 * @return the database metadata script or null if there's no script for
	 *         that db and type
	 */
	public DatabaseMetadataScript getScript(final DatabaseType databaseType,
			final ScriptType scriptType) {
		if (this.scriptMap.size() == 0) {
			this.reloadScripts();
		}
		DatabaseMetadataScript script = this.scriptMap.get(new MapKey(
				databaseType, scriptType));
		DatabaseType internalType = databaseType;
		while (script == null) {
			internalType = internalType.getParent();
			if (internalType == null) {
				return null;
			}
			script = this.scriptMap.get(new MapKey(internalType, scriptType));
		}
		return script;
	}

	/**
	 * Force the scripts reload. Should no be called in a common way, since the
	 * configuration file stays on classpath.
	 * 
	 * @throws ConfigurationException
	 */
	public synchronized void reloadScripts() throws ConfigurationException {
		try {
			this.scriptMap.clear();
			final XStream xstream = new XStream();
			xstream.alias("script", DatabaseMetadataScript.class); //$NON-NLS-1$
			xstream.alias("column", ColumnsNamesForMetadataSelect.class);//$NON-NLS-1$
			xstream.omitField(DatabaseMetadataScript.class, "immutable"); //$NON-NLS-1$
			for (final ScriptType scriptType : ScriptType.values()) {
				for (final DatabaseType databaseType : DatabaseType.values()) {
					final String fileName = format(
							"/configuration/{0}-{1}.xml", databaseType,
							scriptType);
					final InputStream stream = getResourceFromClassPath(fileName);
					if (stream == null) {
						continue;
					}
					final DatabaseMetadataScript newScript = (DatabaseMetadataScript) xstream
							.fromXML(stream);
					if (!databaseType.equals(newScript.getDatabase())) {
						logAndReturn(new IllegalStateException(format(
								"Wrong database on {0}", fileName)));
					}
					if (!scriptType.equals(newScript.getScriptType())) {
						logAndReturn(new IllegalStateException(format(
								"Wrong scriptType on {0}", fileName)));
					}
					if (newScript.getPreferedType() == null) {
						logAndReturn(new IllegalStateException(format(
								"No preferedType on {0}", fileName)));
					}
					if (PreferedType.SQL.equals(newScript.getPreferedType())
							&& ((newScript.getContentSelect() == null) || (newScript
									.getDataSelect() == null))) {
						logAndReturn(new IllegalStateException(
								format(
										"PreferedType SQL but no selects for content or data on {0}",
										fileName)));
					}
					if (PreferedType.TEMPLATE.equals(newScript
							.getPreferedType())
							&& ((newScript.getTemplate() == null) || (newScript
									.getTemplatesSelect() == null))) {
						logAndReturn(new IllegalStateException(
								format(
										"PreferedType TEMPLATE but no select for template or missing template itself on {0}",
										fileName)));
					}

					this.scriptMap.put(new MapKey(newScript.getDatabase(),
							newScript.getScriptType()), newScript);
				}
			}
		} catch (final Exception e) {
			throw logAndReturnNew(e, ConfigurationException.class);
		}

	}

}
