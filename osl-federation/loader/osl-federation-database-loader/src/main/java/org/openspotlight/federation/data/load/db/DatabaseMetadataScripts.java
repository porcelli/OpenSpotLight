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

import static java.util.Collections.unmodifiableList;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspotlight.federation.data.impl.DatabaseType;

/**
 * Pojo class to store database scripts to get schema information. This class
 * should be getter by {@link DatabaseMetadataScriptManager}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public final class DatabaseMetadataScripts {

	private static class MapKey {
		private final DatabaseType databaseType;
		private final ScriptType scriptType;
		private final int hashCode;

		public MapKey(DatabaseType databaseType, ScriptType scriptType) {
			this.databaseType = databaseType;
			this.scriptType = scriptType;
			this.hashCode = hashOf(databaseType, scriptType);
		}

		@Override
		public int hashCode() {
			return this.hashCode;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof MapKey))
				return false;
			MapKey that = (MapKey) o;
			return eachEquality(of(this.databaseType, this.scriptType), andOf(
					that.databaseType, that.scriptType));
		}

	}

	/**
	 * It returns the metadata script description for the parameters passed.
	 * 
	 * 
	 * @param databaseType
	 * @param scriptType
	 * @return the database metadata script or null if there's no script for
	 *         that db and type
	 */
	public DatabaseMetadataScript getScript(DatabaseType databaseType,
			ScriptType scriptType) {
		DatabaseMetadataScript script = this.scriptMap.get(new MapKey(
				databaseType, scriptType));
		DatabaseType internalType = databaseType;
		while (script == null) {
			internalType = internalType.getParent();
			if (internalType == null)
				return null;
			script = this.scriptMap.get(new MapKey(internalType, scriptType));
		}
		return script;
	}

	private boolean immutable = false;

	private List<DatabaseMetadataScript> scripts;

	private Map<MapKey, DatabaseMetadataScript> scriptMap;

	/**
	 * 
	 * @return a list of scripts
	 */
	public List<DatabaseMetadataScript> getScripts() {
		return this.scripts;
	}

	/**
	 * Transform this pojo in a immutable pojo.
	 */
	public void setImmutable() {
		if (!this.immutable) {
			this.scriptMap = new HashMap<MapKey, DatabaseMetadataScript>();
			this.immutable = true;
			this.scripts = unmodifiableList(this.scripts);
			for (final DatabaseMetadataScript script : this.scripts) {
				script.setImmutable();
				this.scriptMap.put(new MapKey(script.getDatabase(), script
						.getScriptType()), script);
			}
		}
	}

	/**
	 * Sets a list of scripts.
	 * 
	 * @param scripts
	 */
	public void setScripts(final List<DatabaseMetadataScript> scripts) {
		if (this.immutable) {
			throw logAndReturn(new UnsupportedOperationException());
		}
		this.scripts = scripts;
	}
}
