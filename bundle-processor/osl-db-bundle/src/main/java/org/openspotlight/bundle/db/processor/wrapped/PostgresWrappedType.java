package org.openspotlight.bundle.db.processor.wrapped;

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
import org.openspotlight.bundle.db.metamodel.node.impl.PostgresCatalog;
import org.openspotlight.bundle.db.metamodel.node.impl.PostgresColumn;
import org.openspotlight.bundle.db.metamodel.node.impl.PostgresDataType;
import org.openspotlight.bundle.db.metamodel.node.impl.PostgresDatabase;
import org.openspotlight.bundle.db.metamodel.node.impl.PostgresDatabaseConstraintForeignKey;
import org.openspotlight.bundle.db.metamodel.node.impl.PostgresDatabaseConstraintPrimaryKey;
import org.openspotlight.bundle.db.metamodel.node.impl.PostgresSchema;
import org.openspotlight.bundle.db.metamodel.node.impl.PostgresServer;
import org.openspotlight.bundle.db.metamodel.node.impl.PostgresTableView;
import org.openspotlight.bundle.db.metamodel.node.impl.PostgresTableViewTable;
import org.openspotlight.bundle.db.metamodel.node.impl.PostgresTableViewView;
import org.openspotlight.bundle.db.processor.DbWrappedType;

public enum PostgresWrappedType implements DbWrappedType {

	INSTANCE;

	public Class<? extends Catalog> getCatalogType() {
		return PostgresCatalog.class;
	}

	public Class<? extends Column> getColumnType() {

		return PostgresColumn.class;
	}

	public Class<? extends DatabaseConstraintForeignKey> getDatabaseConstraintForeignKeyType() {

		return PostgresDatabaseConstraintForeignKey.class;
	}

	public Class<? extends DatabaseConstraintPrimaryKey> getDatabaseConstraintPrimaryKeyType() {

		return PostgresDatabaseConstraintPrimaryKey.class;
	}

	public Class<? extends Database> getDatabaseType() {

		return PostgresDatabase.class;
	}

	public Class<? extends DataType> getDataTypeType() {

		return PostgresDataType.class;
	}

	public Class<? extends Schema> getSchemaType() {

		return PostgresSchema.class;
	}

	public Class<? extends Server> getServerType() {

		return PostgresServer.class;
	}

	public Class<? extends TableViewTable> getTableViewTableType() {

		return PostgresTableViewTable.class;
	}

	public Class<? extends TableView> getTableViewType() {

		return PostgresTableView.class;
	}

	public Class<? extends TableViewView> getTableViewViewType() {

		return PostgresTableViewView.class;
	}

}