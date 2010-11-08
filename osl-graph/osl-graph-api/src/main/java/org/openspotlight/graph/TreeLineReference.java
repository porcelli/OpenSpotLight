/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA **********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA
 * E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */
package org.openspotlight.graph;

import java.io.Serializable;

/**
 * This interfaces groups all data related to {@link Element} line references.
 * 
 * @author porcelli
 */
public interface TreeLineReference extends Serializable {

    /**
     * This interface represents an artifacts with its line references grouped by statements
     * 
     * @author porcelli
     */
    public interface ArtifactLineReference extends Serializable {

        /**
         * Returns the Artifactd Id.
         * 
         * @return the artifact id
         */
        public String getArtifactId();

        /**
         * Returns the artifact version
         * 
         * @return the artifact version
         */
        public String getArtifactVersion();

        /**
         * Returns an iterable that groups line references by statements
         * 
         * @return the statements
         */
        public Iterable<StatementLineReference> getStatements();
    }

    /**
     * Interface that represents region of an artifact
     * 
     * @author porcelli
     */
    public interface SimpleLineReference extends Serializable {

        /**
         * Returns the initial column
         * 
         * @return the initial column
         */
        public int getBeginColumn();

        /**
         * Returns the initial line
         * 
         * @return the initial line
         */
        public int getBeginLine();

        /**
         * Returns the end column
         * 
         * @return the end column
         */
        public int getEndColumn();

        /**
         * Returns the end line
         * 
         * @return the end line
         */
        public int getEndLine();
    }

    /**
     * The Interface SLStatementLineReference.
     * 
     * @author porcelli
     */
    public interface StatementLineReference extends Serializable {

        /**
         * Returns an iterable of line references
         * 
         * @return the line references
         */
        public Iterable<SimpleLineReference> getLineReferences();

        /**
         * Return the statement
         * 
         * @return the statement
         */
        public String getStatement();

    }

    /**
     * Returns an iterable of line references by artifacts
     * 
     * @return the artifacts
     */
    public Iterable<ArtifactLineReference> getArtifacts();

    /**
     * Returns the {@link Element#getId()}
     * 
     * @return the element id
     */
    public String getId();
}
