package org.openspotlight.structure.test.domain.type;

import java.text.Collator;

public class DatabaseTable extends TableView {
    private static final long serialVersionUID = 6353666263882309825L;

    public DatabaseTable() {
        super();
    }

    public DatabaseTable(
                          String key, long contextHandle ) {
        super(key, contextHandle, Collator.PRIMARY);
    }

    public DatabaseTable(
                          String key, long contextHandle, int collatorLevel ) {
        super(key, contextHandle, Collator.PRIMARY);
    }
}
