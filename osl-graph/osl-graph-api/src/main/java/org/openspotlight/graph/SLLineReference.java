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
package org.openspotlight.graph;

import org.openspotlight.graph.annotation.SLProperty;


/**
 * The Interface SLLineReference.
 * 
 * @author Vitor Hugo Chagas
 */
/**
 * @author vitorchagas
 *
 */
public interface SLLineReference extends SLNode {
	
	/**
	 * Gets the start line.
	 * 
	 * @return the start line
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	@SLProperty
	public Integer getStartLine() throws SLGraphSessionException;
	
	/**
	 * Sets the start line.
	 * 
	 * @param startLine the new start line
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public void setStartLine(Integer startLine) throws SLGraphSessionException;
	
	/**
	 * Gets the end line.
	 * 
	 * @return the end line
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	@SLProperty
	public Integer getEndLine() throws SLGraphSessionException;
	
	/**
	 * Sets the end line.
	 * 
	 * @param endLine the new end line
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public void setEndLine(Integer endLine) throws SLGraphSessionException;

	/**
	 * Gets the start column.
	 * 
	 * @return the start column
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	@SLProperty
	public Integer getStartColumn() throws SLGraphSessionException;
	
	/**
	 * Sets the start column.
	 * 
	 * @param startColumn the new start column
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public void setStartColumn(Integer startColumn) throws SLGraphSessionException;
	
	/**
	 * Gets the end column.
	 * 
	 * @return the end column
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	@SLProperty
	public Integer getEndColumn() throws SLGraphSessionException;
	
	/**
	 * Sets the end column.
	 * 
	 * @param endColumn the new end column
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public void setEndColumn(Integer endColumn) throws SLGraphSessionException;
	
	/**
	 * Gets the statement.
	 * 
	 * @return the statement
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	@SLProperty
	public String getStatement() throws SLGraphSessionException;
	
	/**
	 * Sets the statement.
	 * 
	 * @param statement the new statement
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public void setStatement(String statement) throws SLGraphSessionException;
	
	/**
	 * Gets the artifact id.
	 * 
	 * @return the artifact id
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	@SLProperty
	public String getArtifactId() throws SLGraphSessionException;
	
	/**
	 * Sets the artifact id.
	 * 
	 * @param artifactId the new artifact id
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public void setArtifactId(String artifactId) throws SLGraphSessionException;
	
	/**
	 * Gets the artifact version.
	 * 
	 * @return the artifact version
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	@SLProperty
	public String getArtifactVersion() throws SLGraphSessionException;
	
	/**
	 * Sets the artifact version.
	 * 
	 * @param artifactVersion the new artifact version
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public void setArtifactVersion(String artifactVersion) throws SLGraphSessionException;
}
