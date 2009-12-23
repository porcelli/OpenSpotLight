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
import org.openspotlight.bundle.db.metamodel.node.impl.Oracle9Catalog;
import org.openspotlight.bundle.db.metamodel.node.impl.Oracle9Column;
import org.openspotlight.bundle.db.metamodel.node.impl.Oracle9DataType;
import org.openspotlight.bundle.db.metamodel.node.impl.Oracle9Database;
import org.openspotlight.bundle.db.metamodel.node.impl.Oracle9DatabaseConstraintForeignKey;
import org.openspotlight.bundle.db.metamodel.node.impl.Oracle9DatabaseConstraintPrimaryKey;
import org.openspotlight.bundle.db.metamodel.node.impl.Oracle9Schema;
import org.openspotlight.bundle.db.metamodel.node.impl.Oracle9Server;
import org.openspotlight.bundle.db.metamodel.node.impl.Oracle9TableView;
import org.openspotlight.bundle.db.metamodel.node.impl.Oracle9TableViewTable;
import org.openspotlight.bundle.db.metamodel.node.impl.Oracle9TableViewView;
import org.openspotlight.bundle.db.processor.DbWrappedType;

public enum Oracle9WrappedType implements DbWrappedType {

	INSTANCE;

	public Class<? extends Catalog> getCatalogType() {
		return Oracle9Catalog.class;
	}

	public Class<? extends Column> getColumnType() {

		return Oracle9Column.class;
	}

	public Class<? extends DatabaseConstraintForeignKey> getDatabaseConstraintForeignKeyType() {

		return Oracle9DatabaseConstraintForeignKey.class;
	}

	public Class<? extends DatabaseConstraintPrimaryKey> getDatabaseConstraintPrimaryKeyType() {

		return Oracle9DatabaseConstraintPrimaryKey.class;
	}

	public Class<? extends Database> getDatabaseType() {

		return Oracle9Database.class;
	}

	public Class<? extends DataType> getDataTypeType() {

		return Oracle9DataType.class;
	}

	public Class<? extends Schema> getSchemaType() {

		return Oracle9Schema.class;
	}

	public Class<? extends Server> getServerType() {

		return Oracle9Server.class;
	}

	public Class<? extends TableViewTable> getTableViewTableType() {

		return Oracle9TableViewTable.class;
	}

	public Class<? extends TableView> getTableViewType() {

		return Oracle9TableView.class;
	}

	public Class<? extends TableViewView> getTableViewViewType() {

		return Oracle9TableViewView.class;
	}

}