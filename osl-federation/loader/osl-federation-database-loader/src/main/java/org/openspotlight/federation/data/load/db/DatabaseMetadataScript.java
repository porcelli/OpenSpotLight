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

package org.openspotlight.federation.data.load.db;

import static java.util.Collections.unmodifiableMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.EnumMap;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
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
 */
public final class DatabaseMetadataScript {

	/**
	 * The Interface DatabaseArtifactNameHandler is used to handle callback
	 * during the initial phases of Database {@link StreamArtifact} processing.
	 * This handler should be used as a filter also on situations where there's
	 * no possible to filter the sql results.
	 */
	public static interface DatabaseArtifactNameHandler {

		/**
		 * This method is called to fix a wrong artifact name returned by some
		 * select statement.
		 * 
		 * @param oldName
		 * @return the new name
		 */
		public String fixName(String oldName);

		/**
		 * Decide if the data passed to this method should be processed.
		 * 
		 * @param artifactName
		 *            the artifact name
		 * @param type
		 *            the type
		 * @param resultSet
		 *            the result set
		 * 
		 * @return true, if successful
		 * 
		 * @throws Exception
		 *             if anything wrong happened
		 */
		public boolean shouldIncludeName(String artifactName, ScriptType type,
				ResultSet resultSet) throws Exception;

	}

	/**
	 * The Interface DatabaseStreamHandler is used to handle callbacks during
	 * the {@link StreamArtifact} final loading phase. Here is possible to
	 * reject or modify the input stream. There's also methods to add new
	 * variables to a template.
	 */
	public static interface DatabaseStreamHandler {

		/**
		 * Calback method to modify or reject the byte array before the
		 * {@link StreamArtifact} loading.
		 * 
		 * @param schema
		 *            the schema
		 * @param type
		 *            the type
		 * @param catalog
		 *            the catalog
		 * @param name
		 *            the name
		 * @param loadedData
		 *            the loaded data
		 * @param connection
		 *            the connection
		 * 
		 * @return the input stream or null to ignore this artifact
		 * 
		 * @throws Exception
		 *             if anything wrong happened
		 */
		public byte[] afterStreamProcessing(String schema, ScriptType type,
				String catalog, String name, byte[] loadedData,
				Connection connection) throws Exception;

		/**
		 * Before fill template this method will be called to add some new
		 * variables to the template.
		 * 
		 * @param schema
		 *            the schema
		 * @param type
		 *            the type
		 * @param catalog
		 *            the catalog
		 * @param name
		 *            the name
		 * @param template
		 *            the template
		 * @param connection
		 *            the connection
		 * 
		 * @throws Exception
		 *             if anything wrong happened
		 */
		public void beforeFillTemplate(String schema, ScriptType type,
				String catalog, String name, StringTemplate template,
				Connection connection) throws Exception;

	}

	/**
	 * Type for describing the way for filling the {@link StreamArtifact}
	 * content for {@link DatabaseType database} {@link ScriptType scripts}.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 */
	public static enum PreferedType {

		/** Simple sql statement should be used. */
		SQL,

		/** Template should be used. */
		TEMPLATE
	}

	/** The column alias map. */
	private EnumMap<ColumnsNamesForMetadataSelect, String> columnAliasMap;

	/** The content column to use. */
	private Integer contentColumnToUse;

	/** The content select. */
	private String contentSelect;

	/** The database. */
	private DatabaseType database;

	/** The data select. */
	private String dataSelect;

	/** The immutable. */
	private boolean immutable = false;

	/** The name handler class. */
	private Class<? extends DatabaseArtifactNameHandler> nameHandlerClass;

	/** The prefered type. */
	private PreferedType preferedType;

	/** The script type. */
	private ScriptType scriptType;

	/** The stream handler class. */
	private Class<? extends DatabaseStreamHandler> streamHandlerClass;

	/** The template. */
	private String template;

	/** The templates select. */
	private String templatesSelect;

	private boolean tryAgainIfNoResult;

	/**
	 * Gets the column alias map.
	 * 
	 * @return the column alias map
	 */
	public synchronized Map<ColumnsNamesForMetadataSelect, String> getColumnAliasMap() {
		if (this.columnAliasMap == null) {
			this.columnAliasMap = new EnumMap<ColumnsNamesForMetadataSelect, String>(
					ColumnsNamesForMetadataSelect.class);
		}
		if (this.immutable) {
			return unmodifiableMap(this.columnAliasMap);
		}
		return this.columnAliasMap;
	}

	/**
	 * Gets the content column to use.
	 * 
	 * @return column number to use on content select
	 */
	public Integer getContentColumnToUse() {
		return this.contentColumnToUse;
	}

	/**
	 * Gets the content select.
	 * 
	 * @return the select for filling the stream content
	 */
	public String getContentSelect() {
		return this.contentSelect;
	}

	/**
	 * Gets the database.
	 * 
	 * @return the database type
	 */
	public DatabaseType getDatabase() {
		return this.database;
	}

	/**
	 * Gets the data select.
	 * 
	 * @return the mandatory select for filling the basic common data for all
	 *         stream artifacts loaded from database
	 */
	public String getDataSelect() {
		return this.dataSelect;
	}

	/**
	 * Gets the name handler class.
	 * 
	 * @return the name handler class
	 */
	public Class<? extends DatabaseArtifactNameHandler> getNameHandlerClass() {

		return this.nameHandlerClass;
	}

	/**
	 * Gets the prefered type.
	 * 
	 * @return the prefered type
	 */
	public PreferedType getPreferedType() {
		return this.preferedType;
	}

	/**
	 * Gets the script type.
	 * 
	 * @return the script type
	 */
	public ScriptType getScriptType() {
		return this.scriptType;
	}

	/**
	 * Gets the stream handler class.
	 * 
	 * @return the stream handler class
	 */
	public Class<? extends DatabaseStreamHandler> getStreamHandlerClass() {
		return this.streamHandlerClass;
	}

	/**
	 * Gets the template.
	 * 
	 * @return the template for stream content
	 */
	public String getTemplate() {
		return this.template;
	}

	/**
	 * Gets the templates select.
	 * 
	 * @return the select to fill the template for stream content
	 */
	public String getTemplatesSelect() {
		return this.templatesSelect;
	}

	/**
	 * Checks if is try again if no result.
	 * 
	 * @return true, if is try again if no result
	 */
	public boolean isTryAgainIfNoResult() {
		return this.tryAgainIfNoResult;
	}

	/**
	 * Sets the column alias map.
	 * 
	 * @param columnAliasMap
	 *            the column alias map
	 */
	public void setColumnAliasMap(
			final EnumMap<ColumnsNamesForMetadataSelect, String> columnAliasMap) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.columnAliasMap = columnAliasMap;
	}

	/**
	 * Sets the column number to use inside the content select.
	 * 
	 * @param contentColumnToUse
	 *            the content column to use
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
	 *            the content select
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
	 *            the database
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
	 *            the data select
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
	 * Sets the name handler class.
	 * 
	 * @param nameHandlerClass
	 *            the new name handler class
	 */
	public void setNameHandlerClass(
			final Class<? extends DatabaseArtifactNameHandler> nameHandlerClass) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.nameHandlerClass = nameHandlerClass;
	}

	/**
	 * Sets the preffered type.
	 * 
	 * @param preferedType
	 *            the prefered type
	 */
	public void setPreferedType(final PreferedType preferedType) {
		this.preferedType = preferedType;
	}

	/**
	 * Sets the script type.
	 * 
	 * @param scriptType
	 *            the script type
	 */
	public void setScriptType(final ScriptType scriptType) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.scriptType = scriptType;
	}

	/**
	 * Sets the stream handler class.
	 * 
	 * @param streamHandlerClass
	 *            the new stream handler class
	 */
	public void setStreamHandlerClass(
			final Class<? extends DatabaseStreamHandler> streamHandlerClass) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.streamHandlerClass = streamHandlerClass;
	}

	/**
	 * Sets the template for stream content.
	 * 
	 * @param template
	 *            the template
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
	 *            the templates select
	 */
	public void setTemplatesSelect(final String templatesSelect) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.templatesSelect = templatesSelect;
	}

	/**
	 * Sets the try again if no result.
	 * 
	 * @param tryAgainIfNoResult
	 *            the new try again if no result
	 */
	public void setTryAgainIfNoResult(final boolean tryAgainIfNoResult) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		this.tryAgainIfNoResult = tryAgainIfNoResult;
	}

}
