package org.openspotlight.slql.parser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SLQLVariableFloat extends SLQLVariable {

    protected Set<Float> domainValue = null;

    public SLQLVariableFloat(
                                String name ) {
        super(name);
        this.domainValue = new HashSet<Float>();
    }

    public boolean hasDomainValues() {
        if (domainValue.size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public Collection<Float> getDomainValues() {
        return domainValue;
    }

    @Override
    public void addDomainValue( Object value ) {
        if (isValidValue(value)) {
            domainValue.add((Float)value);
        }
    }

    @Override
    public Float getValue() {
        return (Float)value;
    }

    @Override
    public boolean isValidValue( Object value ) {
        if (value.getClass().getName().equals(float.class.getName())) {
            return true;
        }
        if (value instanceof Float) {
            return true;
        }
        return false;
    }
}
