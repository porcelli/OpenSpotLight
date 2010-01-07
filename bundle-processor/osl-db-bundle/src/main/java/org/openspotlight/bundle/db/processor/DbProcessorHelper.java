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
import org.openspotlight.bundle.db.metamodel.node.Schema;
import org.openspotlight.bundle.db.metamodel.node.Server;
import org.openspotlight.bundle.db.metamodel.node.TableView;
import org.openspotlight.federation.context.ExecutionContext;
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
	public static class TableParentVo {
		public final Database database;
		public final SLNode tableParent;
		public final SLNode databaseContextNode;
		public final Class<? extends SLLink> tableParentLink;

		public TableParentVo(final SLNode tableParent,
				final SLNode databaseContextNode,
				final Class<? extends SLLink> tableParentLink,
				final Database database) {
			super();
			this.database = database;
			this.tableParent = tableParent;
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

	public static TableParentVo createParentNodes(
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
		final TableParentVo parent = new TableParentVo(tableParent,
				databaseContextNode, tableParentLink, database);
		return parent;
	}

	public static void createPrimaryKeys(final DbWrappedType wrappedType,
			final ExecutionContext context,
			final org.openspotlight.federation.domain.artifact.db.Column c,
			final Column column) throws SLNodeTypeNotInExistentHierarchy,
			SLGraphSessionException, SLInvalidCredentialException {

	}

	@SuppressWarnings("unchecked")
	public static TableVo createTableData(final DbWrappedType wrappedType,
			final TableArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context)
			throws SLContextAlreadyExistsException, SLGraphSessionException,
			SLInvalidCredentialException, SLNodeTypeNotInExistentHierarchy {
		final TableParentVo parent = createParentNodes(wrappedType, artifact,
				currentContext, context);

		final TableView table;
		final boolean isView = artifact instanceof ViewArtifact;
		final Class<? extends TableView> tableType = isView ? wrappedType
				.getTableViewViewType() : wrappedType.getTableViewTableType();

		table = parent.tableParent.addNode(tableType, artifact.getTableName(),
				links(AbstractTypeBind.class), links(parent.tableParentLink,
						TableViewColumns.class));
		final TableView abstractTable = parent.databaseContextNode.addNode(
				TableView.class, artifact.getTableName());
		context.getGraphSession().addLink(AbstractTypeBind.class, table,
				abstractTable, false);
		context.getGraphSession().addLink(parent.tableParentLink,
				parent.tableParent, table, false);
		final TableVo data = new TableVo(parent.databaseContextNode,
				parent.database, table, abstractTable);
		return data;
	}

}
