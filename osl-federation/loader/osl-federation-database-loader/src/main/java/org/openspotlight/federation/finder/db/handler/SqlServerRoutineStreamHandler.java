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

package org.openspotlight.federation.finder.db.handler;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import org.antlr.stringtemplate.StringTemplate;
import org.openspotlight.federation.finder.db.ScriptType;
import org.openspotlight.federation.finder.db.DatabaseMetadataScript.DatabaseStreamHandler;

/**
 * The Class RoutineStreamHandler is used to fix the routine streams.
 */
public class SqlServerRoutineStreamHandler implements DatabaseStreamHandler {

	/**
	 * {@inheritDoc}
	 */
	public byte[] afterStreamProcessing(final String schema,
			final ScriptType type, final String catalog, final String name,
			final byte[] loadedData, final Connection connection) {
		return loadedData;
	}

	/**
	 * {@inheritDoc}
	 */
	public void beforeFillTemplate(final String schema, final ScriptType type,
			final String catalog, final String name,
			final StringTemplate template, final Connection connection)
			throws Exception {

		final ResultSet parameterResultSet = connection.getMetaData()
				.getProcedureColumns(catalog, schema, name, null);
		try {
			while (parameterResultSet.next()) {
				final String column = parameterResultSet
						.getString("COLUMN_NAME");
				final String typeName = parameterResultSet
						.getString("TYPE_NAME");
				final int columnType = parameterResultSet.getInt("COLUMN_TYPE");
				String inOutType;
				String returnTypeString;
				switch (columnType) {
				case DatabaseMetaData.procedureColumnIn:
					inOutType = "IN";
					break;
				case DatabaseMetaData.procedureColumnInOut:
					inOutType = "IN OUT";
					break;
				case DatabaseMetaData.procedureColumnOut:
					inOutType = "OUT";
					break;
				case DatabaseMetaData.procedureColumnResult:
				case DatabaseMetaData.procedureColumnReturn:
					returnTypeString = " returning " + typeName;
					template.setAttribute("returnType", returnTypeString);
					continue;
				default:
					inOutType = "' '";
					break;
				}
				template.setAttribute("parameter.{column,type,inOut}", column,
						typeName, inOutType);
			}
		} finally {
			if (parameterResultSet != null) {
				parameterResultSet.close();
			}
		}
	}

}
