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
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This is just an wrapper for the XLN XML structure, it only holds elements so XML binding can be applied to it.
 * 
 * @author Vinicius Carvalho
 */
@XmlType( name = "XLN", namespace = "http://www.devexp.com.br/XLNV1" )
@XmlRootElement( name = "XLN", namespace = "http://www.devexp.com.br/XLNV1" )
public class XLNType implements Serializable {
    private static final long serialVersionUID = -1293531480659010195L;
    private Map<String, FileReference> referenceMap;
    private Date createTime;
    private String version;

    public XLNType() {
        this.referenceMap = new ConcurrentHashMap<String, FileReference>();
        this.createTime = new Date();
        this.version = "1.0";
    }

    @XmlElement( name = "XFileRef", namespace = "http://www.devexp.com.br/XLNV1" )
    public Collection<FileReference> getFileReferences() {
        return referenceMap.values();
    }

    public FileReference getFileReference( String path ) {
        return referenceMap.get(path);
    }

    /**
     * Getter CreateTime
     * 
     * @return Date
     */
    @XmlAttribute( name = "CreationTime" )
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Setter for CreateTime
     * 
     * @param createTime
     */
    public void setCreateTime( Date createTime ) {
        this.createTime = createTime;
    }

    /**
     * Getter of xln version
     * 
     * @return version
     */
    @XmlAttribute( name = "Version" )
    public String getVersion() {
        return version;
    }

    /**
     * Setter of version
     * 
     * @param version xln version
     */
    public void setVersion( String version ) {
        this.version = version;
    }

    public void addLineReference( String path,
                                  LineReference lineRef ) {
        FileReference fileRef = referenceMap.get(path);
        if (fileRef == null) {
            fileRef = new FileReference(path);
        }
        fileRef.addLine(lineRef);
        referenceMap.put(path, fileRef);
    }
}
