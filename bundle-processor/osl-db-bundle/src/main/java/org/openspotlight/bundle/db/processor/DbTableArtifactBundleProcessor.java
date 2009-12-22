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
import org.openspotlight.bundle.db.metamodel.node.TableViewTable;
import org.openspotlight.bundle.db.metamodel.node.TableViewView;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ExportedFk;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.domain.TableArtifact;
import org.openspotlight.federation.domain.ViewArtifact;
import org.openspotlight.federation.processing.BundleProcessor;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLContextAlreadyExistsException;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLInvalidCredentialException;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLNodeTypeNotInExistentHierarchy;

public class DbTableArtifactBundleProcessor implements
		BundleProcessor<TableArtifact>, DBConstants {

	private static class TableVo {
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

	public static class UpdateCommand {

	}

	public <A extends Artifact> boolean acceptKindOfArtifact(
			final Class<A> kindOfArtifact) {
		return TableArtifact.class.isAssignableFrom(kindOfArtifact);
	}

	public void beforeProcessArtifact(final TableArtifact artifact) {

	}

	@SuppressWarnings("unchecked")
	private Column createColumn(final ExecutionContext context,
			final SLNode databaseContextNode, final TableView table,
			final TableView abstractTable,
			final org.openspotlight.federation.domain.Column c)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		final Column column = table.addNode(Column.class, c.getName());
		final Column abstractColumn = abstractTable.addNode(Column.class, c
				.getName(), links(TableViewColumns.class,
				AbstractTypeBind.class, ColumnDataType.class,
				ConstraintDatabaseColumn.class, ForeignKey.class), links(
				TableViewColumns.class, ColumnDataType.class,
				ConstraintDatabaseColumn.class, ForeignKey.class));
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

	private void createColumns(final TableArtifact artifact,
			final ExecutionContext context, final SLNode databaseContextNode,
			final Database database, final TableView table,
			final TableView abstractTable)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		for (final org.openspotlight.federation.domain.Column c : artifact
				.getColumns()) {
			final Column column = createColumn(context, databaseContextNode,
					table, abstractTable, c);
			createPrimaryKeys(context, c, column);
			createForeignKeys(context, database, c, column);
		}

	}

	private void createForeignKeys(final ExecutionContext context,
			final Database database,
			final org.openspotlight.federation.domain.Column c,
			final Column column) throws SLNodeTypeNotInExistentHierarchy,
			SLGraphSessionException, SLInvalidCredentialException {
		for (final ExportedFk fk : c.getExportedFks()) {
			final Schema thatSchema = database.addNode(Schema.class, fk
					.getTableSchema());
			final String thatCatalogName = fk.getTableCatalog();
			Catalog thatCatalog = null;
			if (thatCatalogName != null) {
				thatCatalog = thatSchema
						.addNode(Catalog.class, thatCatalogName);
			}
			final SLNode thatTableParent = thatCatalog != null ? thatCatalog
					: thatSchema;
			final TableView thatTable = thatTableParent.addNode(
					TableView.class, fk.getTableName());
			final Column thatColumn = thatTable.addNode(Column.class, fk
					.getColumnName());
			context.getGraphSession().addLink(ForeignKey.class, column,
					thatColumn, false);
			final DatabaseConstraintForeignKey fkNode = thatColumn.addNode(
					DatabaseConstraintForeignKey.class, fk.getFkName());
			context.getGraphSession().addLink(ConstraintDatabaseColumn.class,
					column, fkNode, false);
			context.getGraphSession().addLink(ConstraintDatabaseColumn.class,
					thatColumn, fkNode, false);

		}
	}

	private void createPrimaryKeys(final ExecutionContext context,
			final org.openspotlight.federation.domain.Column c,
			final Column column) throws SLNodeTypeNotInExistentHierarchy,
			SLGraphSessionException, SLInvalidCredentialException {
		if (c.getPks() != null) {
			for (final String pkName : c.getPks()) {
				final DatabaseConstraintPrimaryKey pk = column.addNode(
						DatabaseConstraintPrimaryKey.class, pkName);
				context.getGraphSession().addLink(
						ConstraintDatabaseColumn.class, column, pk, false);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private TableVo createTableData(final TableArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context)
			throws SLContextAlreadyExistsException, SLGraphSessionException,
			SLInvalidCredentialException, SLNodeTypeNotInExistentHierarchy {
		final SLContext databaseContext = context.getGraphSession()
				.createContext(DB_ABSTRACT_CONTEXT);

		final SLNode databaseContextNode = databaseContext.getRootNode();
		final Server server = currentContext.getCurrentNodeGroup().addNode(
				Server.class, artifact.getServerName());
		final Database database = server.addNode(Database.class, artifact
				.getDatabaseName(), links(GroupDatabase.class),
				links(GroupDatabase.class));
		context.getGraphSession().addLink(GroupDatabase.class,
				currentContext.getCurrentNodeGroup(), database, false);

		final Schema schema = database.addNode(Schema.class, artifact
				.getSchemaName(), links(DatabaseSchema.class),
				links(DatabaseSchema.class));

		context.getGraphSession().addLink(DatabaseSchema.class, database,
				schema, false);

		Catalog catalog = null;
		if (artifact.getCatalogName() != null) {
			catalog = schema.addNode(Catalog.class, artifact.getCatalogName(),
					links(SchemaCatalog.class), links(SchemaCatalog.class));
			context.getGraphSession().addLink(SchemaCatalog.class, schema,
					catalog, false);
		}
		final TableView table;
		final boolean isView = artifact instanceof ViewArtifact;
		final Class<? extends TableView> tableType = isView ? TableViewView.class
				: TableViewTable.class;
		final Class<? extends SLLink> tableParentLink = catalog != null ? CatalogTableView.class
				: SchemaTableView.class;

		final SLNode tableParent = catalog != null ? catalog : schema;
		table = tableParent.addNode(tableType, artifact.getTableName(),
				links(tableParentLink, TableViewColumns.class,
						AbstractTypeBind.class), links(tableParentLink,
						TableViewColumns.class));
		final TableView abstractTable = databaseContextNode.addNode(
				TableView.class, artifact.getTableName());
		context.getGraphSession().addLink(AbstractTypeBind.class, table,
				abstractTable, false);
		context.getGraphSession().addLink(tableParentLink, tableParent, table,
				false);
		final TableVo data = new TableVo(databaseContextNode, database, table,
				abstractTable);
		return data;
	}

	public void didFinishProcessing(final ArtifactChanges<TableArtifact> changes) {

	}

	public void didFinishToProcessArtifact(final TableArtifact artifact,
			final LastProcessStatus status) {

	}

	public Class<TableArtifact> getArtifactType() {
		return TableArtifact.class;
	}

	public SaveBehavior getSaveBehavior() {
		return SaveBehavior.PER_ARTIFACT;
	}

	public LastProcessStatus processArtifact(final TableArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) throws Exception {
		final TableVo data = createTableData(artifact, currentContext, context);
		createColumns(artifact, context, data.databaseContextNode,
				data.database, data.table, data.abstractTable);
		return LastProcessStatus.PROCESSED;
	}

	public void selectArtifactsToBeProcessed(
			final CurrentProcessorContext currentContext,
			final ExecutionContext context,
			final ArtifactChanges<TableArtifact> changes,
			final ArtifactsToBeProcessed<TableArtifact> toBeReturned) {

	}

}
