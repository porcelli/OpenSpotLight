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
package org.openspotlight.bundle.db.processor;

import static org.openspotlight.graph.util.GraphManipulationSupport.links;

import java.util.Arrays;

import org.openspotlight.bundle.common.metamodel.link.AbstractTypeBind;
import org.openspotlight.bundle.db.DBConstants;
import org.openspotlight.bundle.db.metamodel.link.CatalogTableView;
import org.openspotlight.bundle.db.metamodel.link.ColumnDataType;
import org.openspotlight.bundle.db.metamodel.link.ConstraintDatabaseColumn;
import org.openspotlight.bundle.db.metamodel.link.DatabaseSchema;
import org.openspotlight.bundle.db.metamodel.link.ForeignKey;
import org.openspotlight.bundle.db.metamodel.link.GroupDatabase;
import org.openspotlight.bundle.db.metamodel.link.SchemaCatalog;
import org.openspotlight.bundle.db.metamodel.link.SchemaTableView;
import org.openspotlight.bundle.db.metamodel.link.TableViewColumns;
import org.openspotlight.bundle.db.metamodel.node.Catalog;
import org.openspotlight.bundle.db.metamodel.node.Column;
import org.openspotlight.bundle.db.metamodel.node.DataType;
import org.openspotlight.bundle.db.metamodel.node.Database;
import org.openspotlight.bundle.db.metamodel.node.DatabaseConstraintForeignKey;
import org.openspotlight.bundle.db.metamodel.node.DatabaseConstraintPrimaryKey;
import org.openspotlight.bundle.db.metamodel.node.Schema;
import org.openspotlight.bundle.db.metamodel.node.Server;
import org.openspotlight.bundle.db.metamodel.node.TableView;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.db.ForeignKeyConstraintArtifact;
import org.openspotlight.federation.domain.artifact.db.PrimaryKeyConstraintArtifact;
import org.openspotlight.federation.domain.artifact.db.TableArtifact;
import org.openspotlight.federation.domain.artifact.db.ViewArtifact;
import org.openspotlight.federation.processing.CurrentProcessorContext;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;

public class DbProcessorHelper implements DBConstants {
    public static class ConstraintVo {
        public final ParentVo  parent;
        public final TableView table;
        public final Column    column;

        public ConstraintVo(
                             final ParentVo parent, final TableView table,
                             final Column column ) {
            super();
            this.parent = parent;
            this.table = table;
            this.column = column;
        }
    }

    public static class ParentVo {
        public final Database                database;
        public final SLNode                  parent;
        public final SLNode                  databaseContextNode;
        public final Class<? extends SLLink> tableParentLink;

        public ParentVo(
                         final SLNode tableParent,
                         final SLNode databaseContextNode,
                         final Class<? extends SLLink> tableParentLink,
                         final Database database ) {
            super();
            this.database = database;
            parent = tableParent;
            this.databaseContextNode = databaseContextNode;
            this.tableParentLink = tableParentLink;
        }
    }

    public static class TableVo {
        public final SLNode    databaseContextNode;
        public final Database  database;
        public final TableView table;
        public final TableView abstractTable;

        public TableVo(
                        final SLNode databaseContextNode,
                        final Database database, final TableView table,
                        final TableView abstractTable ) {
            this.databaseContextNode = databaseContextNode;
            this.database = database;
            this.table = table;
            this.abstractTable = abstractTable;
        }
    }

    @SuppressWarnings( "unchecked" )
    public static Column createColumn( final DbWrappedType wrappedType,
                                       final ExecutionContext context,
                                       final SLNode databaseContextNode,
                                       final TableView table,
                                       final TableView abstractTable,
                                       final org.openspotlight.federation.domain.artifact.db.Column c ) {
        final Column column = table.addNode(wrappedType.getColumnType(), c
                                                                          .getName(), links(AbstractTypeBind.class, ColumnDataType.class,
                                                                                            ForeignKey.class), null);
        final Column abstractColumn = abstractTable.addNode(Column.class, c
                                                                           .getName());
        context.getGraphSession().addLink(AbstractTypeBind.class, column,
                                          abstractColumn, false);
        context.getGraphSession().addLink(TableViewColumns.class, table,
                                          column, false);

        final DataType dataType = databaseContextNode.addNode(DataType.class, c
                                                                               .getType().name());
        context.getGraphSession().addLink(ColumnDataType.class, column,
                                          dataType, false);
        column.setDataType(dataType.getName());
        return column;
    }

    public static void createColumns( final DbWrappedType wrappedType,
                                      final TableArtifact artifact,
                                      final ExecutionContext context,
                                      final SLNode databaseContextNode,
                                      final Database database,
                                      final TableView table,
                                      final TableView abstractTable ) {
        for (final org.openspotlight.federation.domain.artifact.db.Column c : artifact
                                                                                      .getColumns()) {
            createColumn(wrappedType, context, databaseContextNode, table,
                         abstractTable, c);
        }
    }

    @SuppressWarnings( "unchecked" )
    public static ConstraintVo createConstraintParentNodes(
                                                            final DbWrappedType wrappedType,
                                                            final ExecutionContext context,
                                                            final CurrentProcessorContext currentContext,
                                                            final String serverName,
                                                            final String databaseName,
                                                            final String schemaName,
                                                            final String catalogName,
                                                            final String tableName,
                                                            final String columnName ) {

        final SLContext databaseContext = context.getGraphSession()
                                                 .createContext(DB_ABSTRACT_CONTEXT);

        final SLNode databaseContextNode = databaseContext.getRootNode();

        final Server server = currentContext.getCurrentNodeGroup().addNode(
                                                                           wrappedType.getServerType(), serverName);
        final Database database = server.addNode(wrappedType.getDatabaseType(),
                                                 databaseName);
        context.getGraphSession().addLink(GroupDatabase.class,
                                          currentContext.getCurrentNodeGroup(), database, false);

        final Schema schema = database.addNode(wrappedType.getSchemaType(),
                                               schemaName);

        context.getGraphSession().addLink(DatabaseSchema.class, database,
                                          schema, false);

        Catalog catalog = null;
        if (catalogName != null) {
            catalog = schema.addNode(wrappedType.getCatalogType(), catalogName);
            context.getGraphSession().addLink(SchemaCatalog.class, schema,
                                              catalog, false);
        }
        final Class<? extends SLLink> tableParentLink = catalog != null ? CatalogTableView.class
                : SchemaTableView.class;
        final SLNode tableParent = catalog != null ? catalog : schema;
        final ParentVo parent = new ParentVo(tableParent, databaseContextNode,
                                             tableParentLink, database);
        final TableView table = parent.parent.addNode(TableView.class,
                                                      tableName);
        final Column column = table.addNode(Column.class, columnName, links(
                                                                            AbstractTypeBind.class, ColumnDataType.class), null);
        final ConstraintVo constraintVo = new ConstraintVo(parent, table,
                                                           column);
        return constraintVo;

    }

    public static void createForeignKey( final DbWrappedType wrappedType,
                                         final ExecutionContext context,
                                         final CurrentProcessorContext currentContext,
                                         final ForeignKeyConstraintArtifact artifact ) {

        final ConstraintVo fromParent = createConstraintParentNodes(
                                                                    wrappedType, context, currentContext, artifact.getServerName(),
                                                                    artifact.getDatabaseName(), artifact.getFromSchemaName(),
                                                                    artifact.getFromCatalogName(), artifact.getFromTableName(),
                                                                    artifact.getFromColumnName());
        final ConstraintVo toParent = createConstraintParentNodes(wrappedType,
                                                                  context, currentContext, artifact.getServerName(), artifact
                                                                                                                             .getDatabaseName(), artifact.getToSchemaName(),
                                                                  artifact.getToCatalogName(), artifact.getToTableName(),
                                                                  artifact.getToColumnName());
        final DatabaseConstraintForeignKey fk = fromParent.parent.database
                                                                          .addNode(wrappedType.getDatabaseConstraintForeignKeyType(),
                                                                                   artifact.getConstraintName());
        System.err.println(" >>> "
                           + fk.getName()
                           + " inside "
                           + fromParent.parent.database.getName()
                           + " "
                           + Arrays.toString(fromParent.parent.database.getClass()
                                                                       .getInterfaces()));
        context.getGraphSession().addLink(ConstraintDatabaseColumn.class,
                                          fromParent.column, fk, false);
        context.getGraphSession().addLink(ConstraintDatabaseColumn.class,
                                          toParent.column, fk, false);
        context.getGraphSession().addLink(ForeignKey.class, fromParent.column,
                                          toParent.column, false);

    }

    public static void createPrimaryKey( final DbWrappedType wrappedType,
                                         final ExecutionContext context,
                                         final CurrentProcessorContext currentContext,
                                         final PrimaryKeyConstraintArtifact artifact ) {

        final ConstraintVo parent = createConstraintParentNodes(wrappedType,
                                                                context, currentContext, artifact.getServerName(), artifact
                                                                                                                           .getDatabaseName(), artifact.getSchemaName(), artifact
                                                                                                                                                                                 .getCatalogName(), artifact.getTableName(), artifact
                                                                                                                                                                                                                                     .getColumnName());
        final DatabaseConstraintPrimaryKey pk = parent.parent.parent.addNode(
                                                                             wrappedType.getDatabaseConstraintPrimaryKeyType(), artifact
                                                                                                                                        .getConstraintName());
        context.getGraphSession().addLink(ConstraintDatabaseColumn.class,
                                          parent.column, pk, false);

    }

    @SuppressWarnings( "unchecked" )
    public static TableVo createTableData( final DbWrappedType wrappedType,
                                           final TableArtifact artifact,
                                           final CurrentProcessorContext currentContext,
                                           final ExecutionContext context ) {
        final ParentVo parent = createTableParentNodes(wrappedType, artifact,
                                                       currentContext, context);

        final TableView table;
        final boolean isView = artifact instanceof ViewArtifact;
        final Class<? extends TableView> tableType = isView ? wrappedType
                                                                         .getTableViewViewType() : wrappedType.getTableViewTableType();

        table = parent.parent.addNode(tableType, artifact.getTableName(),
                                      links(AbstractTypeBind.class), links(parent.tableParentLink,
                                                                           TableViewColumns.class));
        final TableView abstractTable = parent.databaseContextNode.addNode(
                                                                           TableView.class, artifact.getTableName());
        context.getGraphSession().addLink(AbstractTypeBind.class, table,
                                          abstractTable, false);
        context.getGraphSession().addLink(parent.tableParentLink,
                                          parent.parent, table, false);
        final TableVo data = new TableVo(parent.databaseContextNode,
                                         parent.database, table, abstractTable);
        return data;
    }

    public static ParentVo createTableParentNodes(
                                                   final DbWrappedType wrappedType,
                                                   final CurrentProcessorContext currentContext,
                                                   final ExecutionContext context,
                                                   final String serverName,
                                                   final String databaseName,
                                                   final String schemaName,
                                                   final String catalogName ) {
        final SLContext databaseContext = context.getGraphSession()
                                                 .createContext(DB_ABSTRACT_CONTEXT);

        final SLNode databaseContextNode = databaseContext.getRootNode();

        final Server server = currentContext.getCurrentNodeGroup().addNode(
                                                                           wrappedType.getServerType(), serverName);
        final Database database = server.addNode(wrappedType.getDatabaseType(),
                                                 databaseName);
        context.getGraphSession().addLink(GroupDatabase.class,
                                          currentContext.getCurrentNodeGroup(), database, false);

        final Schema schema = database.addNode(wrappedType.getSchemaType(),
                                               schemaName);

        context.getGraphSession().addLink(DatabaseSchema.class, database,
                                          schema, false);

        Catalog catalog = null;
        if (catalogName != null) {
            catalog = schema.addNode(wrappedType.getCatalogType(), catalogName);
            context.getGraphSession().addLink(SchemaCatalog.class, schema,
                                              catalog, false);
        }
        final Class<? extends SLLink> tableParentLink = catalog != null ? CatalogTableView.class
                : SchemaTableView.class;
        final SLNode tableParent = catalog != null ? catalog : schema;
        final ParentVo parent = new ParentVo(tableParent, databaseContextNode,
                                             tableParentLink, database);
        return parent;
    }

    public static ParentVo createTableParentNodes(
                                                   final DbWrappedType wrappedType,
                                                   final TableArtifact artifact,
                                                   final CurrentProcessorContext currentContext,
                                                   final ExecutionContext context ) {
        return createTableParentNodes(wrappedType, currentContext, context,
                                      artifact.getServerName(), artifact.getDatabaseName(), artifact
                                                                                                    .getSchemaName(), artifact.getCatalogName());
    }

}
