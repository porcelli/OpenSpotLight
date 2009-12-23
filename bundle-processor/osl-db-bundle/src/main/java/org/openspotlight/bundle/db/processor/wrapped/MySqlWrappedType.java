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
import org.openspotlight.bundle.db.metamodel.node.impl.MySqlCatalog;
import org.openspotlight.bundle.db.metamodel.node.impl.MySqlColumn;
import org.openspotlight.bundle.db.metamodel.node.impl.MySqlDataType;
import org.openspotlight.bundle.db.metamodel.node.impl.MySqlDatabase;
import org.openspotlight.bundle.db.metamodel.node.impl.MySqlDatabaseConstraintForeignKey;
import org.openspotlight.bundle.db.metamodel.node.impl.MySqlDatabaseConstraintPrimaryKey;
import org.openspotlight.bundle.db.metamodel.node.impl.MySqlSchema;
import org.openspotlight.bundle.db.metamodel.node.impl.MySqlServer;
import org.openspotlight.bundle.db.metamodel.node.impl.MySqlTableView;
import org.openspotlight.bundle.db.metamodel.node.impl.MySqlTableViewTable;
import org.openspotlight.bundle.db.metamodel.node.impl.MySqlTableViewView;
import org.openspotlight.bundle.db.processor.DbWrappedType;

public enum MySqlWrappedType implements DbWrappedType {

	INSTANCE;

	public Class<? extends Catalog> getCatalogType() {
		return MySqlCatalog.class;
	}

	public Class<? extends Column> getColumnType() {

		return MySqlColumn.class;
	}

	public Class<? extends DatabaseConstraintForeignKey> getDatabaseConstraintForeignKeyType() {

		return MySqlDatabaseConstraintForeignKey.class;
	}

	public Class<? extends DatabaseConstraintPrimaryKey> getDatabaseConstraintPrimaryKeyType() {

		return MySqlDatabaseConstraintPrimaryKey.class;
	}

	public Class<? extends Database> getDatabaseType() {

		return MySqlDatabase.class;
	}

	public Class<? extends DataType> getDataTypeType() {

		return MySqlDataType.class;
	}

	public Class<? extends Schema> getSchemaType() {

		return MySqlSchema.class;
	}

	public Class<? extends Server> getServerType() {

		return MySqlServer.class;
	}

	public Class<? extends TableViewTable> getTableViewTableType() {

		return MySqlTableViewTable.class;
	}

	public Class<? extends TableView> getTableViewType() {

		return MySqlTableView.class;
	}

	public Class<? extends TableViewView> getTableViewViewType() {

		return MySqlTableViewView.class;
	}

}