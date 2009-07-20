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
package org.openspotlight.structure;

import java.util.Collection;
import javax.xml.transform.Source;
import org.apache.commons.collections15.Predicate;
import org.openspotlight.structure.elements.SLLinkType;
import org.openspotlight.structure.elements.SLSimpleType;
import org.openspotlight.structure.exceptions.SLStructureException;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * SpotLight core data structure. This structure should be used by parser that whant store data at SpotLight Repository.
 * 
 * This structure is a mix of undirected graph and tree. The nodes are instances of SLSimpleType and edges are a combination of a
 * Pair of SLSimpleType typed by SLLinkType.
 * 
 * @author Vinicius Carvalho
 * 
 * @see SLSimpleType
 * @see SLLinkType
 * @see Pair
 */
public interface SLStructure {

    /**
     * Adds a new type to the structure. - If the type is a new instance than it's added and a new handle is set for it. - If the
     * type is a subclass of an existent type of same key and contextHandle, then the existent type is replaced by it's subclass
     * version, the properties of the subclass are kept and only new properties from the existent version are copied to the
     * subclass - If the type is a super class of an existent type of same key and contextHandle, then the existent type is kept,
     * new properties are copied to the existent type, and the return of the method is the existent type, the new type is
     * discarded from memory.
     * 
     * @param type - The type to be added
     * @return
     * @throws IllegalArgumentException if type is null or inconsistent (key and contexthandle are null)
     */
    public SLSimpleType addType( SLSimpleType type ) throws IllegalArgumentException;

    /**
     * @param handle
     * @return The type, or null if not found
     */
    public SLSimpleType getType( long handle );

    /**
     * @return a list of all types inside the structure
     */
    public Collection<SLSimpleType> getTypes();

    /**
     * @param handle - The unique handle that identifies the Type
     * @return a list of all types that are connected to these type. If no types are found an empty list is returned
     */
    public Collection<SLSimpleType> getConnectedTypes( long handle );

    /**
     * Returns all types found for a given predicate
     * 
     * @param predicate
     * @return A list of types
     * @throws IllegalArgumentException if the predicate is null
     */
    @SuppressWarnings( "unchecked" )
    public Collection<SLSimpleType> getType( Predicate predicate ) throws IllegalArgumentException;

    /**
     * Fetches a list of types that are connected by the link of the clazz hierarchy. Both sides of the association are returned
     * 
     * @param clazz
     * @return a list of Types
     * @throws IllegalArgumentException if clazz is null
     */
    public Collection<SLSimpleType> getTypeByLink( Class<? extends SLLinkType> clazz ) throws IllegalArgumentException;

    /**
     * Fetches all Types that are connected to the type identified by the handle parameter. Only types connected by hierarchy
     * defined by clazz are returned
     * 
     * @param clazz - The hierarchy of links to be filtered
     * @param handle - The identifier of one of the sides of the associations
     * @return A list of types, if no type is found for a given handle an empty list is returned
     * @throws IllegalArgumentException if clazz is null
     */
    public Collection<SLSimpleType> getTypeByLink( Class<? extends SLLinkType> clazz,
                                                   long handle ) throws IllegalArgumentException;

    /**
     * Fetches all Types that are connected to the type indentified by the handle parameter. Only types of onnected by hierarchy
     * defined by clazz are returned, also, only descendents of returnType are returned.
     * 
     * @param clazz - The hierarchy of links that connects the types
     * @param handle - The identifier of one of the sides of the associations
     * @param returnType - The hierarchy of the other side of the associations
     * @param returnSubTypes - true if all descendants should be returned, false if only exact class must be match
     * @return a list of types
     * @throws IllegalArgumentException if clazz or returnType is null
     */
    public Collection<SLSimpleType> getTypeByLink( Class<? extends SLLinkType> clazz,
                                                   long handle,
                                                   Class<? extends SLSimpleType> returnType,
                                                   boolean returnSubTypes ) throws IllegalArgumentException;

    /**
     * Returns the number of types inside the structure
     * 
     * @return
     */
    public int getTypeCount();

    /**
     * Remove the type identified by the handle. If no type is found nothing is done. All links associated with this type are
     * removed as well
     * 
     * @param handle
     */
    public void removeType( long handle );

    /**
     * Remove this type from the structure. All links associated with this type are removed as well
     * 
     * @param type - The type to be removed, can not be null
     * @throws IllegalArgumentException if type is null
     */
    public void removeType( SLSimpleType type ) throws IllegalArgumentException;

    /**
     * Removes all types that are identified by the clazz hierarchy. If useSubtype is set then all descendants are removed as well
     * All links associated with the types removed are removed as well
     * 
     * @param clazz
     * @param useSubtype
     * @throws IllegalArgumentException if clazz is null
     */
    public void removeType( Class<? extends SLSimpleType> clazz,
                            boolean useSubtype ) throws IllegalArgumentException;

    /**
     * Creates a link between two types. The from and destination must have a valid handle. If not, they will be inserted into the
     * structure prior to the link creation.
     * 
     * @param link - The link to be used
     * @param from
     * @param destination
     * @return - The new link created with the handle set
     * @throws IllegalArgumentException if link, from or destination are null
     */
    public SLLinkType addLink( SLLinkType link,
                               SLSimpleType from,
                               SLSimpleType destination ) throws IllegalArgumentException;

    /**
     * Returns the number of links inside the structure
     * 
     * @return
     */
    public int getLinkCount();

    /**
     * Remove all links identified by class hierarchy. Removing the links do not remove the Types they bound
     * 
     * @param clazz
     * @throws IllegalArgumentException if clazz is null
     */
    public void removeLink( Class<? extends SLLinkType> clazz ) throws IllegalArgumentException;

    /**
     * Creates a line reference to be used later on the XLN representation
     * 
     * @param type - The type bound to the reference
     * @param path - The physical path of the file
     * @param beginLine - The begin line number, must be greater than 0
     * @param endLine - The end line number, must be greater than 0 and greater than or equal beginLine
     * @param beginColumn - The begin column
     * @param endColumn - The end column
     * @param statement - The statement , can not be empty or null
     * @throws IllegalArgumentException if any of the assertions above fail
     */
    public void addLineReference( SLSimpleType type,
                                  String path,
                                  int beginLine,
                                  int endLine,
                                  int beginColumn,
                                  int endColumn,
                                  String statement ) throws IllegalArgumentException;

    /**
     * Returns the XLN XML representation of line references of this graph
     * 
     * @return
     * @throws SLStructureException - if any parser error happens
     */
    public Source getXLNRepresentation() throws SLStructureException;

    /**
     * Returns the XOB XML representation of line references of this graph
     * 
     * @return
     * @throws SLStructureException - if any parser error happens
     */
    public Source getXOBRepresentation() throws SLStructureException;
    
    
    /**
     * Clears all the graph content.
     * 
     * @throws SLStructureException - if the clear operation fails.
     */
    public void clear() throws SLStructureException;
    
}
