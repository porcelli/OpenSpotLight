/**
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
package org.openspotlight.graph.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.util.SLCollections;
import org.openspotlight.graph.TreeLineReference;
import org.openspotlight.graph.TreeLineReference.ArtifactLineReference;
import org.openspotlight.graph.TreeLineReference.SimpleLineReference;
import org.openspotlight.graph.TreeLineReference.StatementLineReference;

public class TreeLineReferenceSupport {

    private static final class ArtifactLineReferenceImpl implements ArtifactLineReference {

        private static final long                      serialVersionUID = 8671847295268238991L;

        private final String                           artifactId;

        private final String                           artifactVersion;

        private final Iterable<StatementLineReference> statements;

        private ArtifactLineReferenceImpl(final String artifactId, final String artifactVersion,
                                          final Iterable<StatementLineReference> statements) {
            this.artifactId = artifactId;
            this.artifactVersion = artifactVersion;
            this.statements = statements;

        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final ArtifactLineReferenceImpl that = (ArtifactLineReferenceImpl) o;

            if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) { return false; }
            if (artifactVersion != null ? !artifactVersion.equals(that.artifactVersion) : that.artifactVersion != null) { return false; }
            if (statements != null ? !statements.equals(that.statements) : that.statements != null) { return false; }

            return true;
        }

        @Override
        public String getArtifactId() {
            return artifactId;
        }

        @Override
        public String getArtifactVersion() {
            return artifactVersion;
        }

        @Override
        public Iterable<StatementLineReference> getStatements() {
            return statements;
        }

        @Override
        public int hashCode() {
            int result = artifactId != null ? artifactId.hashCode() : 0;
            result = 31 * result + (artifactVersion != null ? artifactVersion.hashCode() : 0);
            result = 31 * result + (statements != null ? statements.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "ArtifactLineReferenceImpl{" +
                "artifactId='" + artifactId + '\'' +
                ", artifactVersion='" + artifactVersion + '\'' +
                ", statements=" + statements +
                '}';
        }
    }

    private static final class SimpleLineReferenceImpl implements SimpleLineReference {

        private static final long serialVersionUID = 1L;
        private final int         beginColumn, endColumn, beginLine, endLine;

        SimpleLineReferenceImpl(final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
            this.beginLine = beginColumn;
            this.endLine = endLine;
            this.beginColumn = beginColumn;
            this.endColumn = endColumn;
        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final SimpleLineReferenceImpl that = (SimpleLineReferenceImpl) o;

            if (beginColumn != that.beginColumn) { return false; }
            if (beginLine != that.beginLine) { return false; }
            if (endColumn != that.endColumn) { return false; }
            if (endLine != that.endLine) { return false; }

            return true;
        }

        @Override
        public int getBeginColumn() {
            return beginColumn;
        }

        @Override
        public int getBeginLine() {
            return beginLine;
        }

        @Override
        public int getEndColumn() {
            return endColumn;
        }

        @Override
        public int getEndLine() {
            return endLine;
        }

        @Override
        public int hashCode() {
            int result = beginColumn;
            result = 31 * result + endColumn;
            result = 31 * result + beginLine;
            result = 31 * result + endLine;
            return result;
        }

        @Override
        public String toString() {
            return "SimpleLineReferenceImpl{" +
                "beginColumn=" + beginColumn +
                ", endColumn=" + endColumn +
                ", beginLine=" + beginLine +
                ", endLine=" + endLine +
                '}';
        }
    }

    private static final class StatementLineReferenceImpl implements StatementLineReference {

        private static final long                   serialVersionUID = 3325696914749205681L;

        private final Iterable<SimpleLineReference> lineReferences;

        private final String                        statement;

        private StatementLineReferenceImpl(final Iterable<SimpleLineReference> lineReferences, final String statement) {
            this.lineReferences = lineReferences;
            this.statement = statement;
        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final StatementLineReferenceImpl that = (StatementLineReferenceImpl) o;

            if (lineReferences != null ? !lineReferences.equals(that.lineReferences) : that.lineReferences != null) { return false; }
            if (statement != null ? !statement.equals(that.statement) : that.statement != null) { return false; }

            return true;
        }

        @Override
        public Iterable<SimpleLineReference> getLineReferences() {
            return lineReferences;
        }

        @Override
        public String getStatement() {
            return statement;
        }

        @Override
        public int hashCode() {
            int result = lineReferences != null ? lineReferences.hashCode() : 0;
            result = 31 * result + (statement != null ? statement.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "StatementLineReferenceImpl{" +
                "lineReferences=" + lineReferences +
                ", statement='" + statement + '\'' +
                '}';
        }
    }

    private static final class TreeLineReferenceImpl implements TreeLineReference {

        private static final long                     serialVersionUID = 8915154022785981563L;

        private final Iterable<ArtifactLineReference> artifacts;

        private final String                          id;

        private TreeLineReferenceImpl(final Iterable<ArtifactLineReference> artifacts, final String id) {
            this.artifacts = artifacts;
            this.id = id;
        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final TreeLineReferenceImpl that = (TreeLineReferenceImpl) o;

            if (artifacts != null ? !artifacts.equals(that.artifacts) : that.artifacts != null) { return false; }
            if (id != null ? !id.equals(that.id) : that.id != null) { return false; }

            return true;
        }

        @Override
        public Iterable<ArtifactLineReference> getArtifacts() {
            return artifacts;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public int hashCode() {
            int result = artifacts != null ? artifacts.hashCode() : 0;
            result = 31 * result + (id != null ? id.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "TreeLineReferenceImpl{" +
                "artifacts=" + artifacts +
                ", id='" + id + '\'' +
                '}';
        }
    }

    public static TreeLineReference copyOf(
                                           final String treeLineReferenceId,
                                           final Iterable<ArtifactLineReference> treeLineRef,
                                           final Map<String, Map<String, Set<SimpleLineReference>>> newData,
                                           final String artifactIdToUse) {
        final Map<String, Map<String, Set<SimpleLineReference>>> baseToCreateResult =
            new HashMap<String, Map<String, Set<SimpleLineReference>>>();
        if (treeLineRef != null) {
            for (final ArtifactLineReference artifactLineRef: treeLineRef) {
                if (artifactIdToUse == null || artifactIdToUse.equals(artifactLineRef.getArtifactId())) {
                    final Map<String, Set<SimpleLineReference>> artifactEntry =
                        SLCollections.getOrPut(baseToCreateResult, artifactLineRef.getArtifactId(),
                            new HashMap<String, Set<SimpleLineReference>>());
                    for (final StatementLineReference stmtLineRef: artifactLineRef.getStatements()) {
                        final Set<SimpleLineReference> stmtLineRefs =
                            SLCollections.getOrPut(artifactEntry, stmtLineRef.getStatement(), new HashSet<SimpleLineReference>());
                        for (final SimpleLineReference lineRef: stmtLineRef.getLineReferences()) {
                            stmtLineRefs.add(lineRef);
                        }
                    }
                }
            }
        }
        if (newData != null) {
            for (final String artifactId: newData.keySet()) {
                if (artifactIdToUse == null || artifactIdToUse.equals(artifactId)) {
                    final Map<String, Set<SimpleLineReference>> artifactEntry =
                        SLCollections.getOrPut(baseToCreateResult, artifactId,
                            new HashMap<String, Set<SimpleLineReference>>());
                    final Map<String, Set<SimpleLineReference>> artifactData = newData.get(artifactId);
                    if (artifactData != null) {
                        for (final String stmt: artifactData.keySet()) {
                            final Set<SimpleLineReference> newStmtData = artifactData.get(stmt);
                            if (newStmtData != null) {
                                final Set<SimpleLineReference> stmtLineRefs =
                                    SLCollections.getOrPut(artifactEntry, stmt, new HashSet<SimpleLineReference>());
                                for (final SimpleLineReference lineRef: newStmtData) {
                                    stmtLineRefs.add(lineRef);
                                }
                            }
                        }
                    }
                }
            }
        }

        final HashSet<ArtifactLineReference> artifacts = new HashSet<ArtifactLineReference>();
        final TreeLineReferenceImpl treeLineReferenceImpl = new TreeLineReferenceImpl(artifacts, treeLineReferenceId);
        for (final String artifactId: baseToCreateResult.keySet()) {
            final Map<String, Set<SimpleLineReference>> artifactEntry = baseToCreateResult.get(artifactId);
            final HashSet<StatementLineReference> statements = new HashSet<StatementLineReference>();
            final ArtifactLineReferenceImpl artifactLineReferenceImpl =
                new ArtifactLineReferenceImpl(artifactId, null, statements);
            for (final String stmt: artifactEntry.keySet()) {
                final Set<SimpleLineReference> stmtLineRefs = artifactEntry.get(stmt);
                final StatementLineReferenceImpl newStmt = new StatementLineReferenceImpl(stmtLineRefs, stmt);
                statements.add(newStmt);
            }
            artifacts.add(artifactLineReferenceImpl);
        }
        return treeLineReferenceImpl;
    }

    public static TreeLineReference copyOf(
                                           final String treeLineReferenceId,
                                           final TreeLineReference treeLineRef,
                                           final Map<String, Map<String, Set<SimpleLineReference>>> newData) {
        return copyOf(treeLineReferenceId, treeLineRef.getArtifacts(), newData, null);
    }

    public static SimpleLineReference createSimpleLineReference(
                                                                final int beginLine, final int endLine, final int beginColumn,
                                                                final int endColumn) {
        return new SimpleLineReferenceImpl(beginLine, endLine, beginColumn, endColumn);
    }

    public static TreeLineReference createTreeLineReference(
                                                            final String id, final Iterable<ArtifactLineReference> artifacts) {
        return new TreeLineReferenceImpl(artifacts, id);
    }

}
