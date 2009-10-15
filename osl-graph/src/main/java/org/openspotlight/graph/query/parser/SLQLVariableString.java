package org.openspotlight.graph.query.parser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SLQLVariableString extends SLQLVariable {

    protected Set<String> domainValue = null;

    public SLQLVariableString(
                               String name ) {
        super(name);
        this.domainValue = new HashSet<String>();
    }

    public boolean hasDomainValues() {
        if (domainValue.size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public Collection<String> getDomainValues() {
        return domainValue;
    }

    @Override
    public void addDomainValue( Object value ) {
        if (isValidValue(value)) {
            domainValue.add((String)value);
        }
    }

    @Override
    public String getValue() {
        return (String)value;
    }

    @Override
    public boolean isValidValue( Object value ) {
        if (value instanceof String) {
            return true;
        }
        return false;
    }
}
