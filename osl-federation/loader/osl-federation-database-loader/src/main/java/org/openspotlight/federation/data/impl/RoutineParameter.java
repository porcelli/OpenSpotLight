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
package org.openspotlight.federation.data.impl;

import static org.openspotlight.common.util.Assertions.checkCondition;

import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import net.jcip.annotations.ThreadSafe;

/**
 * Metadata for database routine parameter.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@ThreadSafe
@StaticMetadata(keyPropertyName = "name", keyPropertyType = String.class, validParentTypes = { RoutineArtifact.class }, propertyNames = {
		"type", "nullable", "columnSize", "decimalSize", "parameterType" }, propertyTypes = {
		ColumnType.class, NullableSqlType.class, Integer.class, Integer.class,
		RoutineParameter.RoutineParameterType.class })
public class RoutineParameter implements ConfigurationNode {

	/**
	 * Routine column types described on {@link DatabaseMetaData} class. Its int
	 * value respects the values described in {@link DatabaseMetaData} class.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	@SuppressWarnings("boxing")
	public static enum RoutineParameterType {
		/**
		 * Wasn't possible to discover its type.
		 */
		UNKNOWN(0),
		/**
		 * Input parameter.
		 */
		IN(1),
		/**
		 * Input/Output parameter.
		 */
		INOUT(2),
		/**
		 * Output parameter.
		 */
		OUT(4),
		/**
		 * Return value as described in
		 * {@link DatabaseMetaData#procedureColumnReturn}.
		 */
		RETURN_VALUE(5),
		/**
		 * Column result as described in
		 * {@link DatabaseMetaData#procedureColumnResult}.
		 */
		RESULT_COLUMN(3);

		/**
		 * Internal cache
		 */
		private static final Map<Integer, RoutineParameterType> cache = new HashMap<Integer, RoutineParameterType>();
		static {
			for (final RoutineParameterType n : values()) {
				cache.put(n.getSqlTypeValue(), n);
			}
		}

		/**
		 * Static factory method
		 * 
		 * @param sqlType
		 * @return the correct RoutineParameter type by sql int constant
		 */
		public static RoutineParameterType getTypeByInt(final int sqlType) {
			return cache.get(sqlType);
		}

		private final int sqlTypeValue;

		private RoutineParameterType(final int sqlTypeValue) {
			this.sqlTypeValue = sqlTypeValue;
		}

		/**
		 * 
		 * @return the int value equivalent to {@link Types} constants
		 */
		public int getSqlTypeValue() {
			return this.sqlTypeValue;
		}
	}

	private static final String COLUMN_SIZE = "columnSize"; //$NON-NLS-1$

	private static final String DECIMAL_SIZE = "decimalSize"; //$NON-NLS-1$

	private static final String PARAMETER_TYPE = "parameterType"; //$NON-NLS-1$

	/**
	 * 
	 * @return the parameter type
	 */
	public RoutineParameterType getParameterType() {
		return this.instanceMetadata.getProperty(PARAMETER_TYPE);
	}

	/**
	 * Sets the parameter type.
	 * 
	 * @param parameterType
	 */
	public void setParameterType(RoutineParameterType parameterType) {
		this.instanceMetadata.setProperty(PARAMETER_TYPE, parameterType);
	}

	/**
     * 
     */
	private static final long serialVersionUID = 814911484835529535L;

	private static final String NULLABLE = "nullable"; //$NON-NLS-1$

	private static final String TYPE = "type"; //$NON-NLS-1$

	private final InstanceMetadata instanceMetadata;

	/**
	 * Create a column inside a table
	 * 
	 * @param table
	 * @param columnName
	 */
	public RoutineParameter(final RoutineArtifact table, final String columnName) {
		this.instanceMetadata = createWithKeyProperty(this, table, columnName);
		checkCondition("noParameter", //$NON-NLS-1$
				table.getRoutineParameterByName(columnName) == null);
		table.getInstanceMetadata().addChild(this);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public int compareTo(final ConfigurationNode o) {
		return this.instanceMetadata.compare(this, o);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object obj) {
		return this.instanceMetadata.equals(obj);
	}

	/**
	 * 
	 * @return the column size
	 */
	public Integer getColumnSize() {
		return this.instanceMetadata.getProperty(COLUMN_SIZE);
	}

	/**
	 * 
	 * @return the decimal size
	 */
	public Integer getDecimalSize() {
		return this.instanceMetadata.getProperty(DECIMAL_SIZE);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public InstanceMetadata getInstanceMetadata() {
		return this.instanceMetadata;
	}

	/**
	 * 
	 * @return the name
	 */
	public String getName() {
		return (String) this.instanceMetadata.getKeyPropertyValue();
	}

	/**
	 * 
	 * @return nullable property
	 */
	public NullableSqlType getNullable() {
		return this.instanceMetadata.getProperty(NULLABLE);
	}

	/**
	 * 
	 * @return the column type
	 */
	public ColumnType getType() {
		return this.instanceMetadata.getProperty(TYPE);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return this.instanceMetadata.hashCode();
	}

	/**
	 * 
	 * Sets the column size.
	 * 
	 * @param columnSize
	 */
	public void setColumnSize(final Integer columnSize) {
		this.instanceMetadata.setProperty(COLUMN_SIZE, columnSize);
	}

	/**
	 * Sets the decimal size.
	 * 
	 * @param decimalSize
	 */
	public void setDecimalSize(final Integer decimalSize) {
		this.instanceMetadata.setProperty(DECIMAL_SIZE, decimalSize);

	}

	/**
	 * Set nullable property.
	 * 
	 * @param nullable
	 */
	public void setNullable(final NullableSqlType nullable) {
		this.instanceMetadata.setProperty(NULLABLE, nullable);
	}

	/**
	 * Set the column type
	 * 
	 * @param type
	 */
	public void setType(final ColumnType type) {
		this.instanceMetadata.setProperty(TYPE, type);
	}

}
