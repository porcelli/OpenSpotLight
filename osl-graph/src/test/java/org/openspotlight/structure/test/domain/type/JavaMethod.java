package org.openspotlight.structure.test.domain.type;

import org.openspotlight.structure.elements.SLSimpleType;

public class JavaMethod extends SLSimpleType {
    private static final long serialVersionUID = 3989151058866630710L;

    public JavaMethod() {
        super();
    }

    public JavaMethod(
                       String key, Long contextHandle ) {
        super(key, contextHandle);
    }

    @Override
    public String getLabel() {
        return null;
    }
}
