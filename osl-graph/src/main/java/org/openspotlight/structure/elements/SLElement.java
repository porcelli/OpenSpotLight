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
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;

import org.openspotlight.structure.SLStructure;

/**
 * Base class of SpotLight core data structure (SLStructure). This class holds two common information used by SLSimpleType and
 * SLLinkType: Label and Handle.
 * 
 * @author Vinicius Carvalho
 * 
 * @see SLStructure
 * @see SLSimpleType
 * @see SLLinkType
 */
public abstract class SLElement implements Serializable {
    private static final long serialVersionUID = 5653560362207009491L;
    protected Long handle = 0L;
    protected Map<String, Object> renderHints = new HashMap<String, Object>();

    /**
     * Defines the element label to be displayed at MetaModel explorer.
     * 
     * @return element label
     */
    @XmlAttribute( name = "Type" )
    public abstract String getLabel();

    /**
     * Getter of Handle.
     * 
     * @return handle value
     */
    @XmlAttribute( name = "Handle" )
    public Long getHandle() {
        return handle;
    }

    /**
     * Setter of Handle. Handle is used to identify uniquely the element.
     * 
     * @param handle handle value
     */
    public void setHandle( Long handle ) {
        this.handle = handle;
    }

    /**
     * Adds an rendering hint for the Diagram Service. A rendering hint is a parameter that is used later by one of the diagram
     * services in order to render this element properly. For instance one could add an key
     * "org.openspotlight.diagrams.GeneralDiagram.FONT_FACE" with value "Verdana". Rendering hints are optional and depends on the
     * diagram service. Please consult the API for the proper diagram service before adding any hint
     * 
     * @param key - The key for the hint as specified in one of the diagram services
     * @param hint - The actual value
     * @see org.openspotlight.diagrams
     */
    public void addRenderHint( String key,
                               Object hint ) {
        this.renderHints.put(key, hint);
    }

    /**
     * Getter for a specific rendering hint.
     * 
     * @param key - rendering hint
     * @return value
     */
    public Object getRenderHint( String key ) {
        return this.renderHints.get(key);
    }
}
