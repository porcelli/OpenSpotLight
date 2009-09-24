/*
 * OpenSpotLight - Open Source IT Governance Platform
 *  
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA 
 * or third-party contributors as indicated by the @author tags or express 
 * copyright attribution statements applied by the authors.  All third-party 
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E 
 * TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * This copyrighted material is made available to anyone wishing to use, modify, 
 * copy, or redistribute it subject to the terms and conditions of the GNU 
 * Lesser General Public License, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License  for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this distribution; if not, write to: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA 
 * 
 *********************************************************************** 
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os 
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.  
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
/**
 * 
 */
package org.openspotlight.bundle.dap.language.java.support;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;

import java.util.List;

import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;

// TODO: Auto-generated Javadoc
/**
 * The Class TypeFinder.
 */
public abstract class TypeFinder<N extends SLNode, K extends SLLink> {

    /** The implementation inheritance links. */
    private final List<Class<? extends K>> implementationInheritanceLinks;

    /** The interface inheritance links. */
    private final List<Class<? extends K>> interfaceInheritanceLinks;

    /** The primitive hierarchy links. */
    private final List<Class<? extends K>> primitiveHierarchyLinks;

    /** The abstract context. */
    private final SLContext                abstractContext;

    /** The ordered active contexts. */
    private final List<SLContext>          orderedActiveContexts;

    /** The primitive types. */
    private final List<Class<? extends N>> primitiveTypes;

    /** The enable boxing. */
    private final boolean                  enableBoxing;

    /** The session. */
    private final SLGraphSession           session;

    /**
     * Instantiates a new type finder.
     * 
     * @param implementationInheritanceLinks the implementation inheritance links
     * @param interfaceInheritanceLinks the interface inheritance links
     * @param primitiveHierarchyLinks the primitive hierarchy links
     * @param abstractContext the abstract context
     * @param orderedActiveContexts the ordered active contexts
     * @param primitiveTypes the primitive types
     * @param enableBoxing the enable boxing
     * @param session the session
     */
    protected TypeFinder(
                          final List<Class<? extends K>> implementationInheritanceLinks,
                          final List<Class<? extends K>> interfaceInheritanceLinks,
                          final List<Class<? extends K>> primitiveHierarchyLinks, final SLContext abstractContext,
                          final List<SLContext> orderedActiveContexts, final List<Class<? extends N>> primitiveTypes,
                          final boolean enableBoxing, final SLGraphSession session ) {
        checkNotNull("implementationInheritanceLinks", implementationInheritanceLinks);
        checkNotNull("interfaceInheritanceLinks", interfaceInheritanceLinks);
        checkNotNull("primitiveHierarchyLinks", primitiveHierarchyLinks);
        checkNotNull("abstractContext", abstractContext);
        checkNotNull("orderedActiveContexts", orderedActiveContexts);
        checkNotNull("primitiveTypes", primitiveTypes);
        checkNotNull("session", session);
        checkCondition("implementationInheritanceLinksNotEmpty", implementationInheritanceLinks.size() > 0);
        checkCondition("interfaceInheritanceLinksNotEmpty", interfaceInheritanceLinks.size() > 0);
        checkCondition("primitiveHierarchyLinksNotEmpty", primitiveHierarchyLinks.size() > 0);
        checkCondition("orderedActiveContextsNotEmpty", orderedActiveContexts.size() > 0);
        checkCondition("primitiveTypesNotEmpty", primitiveTypes.size() > 0);
        this.implementationInheritanceLinks = implementationInheritanceLinks;
        this.interfaceInheritanceLinks = interfaceInheritanceLinks;
        this.primitiveHierarchyLinks = primitiveHierarchyLinks;
        this.abstractContext = abstractContext;
        this.orderedActiveContexts = orderedActiveContexts;
        this.primitiveTypes = primitiveTypes;
        this.enableBoxing = enableBoxing;
        this.session = session;
    }

    /**
     * Gets the abstract context.
     * 
     * @return the abstract context
     */
    protected SLContext getAbstractContext() {
        return this.abstractContext;
    }

    /**
     * Gets the concrete types lower higher first.
     * 
     * @param type the type
     * @return the concrete types lower higher first
     */
    public <T extends N> List<N> getConcreteTypesLowerHigherFirst( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the concrete types lower higher last.
     * 
     * @param type the type
     * @return the concrete types lower higher last
     */
    public <T extends N> List<N> getConcreteTypesLowerHigherLast( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the concrete types upper higher first.
     * 
     * @param type the type
     * @return the concrete types upper higher first
     */
    public <T extends N> List<N> getConcreteTypesUpperHigherFirst( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the concrete types upper higher last.
     * 
     * @param type the type
     * @return the concrete types upper higher last
     */
    public <T extends N> List<N> getConcreteTypesUpperHigherLast( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the direct child.
     * 
     * @param type the type
     * @return the direct child
     */
    public <T extends N> List<N> getDirectChild( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the direct concrete parents.
     * 
     * @param type the type
     * @return the direct concrete parents
     */
    public <T extends N> List<N> getDirectConcreteParents( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the direct conrete child.
     * 
     * @param type the type
     * @return the direct conrete child
     */
    public <T extends N> List<N> getDirectConreteChild( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the direct interface child.
     * 
     * @param type the type
     * @return the direct interface child
     */
    public <T extends N> List<N> getDirectInterfaceChild( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the direct interface parents.
     * 
     * @param type the type
     * @return the direct interface parents
     */
    public <T extends N> List<N> getDirectInterfaceParents( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the direct parents.
     * 
     * @param type the type
     * @return the direct parents
     */
    public <T extends N> List<N> getDirectParents( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the implementation inheritance links.
     * 
     * @return the implementation inheritance links
     */
    protected List<Class<? extends K>> getImplementationInheritanceLinks() throws LinkNotFoundException {
        return this.implementationInheritanceLinks;
    }

    /**
     * Gets the interface inheritance links.
     * 
     * @return the interface inheritance links
     */
    protected List<Class<? extends K>> getInterfaceInheritanceLinks() throws LinkNotFoundException {
        return this.interfaceInheritanceLinks;
    }

    /**
     * Gets the interface types lower higher first.
     * 
     * @param type the type
     * @return the interface types lower higher first
     */
    public <T extends N> List<N> getInterfaceTypesLowerHigherFirst( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the interface types lower higher last.
     * 
     * @param type the type
     * @return the interface types lower higher last
     */
    public <T extends N> List<N> getInterfaceTypesLowerHigherLast( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the interface types upper higher first.
     * 
     * @param type the type
     * @return the interface types upper higher first
     */
    public <T extends N> List<N> getInterfaceTypesUpperHigherFirst( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the interface types upper higher last.
     * 
     * @param type the type
     * @return the interface types upper higher last
     */
    public <T extends N> List<N> getInterfaceTypesUpperHigherLast( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the ordered active contexts.
     * 
     * @return the ordered active contexts
     */
    protected List<SLContext> getOrderedActiveContexts() {
        return this.orderedActiveContexts;
    }

    /**
     * Gets the primitive hierarchy links.
     * 
     * @return the primitive hierarchy links
     */
    protected List<Class<? extends K>> getPrimitiveHierarchyLinks() {
        return this.primitiveHierarchyLinks;
    }

    /**
     * Gets the primitive types.
     * 
     * @return the primitive types
     */
    protected List<Class<? extends N>> getPrimitiveTypes() {
        return this.primitiveTypes;
    }

    /**
     * Gets the session.
     * 
     * @return the session
     */
    protected SLGraphSession getSession() {
        return this.session;
    }

    /**
     * Gets the type.
     * 
     * @param typeToSolve the type to solve
     * @return the type
     */
    public abstract <T extends N> N getType( String typeToSolve ) throws NodeNotFoundException;

    /**
     * Gets the type.
     * 
     * @param typeToSolve the type to solve
     * @param activeType the active type
     * @param parametrizedTypes the parametrized types
     * @return the type
     */
    public abstract <T extends N, A extends N> N getType( String typeToSolve,
                                                          A activeType,
                                                          List<? extends N> parametrizedTypes ) throws NodeNotFoundException;

    /**
     * Gets the types lower higher first.
     * 
     * @param type the type
     * @return the types lower higher first
     */
    public <T extends N> List<N> getTypesLowerHigherFirst( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the types lower higher last.
     * 
     * @param type the type
     * @return the types lower higher last
     */
    public <T extends N> List<N> getTypesLowerHigherLast( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the types upper higher first.
     * 
     * @param type the type
     * @return the types upper higher first
     */
    public <T extends N> List<N> getTypesUpperHigherFirst( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Gets the types upper higher last.
     * 
     * @param type the type
     * @return the types upper higher last
     */
    public <T extends N> List<N> getTypesUpperHigherLast( final T type ) throws NodeNotFoundException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Checks if is enable boxing.
     * 
     * @return true, if is enable boxing
     */
    protected boolean isEnableBoxing() {
        return this.enableBoxing;
    }

    /**
     * Checks if is type of.
     * 
     * @param type the type
     * @param anotherType the another type
     * @return true, if is type of
     */
    public <T extends N, A extends N> boolean isTypeOf( final N type,
                                                        final A anotherType ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
