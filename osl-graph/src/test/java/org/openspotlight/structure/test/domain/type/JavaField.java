package org.openspotlight.structure.test.domain.type;

import org.openspotlight.structure.elements.SLSimpleType;

public class JavaField extends SLSimpleType {
    private static final long serialVersionUID = -5929270122711115611L;

    public JavaField(
                      String key, Long contextHandle ) {
        super(key, contextHandle);
    }

    //@Override
    public String getLabel() {
        return "Java Field";
    }
}
