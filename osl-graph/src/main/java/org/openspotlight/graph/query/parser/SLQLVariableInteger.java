package org.openspotlight.graph.query.parser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SLQLVariableInteger extends SLQLVariable {

    protected Set<Integer> domainValue = null;

    public SLQLVariableInteger(
                                String name ) {
        super(name);
        this.domainValue = new HashSet<Integer>();
    }

    public boolean hasDomainValues() {
        if (domainValue.size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public Collection<Integer> getDomainValues() {
        return domainValue;
    }

    @Override
    public void addDomainValue( Object value ) {
        if (isValidValue(value)) {
            domainValue.add((Integer)value);
        }
    }

    @Override
    public Integer getValue() {
        return (Integer)value;
    }

    @Override
    public boolean isValidValue( Object value ) {
        if (value.getClass().getName().equals(int.class.getName())) {
            return true;
        }
        if (value instanceof Integer) {
            return true;
        }
        return false;
    }
}
