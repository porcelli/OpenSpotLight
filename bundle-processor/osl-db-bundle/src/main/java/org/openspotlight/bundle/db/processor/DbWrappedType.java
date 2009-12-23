package org.openspotlight.bundle.db.processor;

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

public interface DbWrappedType {

	public abstract Class<? extends Catalog> getCatalogType();

	public abstract Class<? extends Column> getColumnType();

	public abstract Class<? extends DatabaseConstraintForeignKey> getDatabaseConstraintForeignKeyType();

	public abstract Class<? extends DatabaseConstraintPrimaryKey> getDatabaseConstraintPrimaryKeyType();

	public abstract Class<? extends Database> getDatabaseType();

	public abstract Class<? extends DataType> getDataTypeType();

	public abstract Class<? extends Schema> getSchemaType();

	public abstract Class<? extends Server> getServerType();

	public abstract Class<? extends TableViewTable> getTableViewTableType();

	public abstract Class<? extends TableView> getTableViewType();

	public abstract Class<? extends TableViewView> getTableViewViewType();

}