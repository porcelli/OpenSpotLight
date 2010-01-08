package org.openspotlight.bundle.db.processor;

import static org.openspotlight.federation.processing.BundleProcessorSupport.links;

import org.openspotlight.bundle.db.DBConstants;
import org.openspotlight.bundle.db.metamodel.link.AbstractTypeBind;
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
import org.openspotlight.graph.SLContextAlreadyExistsException;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLInvalidCredentialException;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLNodeTypeNotInExistentHierarchy;

public class DbProcessorHelper implements DBConstants {
	public static class ConstraintVo {
		public final ParentVo parent;
		public final TableView table;
		public final Column column;

		public ConstraintVo(final ParentVo parent, final TableView table,
				final Column column) {
			super();
			this.parent = parent;
			this.table = table;
			this.column = column;
		}
	}

	public static class ParentVo {
		public final Database database;
		public final SLNode parent;
		public final SLNode databaseContextNode;
		public final Class<? extends SLLink> tableParentLink;

		public ParentVo(final SLNode tableParent,
				final SLNode databaseContextNode,
				final Class<? extends SLLink> tableParentLink,
				final Database database) {
			super();
			this.database = database;
			parent = tableParent;
			this.databaseContextNode = databaseContextNode;
			this.tableParentLink = tableParentLink;
		}
	}

	public static class TableVo {
		public final SLNode databaseContextNode;
		public final Database database;
		public final TableView table;
		public final TableView abstractTable;

		public TableVo(final SLNode databaseContextNode,
				final Database database, final TableView table,
				final TableView abstractTable) {
			this.databaseContextNode = databaseContextNode;
			this.database = database;
			this.table = table;
			this.abstractTable = abstractTable;
		}
	}

	@SuppressWarnings("unchecked")
	public static Column createColumn(final DbWrappedType wrappedType,
			final ExecutionContext context, final SLNode databaseContextNode,
			final TableView table, final TableView abstractTable,
			final org.openspotlight.federation.domain.artifact.db.Column c)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		final Column column = table.addNode(wrappedType.getColumnType(), c
				.getName(), links(AbstractTypeBind.class, ColumnDataType.class,
				ForeignKey.class), links(ConstraintDatabaseColumn.class));
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

	public static void createColumns(final DbWrappedType wrappedType,
			final TableArtifact artifact, final ExecutionContext context,
			final SLNode databaseContextNode, final Database database,
			final TableView table, final TableView abstractTable)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		for (final org.openspotlight.federation.domain.artifact.db.Column c : artifact
				.getColumns()) {
			createColumn(wrappedType, context, databaseContextNode, table,
					abstractTable, c);
		}
	}

	@SuppressWarnings("unchecked")
	private static ConstraintVo createConstraintParentNodes(
			final DbWrappedType wrappedType, final ExecutionContext context,
			final CurrentProcessorContext currentContext,
			final String serverName, final String databaseName,
			final String schemaName, final String catalogName,
			final String tableName, final String columnName)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {

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
		final Column column = table.addNode(Column.class, columnName,
				links(AbstractTypeBind.class, ColumnDataType.class,
						ForeignKey.class),
				links(ConstraintDatabaseColumn.class));
		final ConstraintVo constraintVo = new ConstraintVo(parent, table,
				column);
		return constraintVo;

	}

	@SuppressWarnings("unchecked")
	public static void createForeignKey(final DbWrappedType wrappedType,
			final ExecutionContext context,
			final CurrentProcessorContext currentContext,
			final ForeignKeyConstraintArtifact artifact)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {

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
						artifact.getConstraintName(), links(
								ConstraintDatabaseColumn.class,
								ForeignKey.class), links(
								ConstraintDatabaseColumn.class,
								ForeignKey.class));
		context.getGraphSession().addLink(ConstraintDatabaseColumn.class,
				fromParent.column, fk, false);
		context.getGraphSession().addLink(ConstraintDatabaseColumn.class,
				toParent.column, fk, false);
		context.getGraphSession().addLink(ForeignKey.class, fromParent.column,
				toParent.column, false);

	}

	@SuppressWarnings("unchecked")
	public static void createPrimaryKey(final DbWrappedType wrappedType,
			final ExecutionContext context,
			final CurrentProcessorContext currentContext,
			final PrimaryKeyConstraintArtifact artifact)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {

		final ConstraintVo parent = createConstraintParentNodes(wrappedType,
				context, currentContext, artifact.getServerName(), artifact
						.getDatabaseName(), artifact.getSchemaName(), artifact
						.getCatalogName(), artifact.getTableName(), artifact
						.getColumnName());
		final DatabaseConstraintPrimaryKey pk = parent.parent.database.addNode(
				wrappedType.getDatabaseConstraintPrimaryKeyType(), artifact
						.getConstraintName(),
				links(ConstraintDatabaseColumn.class),
				links(ConstraintDatabaseColumn.class));
		context.getGraphSession().addLink(ConstraintDatabaseColumn.class,
				parent.column, pk, false);

	}

	@SuppressWarnings("unchecked")
	public static TableVo createTableData(final DbWrappedType wrappedType,
			final TableArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context)
			throws SLContextAlreadyExistsException, SLGraphSessionException,
			SLInvalidCredentialException, SLNodeTypeNotInExistentHierarchy {
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
			final DbWrappedType wrappedType, final TableArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context)
			throws SLContextAlreadyExistsException, SLGraphSessionException,
			SLInvalidCredentialException, SLNodeTypeNotInExistentHierarchy {
		final SLContext databaseContext = context.getGraphSession()
				.createContext(DB_ABSTRACT_CONTEXT);

		final SLNode databaseContextNode = databaseContext.getRootNode();

		final Server server = currentContext.getCurrentNodeGroup().addNode(
				wrappedType.getServerType(), artifact.getServerName());
		final Database database = server.addNode(wrappedType.getDatabaseType(),
				artifact.getDatabaseName());
		context.getGraphSession().addLink(GroupDatabase.class,
				currentContext.getCurrentNodeGroup(), database, false);

		final Schema schema = database.addNode(wrappedType.getSchemaType(),
				artifact.getSchemaName());

		context.getGraphSession().addLink(DatabaseSchema.class, database,
				schema, false);

		Catalog catalog = null;
		if (artifact.getCatalogName() != null) {
			catalog = schema.addNode(wrappedType.getCatalogType(), artifact
					.getCatalogName());
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

}
