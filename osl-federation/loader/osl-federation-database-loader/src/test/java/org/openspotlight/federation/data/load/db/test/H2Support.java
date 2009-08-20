package org.openspotlight.federation.data.load.db.test;

import java.sql.Connection;

public class H2Support {
	public static void fillDatabaseArtifacts(Connection conn) throws Exception {
		conn
				.prepareStatement(
						"create alias exampleFunction for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.increment\"") //$NON-NLS-1$
				.execute();
		conn
				.prepareStatement(
						"create alias exampleProcedure for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.flagProcedure\" ") //$NON-NLS-1$
				.execute();
		conn
				.prepareStatement(
						"create table exampleTable(i int not null, last_i_plus_2 int, s smallint, f float, dp double precision, v varchar(10) not null)") //$NON-NLS-1$
				.execute();
		conn.prepareStatement("create index exampleIndex on exampleTable(i)") //$NON-NLS-1$
				.execute();
		conn
				.prepareStatement(
						"create trigger exampleTrigger before insert on exampleTable for each row call \"org.openspotlight.federation.data.load.db.test.H2Trigger\"") //$NON-NLS-1$
				.execute();
		conn
				.prepareStatement(
						"create view exampleView (s_was_i, dp_was_s, i_was_f, f_was_dp) as select i,s,f,dp from exampleTable") //$NON-NLS-1$
				.execute();
	}
}
