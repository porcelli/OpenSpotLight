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
import org.openspotlight.bundle.db.metamodel.node.impl.OracleCatalog;
import org.openspotlight.bundle.db.metamodel.node.impl.OracleColumn;
import org.openspotlight.bundle.db.metamodel.node.impl.OracleDataType;
import org.openspotlight.bundle.db.metamodel.node.impl.OracleDatabase;
import org.openspotlight.bundle.db.metamodel.node.impl.OracleDatabaseConstraintForeignKey;
import org.openspotlight.bundle.db.metamodel.node.impl.OracleDatabaseConstraintPrimaryKey;
import org.openspotlight.bundle.db.metamodel.node.impl.OracleSchema;
import org.openspotlight.bundle.db.metamodel.node.impl.OracleServer;
import org.openspotlight.bundle.db.metamodel.node.impl.OracleTableView;
import org.openspotlight.bundle.db.metamodel.node.impl.OracleTableViewTable;
import org.openspotlight.bundle.db.metamodel.node.impl.OracleTableViewView;
import org.openspotlight.bundle.db.processor.DbWrappedType;

public enum OracleWrappedType implements DbWrappedType {

	INSTANCE;

	public Class<? extends Catalog> getCatalogType() {
		return OracleCatalog.class;
	}

	public Class<? extends Column> getColumnType() {

		return OracleColumn.class;
	}

	public Class<? extends DatabaseConstraintForeignKey> getDatabaseConstraintForeignKeyType() {

		return OracleDatabaseConstraintForeignKey.class;
	}

	public Class<? extends DatabaseConstraintPrimaryKey> getDatabaseConstraintPrimaryKeyType() {

		return OracleDatabaseConstraintPrimaryKey.class;
	}

	public Class<? extends Database> getDatabaseType() {

		return OracleDatabase.class;
	}

	public Class<? extends DataType> getDataTypeType() {

		return OracleDataType.class;
	}

	public Class<? extends Schema> getSchemaType() {

		return OracleSchema.class;
	}

	public Class<? extends Server> getServerType() {

		return OracleServer.class;
	}

	public Class<? extends TableViewTable> getTableViewTableType() {

		return OracleTableViewTable.class;
	}

	public Class<? extends TableView> getTableViewType() {

		return OracleTableView.class;
	}

	public Class<? extends TableViewView> getTableViewViewType() {

		return OracleTableViewView.class;
	}

}