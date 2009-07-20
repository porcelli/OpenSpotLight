package org.openspotlight.structure.test.domain.type;

import java.awt.Color;

import org.openspotlight.structure.elements.SLSimpleType;

public class JavaClass extends SLSimpleType {
    private static final long serialVersionUID = 7607953209397727037L;

    public JavaClass() {
    }

    public JavaClass(
                      String key, Long contextHandle ) {
        super(key, contextHandle);
        addRenderHint("SHAPE", "rectangle");
        addRenderHint("FOREGROUND_COLOR", new Color(255, 255, 255));
    }

    @Override
    public String getLabel() {
        return "Java Class";
    }
}
