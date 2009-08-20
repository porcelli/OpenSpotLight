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

import org.openspotlight.federation.data.impl.DatabaseType;

/**
 * Pojo class to store the script to get database metadata for a database type.
 * This class should be getter by {@link DatabaseMetadataScriptManager}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public final class DatabaseMetadataScript {

	private boolean immutable = false;
	private ScriptType scriptType;
	private DatabaseType database;
	private String contentSelect;
	private String dataSelect;

	public ScriptType getScriptType() {
		return this.scriptType;
	}

	public void setScriptType(ScriptType scriptType) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.scriptType = scriptType;
	}

	public DatabaseType getDatabase() {
		return this.database;
	}

	public void setDatabase(DatabaseType database) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.database = database;
	}

	public String getContentSelect() {
		return this.contentSelect;
	}

	public void setContentSelect(String contentSelect) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.contentSelect = contentSelect;
	}

	public String getDataSelect() {
		return this.dataSelect;
	}

	public void setDataSelect(String dataSelect) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.dataSelect = dataSelect;
	}

	void setImmutable() {
		if (!this.immutable) {
			this.immutable = true;
		}
	}

}
