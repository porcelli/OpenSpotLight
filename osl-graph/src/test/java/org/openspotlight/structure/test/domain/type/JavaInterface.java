package org.openspotlight.structure.test.domain.type;

public class JavaInterface extends JavaClass {
    private static final long serialVersionUID = -3610192940792363290L;

    public JavaInterface(
                          String key, Long contextHandle ) {
        super(key, contextHandle);
    }

    @Override
    public String getLabel() {
        return "Java Interface";
    }
}
