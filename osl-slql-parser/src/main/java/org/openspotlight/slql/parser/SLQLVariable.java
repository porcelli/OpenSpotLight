package org.openspotlight.slql.parser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SLQLVariable<DT> {

    private String  name           = null;
    private String  displayMessage = null;
    private Set<DT> domainValue    = new HashSet<DT>();
    private DT      value          = null;

    public SLQLVariable(
                         String name ) {
        this.name = name;
        this.displayMessage = name;
    }

    public void addDomainValue( DT value ) {
        domainValue.add(value);
    }

    public boolean hasDomainValues() {
        if (domainValue.size() == 0){
            return false;
        }
        return true;
    }

    public Collection<DT> getDomainValues() {
        return domainValue;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage( String displayMessage ) {
        this.displayMessage = displayMessage;
    }

    public boolean isValidValue( DT value ) {
        if (!hasDomainValues()) {
            return true;
        }
        if (domainValue.contains(value)) {
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals( Object obj ) {
        if (obj instanceof SLQLVariable<?>) {
            return name.equalsIgnoreCase(((SLQLVariable<?>)obj).getName());
        }
        return false;
    }

    @SuppressWarnings( "unchecked" )
    public void setValue( Object value ) {
        this.value = (DT)value;
    }

    public DT getValue() {
        return (DT)value;
    }
}
