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
import org.openspotlight.bundle.db.metamodel.node.impl.Db2Catalog;
import org.openspotlight.bundle.db.metamodel.node.impl.Db2Column;
import org.openspotlight.bundle.db.metamodel.node.impl.Db2DataType;
import org.openspotlight.bundle.db.metamodel.node.impl.Db2Database;
import org.openspotlight.bundle.db.metamodel.node.impl.Db2DatabaseConstraintForeignKey;
import org.openspotlight.bundle.db.metamodel.node.impl.Db2DatabaseConstraintPrimaryKey;
import org.openspotlight.bundle.db.metamodel.node.impl.Db2Schema;
import org.openspotlight.bundle.db.metamodel.node.impl.Db2Server;
import org.openspotlight.bundle.db.metamodel.node.impl.Db2TableView;
import org.openspotlight.bundle.db.metamodel.node.impl.Db2TableViewTable;
import org.openspotlight.bundle.db.metamodel.node.impl.Db2TableViewView;
import org.openspotlight.bundle.db.processor.DbWrappedType;

public enum Db2WrappedType implements DbWrappedType {

	INSTANCE;

	public Class<? extends Catalog> getCatalogType() {
		return Db2Catalog.class;
	}

	public Class<? extends Column> getColumnType() {

		return Db2Column.class;
	}

	public Class<? extends DatabaseConstraintForeignKey> getDatabaseConstraintForeignKeyType() {

		return Db2DatabaseConstraintForeignKey.class;
	}

	public Class<? extends DatabaseConstraintPrimaryKey> getDatabaseConstraintPrimaryKeyType() {

		return Db2DatabaseConstraintPrimaryKey.class;
	}

	public Class<? extends Database> getDatabaseType() {

		return Db2Database.class;
	}

	public Class<? extends DataType> getDataTypeType() {

		return Db2DataType.class;
	}

	public Class<? extends Schema> getSchemaType() {

		return Db2Schema.class;
	}

	public Class<? extends Server> getServerType() {

		return Db2Server.class;
	}

	public Class<? extends TableViewTable> getTableViewTableType() {

		return Db2TableViewTable.class;
	}

	public Class<? extends TableView> getTableViewType() {

		return Db2TableView.class;
	}

	public Class<? extends TableViewView> getTableViewViewType() {

		return Db2TableViewView.class;
	}

}