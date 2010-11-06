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

    private static final class TreeLineReferenceImpl implements TreeLineReference {

        private static final long serialVersionUID = 8915154022785981563L;

        private final Iterable<ArtifactLineReference> artifacts;

        private final String                          id;

        private TreeLineReferenceImpl(Iterable<ArtifactLineReference> artifacts, String id) {
            this.artifacts = artifacts;
            this.id = id;
        }

        @Override
        public boolean equals(
                              Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TreeLineReferenceImpl that = (TreeLineReferenceImpl) o;

            if (artifacts != null ? !artifacts.equals(that.artifacts) : that.artifacts != null) return false;
            if (id != null ? !id.equals(that.id) : that.id != null) return false;

            return true;
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

        public Iterable<ArtifactLineReference> getArtifacts() {
            return artifacts;
        }

        public String getId() {
            return id;
        }
    }

    private static final class ArtifactLineReferenceImpl implements ArtifactLineReference {

        private static final long serialVersionUID = 8671847295268238991L;

        private final String                           artifactId;

        private final String                           artifactVersion;

        private final Iterable<StatementLineReference> statements;

        public String getArtifactId() {
            return artifactId;
        }

        public String getArtifactVersion() {
            return artifactVersion;
        }

        public Iterable<StatementLineReference> getStatements() {
            return statements;
        }

        private ArtifactLineReferenceImpl(String artifactId, String artifactVersion, Iterable<StatementLineReference> statements) {
            this.artifactId = artifactId;
            this.artifactVersion = artifactVersion;
            this.statements = statements;

        }

        @Override
        public boolean equals(
                              Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ArtifactLineReferenceImpl that = (ArtifactLineReferenceImpl) o;

            if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) return false;
            if (artifactVersion != null ? !artifactVersion.equals(that.artifactVersion) : that.artifactVersion != null)
                return false;
            if (statements != null ? !statements.equals(that.statements) : that.statements != null) return false;

            return true;
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

    private static final class StatementLineReferenceImpl implements StatementLineReference {

        private static final long serialVersionUID = 3325696914749205681L;

        private final Iterable<SimpleLineReference> lineReferences;

        private final String                        statement;

        private StatementLineReferenceImpl(Iterable<SimpleLineReference> lineReferences, String statement) {
            this.lineReferences = lineReferences;
            this.statement = statement;
        }

        public Iterable<SimpleLineReference> getLineReferences() {
            return lineReferences;
        }

        public String getStatement() {
            return statement;
        }

        @Override
        public boolean equals(
                              Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StatementLineReferenceImpl that = (StatementLineReferenceImpl) o;

            if (lineReferences != null ? !lineReferences.equals(that.lineReferences) : that.lineReferences != null)
                return false;
            if (statement != null ? !statement.equals(that.statement) : that.statement != null) return false;

            return true;
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

    private static final class SimpleLineReferenceImpl implements SimpleLineReference {

        private static final long serialVersionUID = 1L;
        private final int beginColumn, endColumn, beginLine, endLine;

        SimpleLineReferenceImpl(int beginLine, int endLine, int beginColumn, int endColumn) {
            this.beginLine = beginColumn;
            this.endLine = endLine;
            this.beginColumn = beginColumn;
            this.endColumn = endColumn;
        }

        public int getBeginColumn() {
            return beginColumn;
        }

        public int getEndColumn() {
            return endColumn;
        }

        public int getBeginLine() {
            return beginLine;
        }

        public int getEndLine() {
            return endLine;
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

        @Override
        public boolean equals(
                              Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleLineReferenceImpl that = (SimpleLineReferenceImpl) o;

            if (beginColumn != that.beginColumn) return false;
            if (beginLine != that.beginLine) return false;
            if (endColumn != that.endColumn) return false;
            if (endLine != that.endLine) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = beginColumn;
            result = 31 * result + endColumn;
            result = 31 * result + beginLine;
            result = 31 * result + endLine;
            return result;
        }
    }

    public static TreeLineReference copyOf(
                                           String treeLineReferenceId,
                                           TreeLineReference treeLineRef,
                                           Map<String, Map<String, Set<SimpleLineReference>>> newData) {
        return copyOf(treeLineReferenceId, treeLineRef.getArtifacts(), newData, null);
    }

    public static TreeLineReference copyOf(
                                           String treeLineReferenceId,
                                           Iterable<ArtifactLineReference> treeLineRef,
                                           Map<String, Map<String, Set<SimpleLineReference>>> newData, String artifactIdToUse) {
        Map<String, Map<String, Set<SimpleLineReference>>> baseToCreateResult =
            new HashMap<String, Map<String, Set<SimpleLineReference>>>();
        if (treeLineRef != null) {
            for (ArtifactLineReference artifactLineRef: treeLineRef) {
                if (artifactIdToUse == null || artifactIdToUse.equals(artifactLineRef.getArtifactId())) {
                    Map<String, Set<SimpleLineReference>> artifactEntry =
                        SLCollections.getOrPut(baseToCreateResult, artifactLineRef.getArtifactId(),
                        new HashMap<String, Set<SimpleLineReference>>());
                    for (StatementLineReference stmtLineRef: artifactLineRef.getStatements()) {
                        Set<SimpleLineReference> stmtLineRefs =
                            SLCollections.getOrPut(artifactEntry, stmtLineRef.getStatement(), new HashSet<SimpleLineReference>());
                        for (SimpleLineReference lineRef: stmtLineRef.getLineReferences()) {
                            stmtLineRefs.add(lineRef);
                        }
                    }
                }
            }
        }
        if (newData != null) {
            for (String artifactId: newData.keySet()) {
                if (artifactIdToUse == null || artifactIdToUse.equals(artifactId)) {
                    Map<String, Set<SimpleLineReference>> artifactEntry =
                        SLCollections.getOrPut(baseToCreateResult, artifactId,
                        new HashMap<String, Set<SimpleLineReference>>());
                    Map<String, Set<SimpleLineReference>> artifactData = newData.get(artifactId);
                    if (artifactData != null) {
                        for (String stmt: artifactData.keySet()) {
                            Set<SimpleLineReference> newStmtData = artifactData.get(stmt);
                            if (newStmtData != null) {
                                Set<SimpleLineReference> stmtLineRefs =
                                    SLCollections.getOrPut(artifactEntry, stmt, new HashSet<SimpleLineReference>());
                                for (SimpleLineReference lineRef: newStmtData) {
                                    stmtLineRefs.add(lineRef);
                                }
                            }
                        }
                    }
                }
            }
        }

        HashSet<ArtifactLineReference> artifacts = new HashSet<ArtifactLineReference>();
        TreeLineReferenceImpl treeLineReferenceImpl = new TreeLineReferenceImpl(artifacts, treeLineReferenceId);
        for (String artifactId: baseToCreateResult.keySet()) {
            Map<String, Set<SimpleLineReference>> artifactEntry = baseToCreateResult.get(artifactId);
            HashSet<StatementLineReference> statements = new HashSet<StatementLineReference>();
            ArtifactLineReferenceImpl artifactLineReferenceImpl = new ArtifactLineReferenceImpl(artifactId, null, statements);
            for (String stmt: artifactEntry.keySet()) {
                Set<SimpleLineReference> stmtLineRefs = artifactEntry.get(stmt);
                StatementLineReferenceImpl newStmt = new StatementLineReferenceImpl(stmtLineRefs, stmt);
                statements.add(newStmt);
            }
            artifacts.add(artifactLineReferenceImpl);
        }
        return treeLineReferenceImpl;
    }

    public static SimpleLineReference createSimpleLineReference(
                                                                int beginLine, int endLine, int beginColumn, int endColumn) {
        return new SimpleLineReferenceImpl(beginLine, endLine, beginColumn, endColumn);
    }

    public static TreeLineReference createTreeLineReference(
                                                            String id, Iterable<ArtifactLineReference> artifacts) {
        return new TreeLineReferenceImpl(artifacts, id);
    }

}
