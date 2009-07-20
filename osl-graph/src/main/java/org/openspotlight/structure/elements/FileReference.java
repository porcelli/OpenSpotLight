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
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Represents a FileReference on SpotLight. A FileReference is just a pointer to the path of the real artifact.
 * 
 * @author Vinicius Carvalho
 */
public class FileReference implements Serializable {
    private static final long serialVersionUID = -4487060153183140046L;
    private String path;
    private Set<LineReference> lines = new HashSet<LineReference>();

    public FileReference() {
        this.lines = new HashSet<LineReference>();
    }

    public FileReference(
                          String path ) {
        this.path = toUnixFormat(path);
        this.lines = new HashSet<LineReference>();
    }

    /**
     * Getter for LineReferences
     * 
     * @return set of line references
     * @see LineReference
     */
    @XmlElement( name = "XLineRef", namespace = "http://www.devexp.com.br/XLNV1" )
    public Set<LineReference> getLines() {
        return lines;
    }

    /**
     * Setter for LineReferences
     * 
     * @param lines set of line references
     * @see LineReference
     */
    public void setLines( Set<LineReference> lines ) {
        this.lines = lines;
    }

    /**
     * Add one line reference
     * 
     * @param line line reference
     * @see LineReference
     */
    public void addLine( LineReference line ) {
        this.lines.add(line);
    }

    /**
     * Getter of artifact path
     * 
     * @return artifact path
     */
    @XmlAttribute( name = "Path" )
    public String getPath() {
        return path;
    }

    /**
     * Setter of artifact path
     * 
     * @param path artifact path
     */
    public void setPath( String path ) {
        this.path = toUnixFormat(path);
    }

    /**
     * Getter for artifact name
     * 
     * @return artifact name
     */
    @XmlAttribute( name = "Name" )
    public String getName() {
        return path.substring(path.lastIndexOf("/") + 1, path.length());
    }

    private String toUnixFormat( String path ) {
        return path.replaceAll("\\\\", "/");
    }

    //@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    //@Override
    public boolean equals( Object obj ) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final FileReference other = (FileReference)obj;
        if (path == null) {
            if (other.path != null) return false;
        } else if (!path.equals(other.path)) return false;
        return true;
    }
}
