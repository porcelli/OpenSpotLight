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

import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.openspotlight.structure.elements.LineReference;
import org.openspotlight.structure.elements.SLLinkType;
import org.openspotlight.structure.elements.SLProperty;
import org.openspotlight.structure.elements.SLSimpleType;
import org.openspotlight.structure.elements.XLNType;
import org.openspotlight.structure.elements.XOB;
import org.openspotlight.structure.exceptions.SLStructureException;
import org.openspotlight.structure.predicates.ClassTypePredicate;
import org.openspotlight.structure.util.JAXBUtil;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * Default SLStructure implementation.
 * 
 * @author Vinicius Carvalho
 * 
 * @see SLStructure
 */
public class SLStructureImpl implements SLStructure {
    private SynchronizedSparseMultiGraph<SLSimpleType, SLLinkType> graph;
    private Long handle = new Long(0);
    private Collator collator = null;
    private XLNType xln;
    private Map<String, Set<SLSimpleType>> nodeContextexIndex;
    private Map<Long, SLSimpleType> nodeHandleIndex;

    public SLStructureImpl() {
        this.graph = new SynchronizedSparseMultiGraph<SLSimpleType, SLLinkType>();
        this.xln = new XLNType();
        this.nodeContextexIndex = new ConcurrentHashMap<String, Set<SLSimpleType>>();
        this.nodeHandleIndex = new ConcurrentHashMap<Long, SLSimpleType>();
        this.collator = Collator.getInstance();
    }

    public SLSimpleType addType( SLSimpleType node ) throws IllegalArgumentException {
        if (node == null) {
            throw new IllegalArgumentException("Type can not be null");
        }
        if (node.getKey() == null || node.getKey().trim().length() == 0) {
            throw new IllegalArgumentException("Key can not be null");
        }
        if (getTypeCount() > 0) {
            SLSimpleType source = getNodeFromContextexIndex(node);
            if (source == null) {
                synchronized (handle) {
                    handle++;
                    node.setHandle(handle);
                }
                addVertex(node);
            } else {
                node = replaceVertex(source, node);
                addNodeContextexIndex(node);
            }
        } else {
            synchronized (handle) {
                handle++;
                node.setHandle(handle);
            }
            addVertex(node);
        }
        return node;
    }

    public SLSimpleType getType( long handle ) {
        return this.nodeHandleIndex.get(handle);
    }

    public Collection<SLSimpleType> getTypes() {
        return this.graph.getVertices();
    }

    public Collection<SLSimpleType> getConnectedTypes( long handle ) {
        SLSimpleType type = this.nodeHandleIndex.get(handle);
        if (type == null) {
            return Collections.emptyList();
        }
        Collection<SLLinkType> edges = new HashSet<SLLinkType>();
        Collection<SLLinkType> outEdges = this.graph.getOutEdges(type);
        Collection<SLLinkType> inEdges = this.graph.getIncidentEdges(type);
        if (outEdges != null) {
            edges.addAll(outEdges);
        }
        if (inEdges != null) {
            edges.addAll(inEdges);
        }
        Set<SLSimpleType> nodes = new HashSet<SLSimpleType>();
        for (SLLinkType link : edges) {
            Pair<SLSimpleType> p = this.graph.getEndpoints(link);
            SLSimpleType adjNode = p.getFirst().equals(type) ? p.getSecond() : p.getFirst();
            nodes.add(adjNode);
        }
        return nodes;
    }

    @SuppressWarnings( {"unchecked", "cast"} )
    public Collection<SLSimpleType> getType( Predicate predicate ) throws IllegalArgumentException {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate can not be null");
        }
        return (Collection<SLSimpleType>)CollectionUtils.select(this.graph.getVertices(), predicate);
    }

    public Collection<SLSimpleType> getTypeByLink( Class<? extends SLLinkType> clazz ) throws IllegalArgumentException {
        if (clazz == null) {
            throw new IllegalArgumentException("Can not search for null class");
        }
        Collection<SLLinkType> links = CollectionUtils.select(this.graph.getEdges(), new ClassTypePredicate(clazz, true));
        Set<SLSimpleType> nodes = new HashSet<SLSimpleType>();
        for (SLLinkType link : links) {
            Pair<SLSimpleType> pair = this.graph.getEndpoints(link);
            nodes.add(pair.getFirst());
            nodes.add(pair.getSecond());
        }
        return nodes;
    }

    public Collection<SLSimpleType> getTypeByLink( Class<? extends SLLinkType> clazz,
                                                   long handle ) throws IllegalArgumentException {
        if (clazz == null) {
            throw new IllegalArgumentException("Can not search for null class");
        }
        SLSimpleType node = this.nodeHandleIndex.get(handle);
        if (node == null) {
            return Collections.emptyList();
        }
        Collection<SLLinkType> inEdges = this.graph.getIncidentEdges(node);
        Collection<SLLinkType> outEdges = this.graph.getOutEdges(node);
        Collection<SLLinkType> edges = new HashSet<SLLinkType>();
        Set<SLSimpleType> nodes = new HashSet<SLSimpleType>();
        if (inEdges != null) {
            edges.addAll(inEdges);
        }
        if (outEdges != null) {
            edges.addAll(outEdges);
        }
        CollectionUtils.filter(edges, new ClassTypePredicate(clazz, true));
        for (SLLinkType link : edges) {
            Pair<SLSimpleType> pair = this.graph.getEndpoints(link);
            if (!pair.getFirst().equals(node)) {
                nodes.add(pair.getFirst());
            }
            if (!pair.getSecond().equals(node)) {
                nodes.add(pair.getSecond());
            }
        }
        return nodes;
    }

    public Collection<SLSimpleType> getTypeByLink( Class<? extends SLLinkType> clazz,
                                                   long handle,
                                                   Class<? extends SLSimpleType> returnType,
                                                   boolean returnSubTypes ) throws IllegalArgumentException {
        if (returnType == null) {
            throw new IllegalArgumentException("Return type can not be null");
        }
        Collection<SLSimpleType> full = this.getTypeByLink(clazz, handle);
        CollectionUtils.filter(full, new ClassTypePredicate(returnType, returnSubTypes));
        return full;
    }

    public int getTypeCount() {
        return this.graph.getVertexCount();
    }

    public void removeType( long handle ) {
        SLSimpleType node = nodeHandleIndex.get(handle);
        if (node != null) {
            removeType(node);
        }
    }

    public void removeType( SLSimpleType node ) throws IllegalArgumentException {
        if (node == null) {
            throw new IllegalArgumentException("Type can not be null");
        }
        for (SLSimpleType child : node.getChildren()) {
            removeType(child);
        }
        boolean removed = this.graph.removeVertex(node);
        if (removed) {
            this.nodeContextexIndex.remove(convertToNodeKey(node));
            this.nodeHandleIndex.remove(node.getHandle());
        }
    }

    public void removeType( Class<? extends SLSimpleType> clazz,
                            boolean useSubType ) throws IllegalArgumentException {
        if (clazz == null) {
            throw new IllegalArgumentException("Can not search for null class");
        }
        ClassTypePredicate predicate = new ClassTypePredicate(clazz, useSubType);
        Collection<SLSimpleType> nodes = CollectionUtils.select(this.graph.getVertices(), predicate);
        for (SLSimpleType n : nodes) {
            removeType(n);
        }
    }

    public SLLinkType addLink( SLLinkType link,
                               SLSimpleType from,
                               SLSimpleType destination ) throws IllegalArgumentException {
        if (link == null) {
            throw new IllegalArgumentException("Link can not be null");
        }
        if (from == null || destination == null) {
            throw new IllegalArgumentException("Neither destination or from can be null");
        }
        Pair<SLSimpleType> endpoints = new Pair<SLSimpleType>(from, destination);
        endpoints = validatePair(endpoints);
        if (!pairExists(endpoints, link.getClass())) {
            //            boolean found = false;
            //            for (Entry<SLLinkType, Pair<SLSimpleType>> e : this.graph.getInternalEdges().entrySet()) {
            //                if (((e.getValue().getFirst().getHandle() == from.getHandle() && e.getValue().getSecond().getHandle() == destination.getHandle())
            //                    || (e.getValue().getFirst().getHandle() == destination.getHandle() && e.getValue().getSecond().getHandle() == from.getHandle()))
            //                    && link.getClass().getName().equals(e.getKey().getClass().getName())) {
            //                    found = true;
            //                    link = e.getKey();
            //                    break;
            //                }
            //            }
            //
            //            if (!found) {
            synchronized (handle) {
                handle++;
                link.setHandle(handle);
            }
            this.graph.addEdge(link, endpoints);
            //            }
        }
        return link;
    }

    public int getLinkCount() {
        return this.graph.getEdgeCount();
    }

    public void removeLink( Class<? extends SLLinkType> clazz ) throws IllegalArgumentException {
        if (clazz == null) {
            throw new IllegalArgumentException("Can not search for null class");
        }
        ClassTypePredicate predicate = new ClassTypePredicate(clazz, false);
        Collection<SLLinkType> links = CollectionUtils.select(this.graph.getEdges(), predicate);
        for (SLLinkType l : links) {
            this.graph.removeEdge(l);
        }
    }

    public void addLineReference( SLSimpleType node,
                                  String path,
                                  int beginLine,
                                  int endLine,
                                  int beginColumn,
                                  int endColumn,
                                  String statement ) throws IllegalArgumentException {
        if (beginLine <= 0 || endLine <= 0) {
            throw new IllegalArgumentException("Lines must be greater than 0");
        }
        if (beginColumn <= 0 || endColumn <= 0) {
            throw new IllegalArgumentException("Columns can not be less than 0");
        }
        if (beginLine > endLine) {
            throw new IllegalArgumentException("BeginLine must be greather than EndLine");
        }
        if (path == null || path.trim().length() == 0) {
            throw new IllegalArgumentException("Path can not be empty or null");
        }
        if (statement == null || statement.trim().length() == 0) {
            throw new IllegalArgumentException("Statement can not be empty or null");
        }
        if (node == null) {
            throw new IllegalArgumentException("Type can not be null");
        }
        if (getType(node.getHandle()) == null) {
            throw new IllegalArgumentException("Can not make a reference to a non existing type element in the strucutre");
        }
        LineReference ref = new LineReference(node, beginLine, endLine, beginColumn, endColumn, statement);
        xln.addLineReference(path, ref);
    }

    public Source getXLNRepresentation() throws SLStructureException {
        Source source = null;
        try {
            source = new DOMSource(JAXBUtil.marshal(this.xln));
        } catch (Exception e) {
            throw new SLStructureException("Could not create XML representation for this graph", e);
        }
        return source;
    }

    public Source getXOBRepresentation() throws SLStructureException {
        Source source = null;
        XOB xob = new XOB(); //new XOB(this.graph.getVertices(), this.graph.getEdges());
        try {
            source = new DOMSource(JAXBUtil.marshal(xob));
        } catch (Exception e) {
            throw new SLStructureException("Could not create XML representation for this graph", e);
        }
        return source;
    }

    private String convertToNodeKey( SLSimpleType node ) {
        if (node.getCollatorLevel() == Collator.IDENTICAL) {
            return new String((node.getKey() + ":" + node.getContextHandle()).getBytes());
        }
        collator.setStrength(node.getCollatorLevel());
        return new String(collator.getCollationKey(node.getKey() + ":" + node.getContextHandle()).toByteArray());
    }

    private void addVertex( SLSimpleType node ) {
        this.graph.addVertex(node);
        addNodeContextexIndex(node);
        nodeHandleIndex.put(node.getHandle(), node);
        setNodePath(node);
    }

    private void addNodeContextexIndex( SLSimpleType node ) {
        String nodeKey = convertToNodeKey(node);
        Set<SLSimpleType> typeSet = null;
        if (!nodeContextexIndex.containsKey(nodeKey)) {
            nodeContextexIndex.put(convertToNodeKey(node), new HashSet<SLSimpleType>());
        }
        typeSet = nodeContextexIndex.get(convertToNodeKey(node));
        typeSet.add(node);
    }

    private SLSimpleType getNodeFromContextexIndex( SLSimpleType node ) {
        String nodeKey = convertToNodeKey(node);
        if (nodeContextexIndex.containsKey(nodeKey)) {
            Set<SLSimpleType> typeSet = nodeContextexIndex.get(convertToNodeKey(node));
            for (SLSimpleType activeType : typeSet) {
                if (activeType.getClass().isAssignableFrom(node.getClass())
                    || node.getClass().isAssignableFrom(activeType.getClass())) {
                    return activeType;
                }
            }
            return null;
        }
        return null;
    }

    /**
     * Replaces source by target. This method also copies the properties of source and target as well as pointing the edges of old
     * node to the new one.
     * 
     * @param source
     * @param target
     * @return replaced SLSimpleType
     */
    protected SLSimpleType replaceVertex( SLSimpleType source,
                                          SLSimpleType target ) {
        SLSimpleType node = null;
        // if our node is a superclass, we just copy the properties
        if (source.getClass().isAssignableFrom(target.getClass())) {
            copyProperties(source, target, false);
            target.setHandle(source.getHandle());
            Collection<SLLinkType> edges = this.graph.getIncidentEdges(source);
            HashMap<SLLinkType, Pair<SLSimpleType>> edgeMap = new HashMap<SLLinkType, Pair<SLSimpleType>>();
            if (edges != null) {
                for (SLLinkType e : edges) {
                    Pair<SLSimpleType> p = this.graph.getEndpoints(e);
                    edgeMap.put(e, p);
                }
            }
            removeType(source);
            addVertex(target);
            for (SLLinkType e : edgeMap.keySet()) {
                Pair<SLSimpleType> p = edgeMap.get(e);
                if (p.getFirst().equals(source)) {
                    addLink(e, target, p.getSecond());
                } else {
                    //TODO HERE IS A REAL BUG!
                    addLink(e, p.getFirst(), target);
                }
            }
            node = target;
        } else {
            copyProperties(target, source, true);
            node = source;
        }
        return node;
    }

    /**
     * Copies propeties from target to source
     * 
     * @param source
     * @param target
     * @param overrides
     */
    @SuppressWarnings( "unchecked" )
    private void copyProperties( SLSimpleType source,
                                 SLSimpleType target,
                                 boolean overrides ) {
        for (SLProperty p : source.getProperties()) {
            if ((target.getProperty(p.getName()) == null)
                || (!target.getProperty(p.getName()).getValue().equals(p.getValue()) && overrides)) {
                target.addProperty(p);
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    private boolean pairExists( Pair<? extends SLSimpleType> endpoints,
                                Class clazz ) {
        boolean exists = false;
        Set<Pair<SLSimpleType>> pairs = new HashSet<Pair<SLSimpleType>>();
        Collection<SLLinkType> links = CollectionUtils.select(this.graph.getEdges(), new ClassTypePredicate(clazz, false));
        for (SLLinkType link : links) {
            pairs.add(this.graph.getEndpoints(link));
        }
        for (Pair p : pairs) {
            if (p.equals(endpoints)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    private void setNodePath( SLSimpleType node ) {
        SLSimpleType parent = this.nodeHandleIndex.get((long)node.getContextHandle());
        if (parent != null) {
            node.setParent(parent);
            parent.getChildren().add(node);
        }
    }

    private Pair<SLSimpleType> validatePair( Pair<? extends SLSimpleType> endpoints ) {
        SLSimpleType first;
        SLSimpleType second;
        first = getNodeFromContextexIndex(endpoints.getFirst()) != null ? getNodeFromContextexIndex(endpoints.getFirst()) : addType(endpoints.getFirst());
        second = getNodeFromContextexIndex(endpoints.getSecond()) != null ? getNodeFromContextexIndex(endpoints.getSecond()) : addType(endpoints.getSecond());
        return new Pair<SLSimpleType>(first, second);
    }

	@Override
	public void clear() throws SLStructureException {
		// TODO Auto-generated method stub
		
	}
}
