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
import org.openspotlight.bundle.db.metamodel.node.impl.H2Catalog;
import org.openspotlight.bundle.db.metamodel.node.impl.H2Column;
import org.openspotlight.bundle.db.metamodel.node.impl.H2DataType;
import org.openspotlight.bundle.db.metamodel.node.impl.H2Database;
import org.openspotlight.bundle.db.metamodel.node.impl.H2DatabaseConstraintForeignKey;
import org.openspotlight.bundle.db.metamodel.node.impl.H2DatabaseConstraintPrimaryKey;
import org.openspotlight.bundle.db.metamodel.node.impl.H2Schema;
import org.openspotlight.bundle.db.metamodel.node.impl.H2Server;
import org.openspotlight.bundle.db.metamodel.node.impl.H2TableView;
import org.openspotlight.bundle.db.metamodel.node.impl.H2TableViewTable;
import org.openspotlight.bundle.db.metamodel.node.impl.H2TableViewView;
import org.openspotlight.bundle.db.processor.DbWrappedType;

public enum H2WrappedType implements DbWrappedType {

	INSTANCE;

	public Class<? extends Catalog> getCatalogType() {
		return H2Catalog.class;
	}

	public Class<? extends Column> getColumnType() {

		return H2Column.class;
	}

	public Class<? extends DatabaseConstraintForeignKey> getDatabaseConstraintForeignKeyType() {

		return H2DatabaseConstraintForeignKey.class;
	}

	public Class<? extends DatabaseConstraintPrimaryKey> getDatabaseConstraintPrimaryKeyType() {

		return H2DatabaseConstraintPrimaryKey.class;
	}

	public Class<? extends Database> getDatabaseType() {

		return H2Database.class;
	}

	public Class<? extends DataType> getDataTypeType() {

		return H2DataType.class;
	}

	public Class<? extends Schema> getSchemaType() {

		return H2Schema.class;
	}

	public Class<? extends Server> getServerType() {

		return H2Server.class;
	}

	public Class<? extends TableViewTable> getTableViewTableType() {

		return H2TableViewTable.class;
	}

	public Class<? extends TableView> getTableViewType() {

		return H2TableView.class;
	}

	public Class<? extends TableViewView> getTableViewViewType() {

		return H2TableViewView.class;
	}

}