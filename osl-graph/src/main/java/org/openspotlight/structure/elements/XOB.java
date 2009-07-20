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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import edu.uci.ics.jung.graph.util.Pair;



/**
 * This class is just an wrapper to the SLStructure, so we can export it as a XOB file.
 * 
 * @author Vinicius Carvalho
 */
@XmlRootElement( name = "XOB", namespace = "http://www.devexp.com.br/XOBV1" )
@XmlType( propOrder = {"objects", "links"}, namespace = "http://www.devexp.com.br/XOBV1", name = "XOB" )
public class XOB {
    private Date createTime;
    private String version;
    private Set<SLSimpleType> objects;
    private List<XOBLink> links;

    public XOB() {
    }

    public XOB(
                Map<SLSimpleType, Pair<Set<SLLinkType>>> vertices, Map<SLLinkType, Pair<SLSimpleType>> edges ) {
        this.createTime = new Date();
        this.version = "1.0";
        this.objects = vertices.keySet();
        prepareLinks(edges);
    }

    /**
     * Getter CreateTime
     * 
     * @return Date
     */
    @XmlAttribute( name = "CreationDate" )
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Getter of XOB version
     * 
     * @return version
     */
    @XmlAttribute( name = "FormatVersion" )
    public String getVersion() {
        return version;
    }

    /**
     * Getter of all Objects
     * 
     * @return a set of objects
     */
    @XmlElementWrapper( name = "XObjectList", namespace = "http://www.devexp.com.br/XOBV1" )
    @XmlElement( name = "XParserObject", namespace = "http://www.devexp.com.br/XOBV1" )
    public Set<SLSimpleType> getObjects() {
        return objects;
    }

    /**
     * Getter of all Links
     * 
     * @return list of xob links
     */
    @XmlElementWrapper( name = "XLinkList", namespace = "http://www.devexp.com.br/XOBV1" )
    @XmlElement( name = "XLink", namespace = "http://www.devexp.com.br/XOBV1" )
    public List<XOBLink> getLinks() {
        return links;
    }

    /**
     * Setter of xob links
     * 
     * @param links list of links
     */
    public void setLinks( List<XOBLink> links ) {
        this.links = links;
    }

    /**
     * Setter of version
     * 
     * @param version xob version
     */
    public void setVersion( String version ) {
        this.version = version;
    }

    /**
     * Setter of objects
     * 
     * @param objects set of objects
     */
    public void setObjects( Set<SLSimpleType> objects ) {
        this.objects = objects;
    }

    /**
     * Setter for CreateTime
     * 
     * @param createTime
     */
    public void setCreateTime( Date createTime ) {
        this.createTime = createTime;
    }

    private void prepareLinks( Map<SLLinkType, Pair<SLSimpleType>> edges ) {
        this.links = new ArrayList<XOBLink>();
        for (Entry<SLLinkType, Pair<SLSimpleType>> e : edges.entrySet()) {
            XOBLink link = new XOBLink(e.getKey().getHandle(), e.getValue().getFirst().getHandle(),
                                       e.getValue().getSecond().getHandle(), "A2B", e.getKey().getLabel());
            this.links.add(link);
        }
    }

    /**
     * The XOB file requires an element named XLink, this element does not have a corresponding domain model on the SpotLight
     * Graph. Instead of adding a new class to our domain, this inner class acts as just one wrapper for the SLLinkType and the
     * Pair that represents the edges on our graph
     * 
     * @author viniciuscarvalho
     */
    @SuppressWarnings( "unused" )
    @XmlAccessorType( XmlAccessType.FIELD )
    public static class XOBLink {
        @XmlAttribute( name = "Handle" )
        private Long handle;
        @XmlAttribute( name = "ObjectAHandle" )
        private Long objectAHandle;
        @XmlAttribute( name = "ObjectBHandle" )
        private Long objectBHandle;
        @XmlAttribute( name = "Direction" )
        private String direction;
        @XmlAttribute( name = "Type" )
        private String type;

        public XOBLink() {
        }

        public XOBLink(
                        Long handle, Long objectAHandle, Long objectBHandle, String direction, String type ) {
            this.handle = handle;
            this.objectAHandle = objectAHandle;
            this.objectBHandle = objectBHandle;
            this.direction = direction;
            this.type = type;
        }
    }
}
