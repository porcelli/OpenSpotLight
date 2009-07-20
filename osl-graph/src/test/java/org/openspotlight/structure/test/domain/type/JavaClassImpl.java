package org.openspotlight.structure.test.domain.type;

public class JavaClassImpl extends JavaClass {
    private static final long serialVersionUID = 3950529030206032716L;

    public JavaClassImpl() {
    }

    public JavaClassImpl(
                          String key, Long contextHandle ) {
        super(key, contextHandle);
    }

    //@Override
    public String getLabel() {
        return "Java Class Impl";
    }
}
