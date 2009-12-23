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
import org.openspotlight.bundle.db.metamodel.node.impl.SqlServerCatalog;
import org.openspotlight.bundle.db.metamodel.node.impl.SqlServerColumn;
import org.openspotlight.bundle.db.metamodel.node.impl.SqlServerDataType;
import org.openspotlight.bundle.db.metamodel.node.impl.SqlServerDatabase;
import org.openspotlight.bundle.db.metamodel.node.impl.SqlServerDatabaseConstraintForeignKey;
import org.openspotlight.bundle.db.metamodel.node.impl.SqlServerDatabaseConstraintPrimaryKey;
import org.openspotlight.bundle.db.metamodel.node.impl.SqlServerSchema;
import org.openspotlight.bundle.db.metamodel.node.impl.SqlServerServer;
import org.openspotlight.bundle.db.metamodel.node.impl.SqlServerTableView;
import org.openspotlight.bundle.db.metamodel.node.impl.SqlServerTableViewTable;
import org.openspotlight.bundle.db.metamodel.node.impl.SqlServerTableViewView;
import org.openspotlight.bundle.db.processor.DbWrappedType;

public enum SqlServerWrappedType implements DbWrappedType {

	INSTANCE;

	public Class<? extends Catalog> getCatalogType() {
		return SqlServerCatalog.class;
	}

	public Class<? extends Column> getColumnType() {

		return SqlServerColumn.class;
	}

	public Class<? extends DatabaseConstraintForeignKey> getDatabaseConstraintForeignKeyType() {

		return SqlServerDatabaseConstraintForeignKey.class;
	}

	public Class<? extends DatabaseConstraintPrimaryKey> getDatabaseConstraintPrimaryKeyType() {

		return SqlServerDatabaseConstraintPrimaryKey.class;
	}

	public Class<? extends Database> getDatabaseType() {

		return SqlServerDatabase.class;
	}

	public Class<? extends DataType> getDataTypeType() {

		return SqlServerDataType.class;
	}

	public Class<? extends Schema> getSchemaType() {

		return SqlServerSchema.class;
	}

	public Class<? extends Server> getServerType() {

		return SqlServerServer.class;
	}

	public Class<? extends TableViewTable> getTableViewTableType() {

		return SqlServerTableViewTable.class;
	}

	public Class<? extends TableView> getTableViewType() {

		return SqlServerTableView.class;
	}

	public Class<? extends TableViewView> getTableViewViewType() {

		return SqlServerTableViewView.class;
	}

}