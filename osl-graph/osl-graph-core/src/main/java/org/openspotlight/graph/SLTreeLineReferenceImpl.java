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
import java.util.HashMap;
import java.util.Map;

/**
 * The Class SLTreeLineReferenceImpl.
 * 
 * @author porcelli
 */
public class SLTreeLineReferenceImpl implements SLTreeLineReference {

    /** The id. */
    private String                               id        = null;

    /** The artifacts. */
    private Map<String, SLArtifactLineReference> artifacts = new HashMap<String, SLArtifactLineReference>();

    /**
     * Instantiates a new tree line reference impl.
     * 
     * @param id the id
     * @param lineReferences the line references
     */
    public SLTreeLineReferenceImpl(
                                    String id,
                                    Collection<SLLineReference> lineReferences ) {

        if (lineReferences != null && lineReferences.size() > 0) {
            this.id = id;
            for (SLLineReference lineReference : lineReferences) {
                String artifactKey = lineReference.getArtifactId() + "|" + lineReference.getArtifactVersion();
                if (!artifacts.containsKey(artifactKey)) {
                    SLArtifactLineReference artifactLineRef = new SLArtifactLineReferenceImpl(lineReference.getArtifactVersion(), lineReference.getArtifactId());
                    artifacts.put(artifactKey, artifactLineRef);
                }
                SLArtifactLineReference artifactLineRef = artifacts.get(artifactKey);
                artifactLineRef.addStatement(lineReference.getStatement(), lineReference.getStartLine(), lineReference.getEndLine(), lineReference.getStartColumn(), lineReference.getEndColumn());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<SLArtifactLineReference> getArtifacts() {
        return artifacts.values();
    }

    /**
     * The Class SLArtifactLineReferenceImpl.
     * 
     * @author porcelli
     */
    public class SLArtifactLineReferenceImpl implements SLArtifactLineReference {

        /** The artifact id. */
        private String                                artifactId      = null;

        /** The artifact version. */
        private String                                artifactVersion = null;

        /** The statements. */
        private Map<String, SLStatementLineReference> statements      = null;

        /**
         * Instantiates a new artifact line reference impl.
         * 
         * @param artifactVersion the artifact version
         * @param artifactId the artifact id
         */
        public SLArtifactLineReferenceImpl(
                                            String artifactVersion, String artifactId ) {
            this.artifactVersion = artifactVersion;
            this.artifactId = artifactId;
            this.statements = new HashMap<String, SLStatementLineReference>();
        }

        /**
         * {@inheritDoc}
         */
        public String getArtifactId() {
            return artifactId;
        }

        /**
         * {@inheritDoc}
         */
        public String getArtifactVersion() {
            return artifactVersion;
        }

        /**
         * {@inheritDoc}
         */
        public Collection<SLStatementLineReference> getStatements() {
            return statements.values();
        }

        /**
         * {@inheritDoc}
         */
        public void addStatement( String statement,
                                  Integer startLine,
                                  Integer endLine,
                                  Integer startColumn,
                                  Integer endColumn ) {
            if (!statements.containsKey(statement)) {
                SLStatementLineReference statementLineRef = new SLStatementLineReferenceImpl(statement);
                statements.put(statement, statementLineRef);
            }
            SLStatementLineReference statementLineRef = statements.get(statement);
            statementLineRef.addLineRef(startLine, endLine, startColumn, endColumn);
        }
    }

    /**
     * The Class SLStatementLineReferenceImpl.
     * 
     * @author porcelli
     */
    public class SLStatementLineReferenceImpl implements SLStatementLineReference {

        /** The statement. */
        private String                             statement = null;

        /** The lines. */
        private Map<String, SLSimpleLineReference> lines     = null;

        /**
         * Instantiates a new sL statement line reference impl.
         * 
         * @param statement the statement
         */
        public SLStatementLineReferenceImpl(
                                             String statement ) {
            this.statement = statement;
            this.lines = new HashMap<String, SLSimpleLineReference>();
        }

        /**
         * {@inheritDoc}
         */
        public String getStatement() {
            return statement;
        }

        /**
         * {@inheritDoc}
         */
        public Collection<SLSimpleLineReference> getLineReferences() {
            return lines.values();
        }

        /**
         * {@inheritDoc}
         */
        public void addLineRef( Integer startLine,
                                Integer endLine,
                                Integer startColumn,
                                Integer endColumn ) {
            String lineRefKey = startLine + "|" + endLine + "|" + startColumn + "|" + endColumn;
            if (!lines.containsKey(lineRefKey)) {
                SLSimpleLineReference statementLineRef = new SLSimpleLineReferenceImpl(startLine, endLine, startColumn, endColumn);
                lines.put(lineRefKey, statementLineRef);
            }
        }
    }

    /**
     * The Class SLSimpleLineReferenceImpl.
     * 
     * @author porcelli
     */
    public class SLSimpleLineReferenceImpl implements SLSimpleLineReference {

        /** The start line. */
        private Integer startLine;

        /** The end line. */
        private Integer endLine;

        /** The start column. */
        private Integer startColumn;

        /** The end column. */
        private Integer endColumn;

        /**
         * Instantiates a new sL simple line reference impl.
         * 
         * @param startLine the start line
         * @param endLine the end line
         * @param startColumn the start column
         * @param endColumn the end column
         */
        public SLSimpleLineReferenceImpl(
                                          Integer startLine, Integer endLine, Integer startColumn, Integer endColumn ) {
            this.startLine = startLine;
            this.endLine = endLine;
            this.startColumn = startColumn;
            this.endColumn = endColumn;
        }

        /**
         * {@inheritDoc}
         */
        public Integer getStartLine() {
            return startLine;
        }

        /**
         * {@inheritDoc}
         */
        public Integer getEndLine() {
            return endLine;
        }

        /**
         * {@inheritDoc}
         */
        public Integer getStartColumn() {
            return startColumn;
        }

        /**
         * {@inheritDoc}
         */
        public Integer getEndColumn() {
            return endColumn;
        }
    }
}
