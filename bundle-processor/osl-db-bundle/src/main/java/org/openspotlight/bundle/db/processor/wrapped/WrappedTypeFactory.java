package org.openspotlight.bundle.db.processor.wrapped;

import org.openspotlight.bundle.db.processor.DbWrappedType;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.DatabaseType;

public enum WrappedTypeFactory {

	INSTANCE;

	public DbWrappedType createByType(final DatabaseType type) {
		Assertions.checkNotNull("type", type);
		if (DatabaseType.DB2.equals(type)) {
			return Db2WrappedType.INSTANCE;
		}
		if (DatabaseType.H2.equals(type)) {
			return H2WrappedType.INSTANCE;
		}
		if (DatabaseType.MY_SQL.equals(type)) {
			return MySqlWrappedType.INSTANCE;
		}
		if (DatabaseType.ORACLE.equals(type)) {
			return OracleWrappedType.INSTANCE;
		}
		if (DatabaseType.ORACLE9.equals(type)) {
			return Oracle9WrappedType.INSTANCE;
		}
		if (DatabaseType.POSTGRES.equals(type)) {
			return PostgresWrappedType.INSTANCE;
		}
		if (DatabaseType.SQL_SERVER.equals(type)) {
			return SqlServerWrappedType.INSTANCE;
		}
		throw Exceptions.logAndReturn(new IllegalArgumentException());
	}
}
