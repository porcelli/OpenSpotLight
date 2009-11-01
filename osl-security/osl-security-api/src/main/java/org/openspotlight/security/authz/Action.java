package org.openspotlight.security.authz;

public enum Action {

    READ,
    WRITE(READ),
    DELETE(WRITE),
    OPERATE(DELETE),
    MANAGE(OPERATE);

    private final Action parent;

    private Action() {
        this.parent = null;
    }

    private Action(
                    Action parent ) {
        this.parent = parent;
    }

    /**
     * @return the parent type
     */
    public Action getParent() {
        return this.parent;
    }

}
