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
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.load.DatabaseStreamLoader;

import com.thoughtworks.xstream.XStream;

/**
 * Pojo class to store the script to get database metadata for a database type.
 * This class should be getted by {@link DatabaseMetadataScriptManager}.
 * 
 * This pojo class is serialized by {@link XStream} and should not be directly
 * instantiated. Instead, take a look on {@link DatabaseStreamLoader} class to
 * see the instructions to fill the xml files.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public final class DatabaseMetadataScript {

	/**
	 * Type for describing the way for filling the {@link StreamArtifact}
	 * content for {@link DatabaseType database} {@link ScriptType scripts}.
	 * 
	 * @author feu
	 * 
	 */
	public static enum PreferedType {
		/**
		 * Simple sql statement should be used.
		 */
		SQL,
		/**
		 * Template should be used.
		 */
		TEMPLATE
	}

	private Integer contentColumnToUse;
	private String contentSelect;
	private DatabaseType database;
	private String dataSelect;
	private boolean immutable = false;
	private PreferedType preferedType;
	private ScriptType scriptType;
	private String template;

	private String templatesSelect;

	/**
	 * 
	 * @return column number to use on content select
	 */
	public Integer getContentColumnToUse() {
		return this.contentColumnToUse;
	}

	/**
	 * 
	 * @return the select for filling the stream content
	 */
	public String getContentSelect() {
		return this.contentSelect;
	}

	/**
	 * 
	 * @return the database type
	 */
	public DatabaseType getDatabase() {
		return this.database;
	}

	/**
	 * 
	 * @return the mandatory select for filling the basic common data for all
	 *         stream artifacts loaded from database
	 */
	public String getDataSelect() {
		return this.dataSelect;
	}

	/**
	 * 
	 * @return the prefered type
	 */
	public PreferedType getPreferedType() {
		return this.preferedType;
	}

	/**
	 * 
	 * @return the script type
	 */
	public ScriptType getScriptType() {
		return this.scriptType;
	}

	/**
	 * 
	 * @return the template for stream content
	 */
	public String getTemplate() {
		return this.template;
	}

	/**
	 * 
	 * @return the select to fill the template for stream content
	 */
	public String getTemplatesSelect() {
		return this.templatesSelect;
	}

	/**
	 * Sets the column number to use inside the content select.
	 * 
	 * @param contentColumnToUse
	 */
	public void setContentColumnToUse(final Integer contentColumnToUse) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.contentColumnToUse = contentColumnToUse;
	}

	/**
	 * Sets the select for filling the stream content.
	 * 
	 * @param contentSelect
	 */
	public void setContentSelect(final String contentSelect) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.contentSelect = contentSelect;
	}

	/**
	 * Sets the database type.
	 * 
	 * @param database
	 */
	public void setDatabase(final DatabaseType database) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.database = database;
	}

	/**
	 * Sets the mandatory select for filling the basic common data for all
	 * stream artifacts loaded from database.
	 * 
	 * @param dataSelect
	 */
	public void setDataSelect(final String dataSelect) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.dataSelect = dataSelect;
	}

	/**
	 * Changes the behavior of this object to be immutable. After this method
	 * call all setters should throw an {@link UnsupportedOperationException}
	 * when called.
	 */
	void setImmutable() {
		if (!this.immutable) {
			this.immutable = true;
		}
	}

	/**
	 * Sets the preffered type.
	 * 
	 * @param preferedType
	 */
	public void setPreferedType(final PreferedType preferedType) {
		this.preferedType = preferedType;
	}

	/**
	 * Sets the script type.
	 * 
	 * @param scriptType
	 */
	public void setScriptType(final ScriptType scriptType) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.scriptType = scriptType;
	}

	/**
	 * Sets the template for stream content.
	 * 
	 * @param template
	 */
	public void setTemplate(final String template) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.template = template;
	}

	/**
	 * Sets the select to fill the template for stream content.
	 * 
	 * @param templatesSelect
	 */
	public void setTemplatesSelect(final String templatesSelect) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.templatesSelect = templatesSelect;
	}

}
