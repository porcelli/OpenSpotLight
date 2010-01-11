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
import org.openspotlight.bundle.db.metamodel.node.impl.$dbName$Catalog;
import org.openspotlight.bundle.db.metamodel.node.impl.$dbName$Column;
import org.openspotlight.bundle.db.metamodel.node.impl.$dbName$DataType;
import org.openspotlight.bundle.db.metamodel.node.impl.$dbName$Database;
import org.openspotlight.bundle.db.metamodel.node.impl.$dbName$DatabaseConstraintForeignKey;
import org.openspotlight.bundle.db.metamodel.node.impl.$dbName$DatabaseConstraintPrimaryKey;
import org.openspotlight.bundle.db.metamodel.node.impl.$dbName$Schema;
import org.openspotlight.bundle.db.metamodel.node.impl.$dbName$Server;
import org.openspotlight.bundle.db.metamodel.node.impl.$dbName$TableView;
import org.openspotlight.bundle.db.metamodel.node.impl.$dbName$TableViewTable;
import org.openspotlight.bundle.db.metamodel.node.impl.$dbName$TableViewView;
import org.openspotlight.bundle.db.processor.DbWrappedType;

public enum $dbName$WrappedType implements DbWrappedType {

	INSTANCE;

	public Class<? extends Catalog> getCatalogType() {
		return $dbName$Catalog.class;
	}

	public Class<? extends Column> getColumnType() {

		return $dbName$Column.class;
	}

	public Class<? extends DatabaseConstraintForeignKey> getDatabaseConstraintForeignKeyType() {

		return $dbName$DatabaseConstraintForeignKey.class;
	}

	public Class<? extends DatabaseConstraintPrimaryKey> getDatabaseConstraintPrimaryKeyType() {

		return $dbName$DatabaseConstraintPrimaryKey.class;
	}

	public Class<? extends Database> getDatabaseType() {

		return $dbName$Database.class;
	}

	public Class<? extends DataType> getDataTypeType() {

		return $dbName$DataType.class;
	}

	public Class<? extends Schema> getSchemaType() {

		return $dbName$Schema.class;
	}

	public Class<? extends Server> getServerType() {

		return $dbName$Server.class;
	}

	public Class<? extends TableViewTable> getTableViewTableType() {

		return $dbName$TableViewTable.class;
	}

	public Class<? extends TableView> getTableViewType() {

		return $dbName$TableView.class;
	}

	public Class<? extends TableViewView> getTableViewViewType() {

		return $dbName$TableViewView.class;
	}

}
