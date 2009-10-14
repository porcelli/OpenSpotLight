package org.openspotlight.slql.parser;

import java.util.Collection;

import org.openspotlight.common.util.Exceptions;

public abstract class SLQLVariable {

    protected String name           = null;
    protected String displayMessage = null;
    protected Object value          = null;

    public SLQLVariable(
                         String name ) {
        this.name = name;
        this.displayMessage = name;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage( String displayMessage ) {
        this.displayMessage = displayMessage;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals( Object obj ) {
        if (obj instanceof SLQLVariable) {
            return name.equalsIgnoreCase(((SLQLVariable)obj).getName());
        }
        return false;
    }

    public void setValue( Object value ) {
        if (isValidValue(value)) {
            this.value = (Integer)value;
        } else {
            Exceptions.logAndThrow(new IllegalArgumentException("Variable value invalid data type."));
        }
    }

    public void addAllDomainValue( Collection<Object> values ){
        for (Object activeValue : values) {
            addDomainValue(activeValue);
        }
    }

    public abstract void addDomainValue( Object value );

    public abstract boolean hasDomainValues();

    public abstract boolean isValidValue( Object value );

    public abstract Collection<?> getDomainValues();

    public abstract Object getValue();
}
