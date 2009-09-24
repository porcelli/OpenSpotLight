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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openspotlight.common.util.Assertions;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;

// TODO: Auto-generated Javadoc
/**
 * The Class TypeFinder.
 */
public abstract class TypeFinder<N extends SLNode> {

    public static enum ResultOrder {
        ASC,
        DESC
    }

    /** The implementation inheritance links. */
    private final List<Class<? extends SLLink>> implementationInheritanceLinks;

    /** The interface inheritance links. */
    private final List<Class<? extends SLLink>> interfaceInheritanceLinks;

    /** The primitive hierarchy links. */
    private final List<Class<? extends SLLink>> primitiveHierarchyLinks;

    /** The abstract context. */
    private final SLContext                     abstractContext;

    /** The ordered active contexts. */
    private final List<SLContext>               orderedActiveContexts;

    /** The primitive types. */
    private final List<Class<? extends N>>      primitiveTypes;

    /** The enable boxing. */
    private final boolean                       enableBoxing;

    /** The session. */
    private final SLGraphSession                session;

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
                          final List<Class<? extends SLLink>> implementationInheritanceLinks,
                          final List<Class<? extends SLLink>> interfaceInheritanceLinks,
                          final List<Class<? extends SLLink>> primitiveHierarchyLinks, final SLContext abstractContext,
                          final List<SLContext> orderedActiveContexts, final List<Class<? extends N>> primitiveTypes,
                          final boolean enableBoxing, final SLGraphSession session ) {
        Assertions.checkNotNull("implementationInheritanceLinks", implementationInheritanceLinks);
        Assertions.checkNotNull("interfaceInheritanceLinks", interfaceInheritanceLinks);
        Assertions.checkNotNull("primitiveHierarchyLinks", primitiveHierarchyLinks);
        Assertions.checkNotNull("abstractContext", abstractContext);
        Assertions.checkNotNull("orderedActiveContexts", orderedActiveContexts);
        Assertions.checkNotNull("primitiveTypes", primitiveTypes);
        Assertions.checkNotNull("session", session);
        Assertions.checkCondition("implementationInheritanceLinksNotEmpty", implementationInheritanceLinks.size() > 0);
        Assertions.checkCondition("interfaceInheritanceLinksNotEmpty", interfaceInheritanceLinks.size() > 0);
        Assertions.checkCondition("primitiveHierarchyLinksNotEmpty", primitiveHierarchyLinks.size() > 0);
        Assertions.checkCondition("orderedActiveContextsNotEmpty", orderedActiveContexts.size() > 0);
        Assertions.checkCondition("primitiveTypesNotEmpty", primitiveTypes.size() > 0);
        this.implementationInheritanceLinks = implementationInheritanceLinks;
        this.interfaceInheritanceLinks = interfaceInheritanceLinks;
        this.primitiveHierarchyLinks = primitiveHierarchyLinks;
        this.abstractContext = abstractContext;
        final ArrayList<SLContext> all = new ArrayList<SLContext>(orderedActiveContexts);
        if (!all.contains(abstractContext)) {
            all.add(abstractContext);
        }
        this.orderedActiveContexts = Collections.unmodifiableList(all);
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

    public <T extends N, A extends N> List<T> getAllChildren( final A activeType,
                                                              final ResultOrder order ) throws NodeNotFoundException {
        throw new UnsupportedOperationException();
    }

    public <T extends N, A extends N> List<T> getAllParents( final A activeType,
                                                             final ResultOrder order ) throws NodeNotFoundException {
        throw new UnsupportedOperationException();
    }

    public <T extends N, A extends N> List<T> getConcreteChildren( final A activeType,
                                                                   final ResultOrder order ) throws NodeNotFoundException {
        throw new UnsupportedOperationException();
    }

    public <T extends N, A extends N> List<T> getConcreteParents( final A activeType,
                                                                  final ResultOrder order ) throws NodeNotFoundException {
        throw new UnsupportedOperationException();
    }

    public <T extends N, A extends N> List<T> getDirectConcreteChildren( final A activeType ) throws NodeNotFoundException {
        throw new UnsupportedOperationException();
    }

    public <T extends N, A extends N> List<T> getDirectConcreteParents( final A activeType ) throws NodeNotFoundException {
        throw new UnsupportedOperationException();
    }

    public <T extends N, A extends N> List<T> getDirectInterfaceChildren( final A activeType ) throws NodeNotFoundException {
        throw new UnsupportedOperationException();
    }

    public <T extends N, A extends N> List<T> getDirectInterfaceParents( final A activeType ) throws NodeNotFoundException {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the implementation inheritance links.
     * 
     * @return the implementation inheritance links
     */
    protected List<Class<? extends SLLink>> getImplementationInheritanceLinks() throws LinkNotFoundException {
        return this.implementationInheritanceLinks;
    }

    public <T extends N, A extends N> List<T> getInterfaceChildren( final A activeType,
                                                                    final ResultOrder order ) throws NodeNotFoundException {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the interface inheritance links.
     * 
     * @return the interface inheritance links
     */
    protected List<Class<? extends SLLink>> getInterfaceInheritanceLinks() throws LinkNotFoundException {
        return this.interfaceInheritanceLinks;
    }

    public <T extends N, A extends N> List<T> getInterfaceParents( final A activeType,
                                                                   final ResultOrder order ) throws NodeNotFoundException {
        throw new UnsupportedOperationException();
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
    protected List<Class<? extends SLLink>> getPrimitiveHierarchyLinks() {
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
    public abstract <T extends N> T getType( String typeToSolve ) throws NodeNotFoundException;

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
     * Checks if is type of.
     * 
     * @param type the type
     * @param anotherType the another type
     * @return true, if is type of
     */
    public <T extends N> boolean isConcreteType( final T type ) {
        throw new UnsupportedOperationException("Not implemented yet");
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
    public <T extends N, A extends N> boolean isTypeOf( final T type,
                                                        final A anotherType ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
