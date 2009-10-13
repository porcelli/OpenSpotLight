package org.openspotlight.slql.parser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SLQLVariableBoolean extends SLQLVariable {

    Set<Boolean> domainValue = new HashSet<Boolean>(2);

    public SLQLVariableBoolean(
                                String name ) {
        super(name);
        domainValue.add(true);
        domainValue.add(false);
    }

    public boolean hasDomainValues() {
        return true;
    }

    @Override
    public Collection<Boolean> getDomainValues() {
        return domainValue;
    }

    @Override
    public void addDomainValue( Object value ) {
    }

    @Override
    public Boolean getValue() {
        return (Boolean)value;
    }

    @Override
    public boolean isValidValue( Object value ) {
        if (value.getClass().getName().equals(boolean.class.getName())) {
            return true;
        }
        if (value instanceof Boolean) {
            return true;
        }
        return false;
    }
}
