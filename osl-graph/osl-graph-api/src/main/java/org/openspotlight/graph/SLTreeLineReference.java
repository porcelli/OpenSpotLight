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

import java.util.Collection;

/**
 * The Interface SLTreeLineReference.
 * 
 * @author porcelli
 */
public interface SLTreeLineReference {

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId();

    /**
     * Gets the artifacts.
     * 
     * @return the artifacts
     */
    public Collection<SLArtifactLineReference> getArtifacts();

    /**
     * The Interface SLArtifactLineReference.
     * 
     * @author porcelli
     */
    public interface SLArtifactLineReference {

        /**
         * Gets the artifact id.
         * 
         * @return the artifact id
         */
        public String getArtifactId();

        /**
         * Gets the artifact version.
         * 
         * @return the artifact version
         */
        public String getArtifactVersion();

        /**
         * Gets the statements.
         * 
         * @return the statements
         */
        public Collection<SLStatementLineReference> getStatements();

        /**
         * Adds the statement.
         * 
         * @param statement the statement
         * @param startLine the start line
         * @param endLine the end line
         * @param startColumn the start column
         * @param endColumn the end column
         */
        public void addStatement( String statement,
                                  Integer startLine,
                                  Integer endLine,
                                  Integer startColumn,
                                  Integer endColumn );
    }

    /**
     * The Interface SLStatementLineReference.
     * 
     * @author porcelli
     */
    public interface SLStatementLineReference {

        /**
         * Gets the statement.
         * 
         * @return the statement
         */
        public String getStatement();

        /**
         * Gets the line references.
         * 
         * @return the line references
         */
        public Collection<SLSimpleLineReference> getLineReferences();

        /**
         * Adds the line ref.
         * 
         * @param startLine the start line
         * @param endLine the end line
         * @param startColumn the start column
         * @param endColumn the end column
         */
        public void addLineRef( Integer startLine,
                                Integer endLine,
                                Integer startColumn,
                                Integer endColumn );

        /**
         * The Interface SLSimpleLineReference.
         * 
         * @author porcelli
         */
    }

    /**
     * The Interface SLSimpleLineReference.
     * 
     * @author porcelli
     */
    public interface SLSimpleLineReference {

        /**
         * Gets the start line.
         * 
         * @return the start line
         */
        public Integer getStartLine();

        /**
         * Gets the end line.
         * 
         * @return the end line
         */
        public Integer getEndLine();

        /**
         * Gets the start column.
         * 
         * @return the start column
         */
        public Integer getStartColumn();

        /**
         * Gets the end column.
         * 
         * @return the end column
         */
        public Integer getEndColumn();
    }
}
