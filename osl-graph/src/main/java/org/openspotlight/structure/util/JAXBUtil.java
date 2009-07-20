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
package org.openspotlight.structure.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;

/**
 * Just an utility class to create an JAXB Context
 * 
 * @author Vinicius Carvalho
 */
public class JAXBUtil {
    private static JAXBContext context = null;
    private static final String packages = "org.openspotlight.graph.elements";
    private static DocumentBuilderFactory dbf;
    static {
        //SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            dbf = DocumentBuilderFactory.newInstance();
        } catch (Exception e) {
            //TODO replace for a generic SpotLight exception
            throw new RuntimeException(e);
        }
    }

    /**
     * Unmarshal XML data from the specified DOM tree and return the resulting content tree.
     * 
     * @param node root node
     * @return unmarshaled object
     * @throws Exception
     */
    public static Object unMarshal( Node node ) throws Exception {
        Unmarshaller unMarshaller = getJAXBContext().createUnmarshaller();
        return unMarshaller.unmarshal(node);
    }

    /**
     * Marshal the content tree rooted at obj into a DOM tree.
     * 
     * @param obj Object to be marshaled
     * @return root node
     * @throws JAXBException
     * @throws Exception
     */
    public static Node marshal( Object obj ) throws JAXBException, Exception {
        Marshaller marshaller = getJAXBContext().createMarshaller();
        Node node = dbf.newDocumentBuilder().newDocument();
        marshaller.marshal(obj, node);
        return node;
    }

    private static JAXBContext getJAXBContext() throws Exception {
        if (context == null) {
            synchronized (JAXBUtil.class) {
                if (context == null) {
                    context = JAXBContext.newInstance(packages);
                }
            }
        }
        return context;
    }
}
