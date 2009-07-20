package org.openspotlight.structure.test.domain.type;

import java.text.Collator;

import org.openspotlight.structure.elements.SLSimpleType;

public class TableView extends SLSimpleType {
    /**
     * 
     */
    private static final long serialVersionUID = -1383237011532032209L;

    @Override
    public String getLabel() {
        return "TableView";
    }

    public TableView(
                      String key, long contextHandle ) {
        super(key, contextHandle, Collator.PRIMARY);
    }

    public TableView() {
        super();
        setCollatorLevel(Collator.PRIMARY);
    }

    public TableView(
                      String key, long contextHandle, int collatorLevel ) {
        super(key, contextHandle, Collator.PRIMARY);
    }
}
