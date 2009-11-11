package org.openspotlight.federation.data.load.db.test;

import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createH2DbConfiguration;

import java.sql.Connection;
import java.util.EnumSet;
import java.util.Set;

import org.junit.Before;
import org.openspotlight.federation.data.load.db.ScriptType;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.Repository;

@SuppressWarnings( "all" )
public class H2DatabaseStreamTest extends DatabaseStreamTest {

    @Before
    public void cleanDatabaseFiles() throws Exception {
        delete("./target/test-data/H2DatabaseStreamTest"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DbArtifactSource createValidConfigurationWithMappings() {
        final Repository repository = createH2DbConfiguration("H2DatabaseStreamTest"); //$NON-NLS-1$
        return (DbArtifactSource)repository.getGroups().get("h2 Group").getArtifactSources().iterator().next(); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillDatabase( final Connection conn ) throws Exception {
        H2Support.fillDatabaseArtifacts(conn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<ScriptType> typesToAssert() {
        return EnumSet.of(ScriptType.VIEW, ScriptType.FUNCTION, ScriptType.PROCEDURE, ScriptType.INDEX, ScriptType.TABLE,
                          ScriptType.TRIGGER);
    }

}
