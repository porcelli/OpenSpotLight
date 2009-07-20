/*
 * Copyright (c) 2008, Alexandre Porcelli or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Alexandre Porcelli.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package org.openspotlight.structure.elements;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents a LineReference on SpotLight.
 * 
 * A LineReference is composed by: an area (BeginLine, BeginColumn, EndColumn and EndLine), an element handle (instance of
 * SLElement) , the type of element (Object or Link) and the Statement name.
 * 
 * @author Vinicius Carvalho
 * 
 * @see SLElement
 * @see LineType
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class LineReference implements Serializable {
    private static final long serialVersionUID = 5649233490146970179L;
    @XmlAttribute( name = "BeginLine" )
    private int beginLine;
    @XmlAttribute( name = "BeginColumn" )
    private int beginColumn;
    @XmlAttribute( name = "EndLine" )
    private int endLine;
    @XmlAttribute( name = "EndColumn" )
    private int endColumn;
    @XmlAttribute( name = "ElementHandle")
    private long elementHandle;
    @XmlAttribute( name = "Statement" )
    private String statement;
    @XmlAttribute( name = "ElementType")
    private LineType type;

    public LineReference() {
    }

    public LineReference(
                          SLElement element, int beginLine, int endLine, int beginColumn, int endColumn, String statement ) {
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.beginColumn = beginColumn;
        this.endColumn = endColumn;
        this.statement = statement;
        this.elementHandle = element.getHandle();
        this.type = (element instanceof SLLinkType) ? LineType.LINK : LineType.XParseObject;
    }

    /**
     * Getter of BeginLine
     * 
     * @return begin line
     */
    public int getBeginLine() {
        return beginLine;
    }

    /**
     * Setter of BeginLine
     * 
     * @param beginLine line start
     */
    public void setBeginLine( int beginLine ) {
        this.beginLine = beginLine;
    }

    /**
     * Getter of BeginColumn
     * 
     * @return begin column
     */
    public int getBeginColumn() {
        return beginColumn;
    }

    /**
     * Setter of BeginColumn
     * 
     * @param beginColumn begin column
     */
    public void setBeginColumn( int beginColumn ) {
        this.beginColumn = beginColumn;
    }

    /**
     * Getter of EndLine
     * 
     * @return end line
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * Setter of EndLine
     * 
     * @param endLine end line
     */
    public void setEndLine( int endLine ) {
        this.endLine = endLine;
    }

    /**
     * Getter of EndColumn
     * 
     * @return end column
     */
    public int getEndColumn() {
        return endColumn;
    }

    /**
     * Setter of EndColumn
     * 
     * @param endColumn end column
     */
    public void setEndColumn( int endColumn ) {
        this.endColumn = endColumn;
    }

    /**
     * Getter of element handle
     * 
     * @return element handle
     * @see SLElement
     */
    public long getElementHandle() {
        return elementHandle;
    }

    /**
     * Setter of element handle
     * 
     * @param elementHandle element handle
     * @see SLElement
     */
    public void setElementHandle( long elementHandle ) {
        this.elementHandle = elementHandle;
    }

    /**
     * Getter of Statement name
     * 
     * @return statement name
     */
    public String getStatement() {
        return statement;
    }

    /**
     * Setter of Statement name
     * 
     * @param statement
     */
    public void setStatement( String statement ) {
        this.statement = statement;
    }

    /**
     * Getter of ElementType
     * 
     * @return element type
     */
    public LineType getType() {
        return type;
    }

    /**
     * Setter of ElementType
     * 
     * @param type element type
     * @See LineType
     */
    public void setType( LineType type ) {
        this.type = type;
    }

    //@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + beginColumn;
        result = prime * result + beginLine;
        result = prime * result + (int)(elementHandle ^ (elementHandle >>> 32));
        result = prime * result + endColumn;
        result = prime * result + endLine;
        result = prime * result + ((statement == null) ? 0 : statement.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    //@Override
    public boolean equals( Object obj ) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final LineReference other = (LineReference)obj;
        if (beginColumn != other.beginColumn) return false;
        if (beginLine != other.beginLine) return false;
        if (elementHandle != other.elementHandle) return false;
        if (endColumn != other.endColumn) return false;
        if (endLine != other.endLine) return false;
        if (statement == null) {
            if (other.statement != null) return false;
        } else if (!statement.equals(other.statement)) return false;
        if (type == null) {
            if (other.type != null) return false;
        } else if (!type.equals(other.type)) return false;
        return true;
    }
}
