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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openspotlight.bundle.dap.language.java.metamodel.link.Extends;
import org.openspotlight.bundle.dap.language.java.metamodel.link.Implements;
import org.openspotlight.bundle.dap.language.java.metamodel.link.PackageType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Strings;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLQuery;
import org.openspotlight.graph.query.SLQueryResult;

/**
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class JavaTypeFinder extends TypeFinder<JavaType> {

    private class ComplexTypeFinderQueryExecutor extends TypeFinderByQueryExecutor {

        private final Collection<SLNode> allTypesFromSamePackages;

        public ComplexTypeFinderQueryExecutor(
                                               final String typeToSolve, final Collection<SLNode> allTypesFromSamePackages ) {
            super(typeToSolve);
            this.allTypesFromSamePackages = allTypesFromSamePackages;
        }

        /**
         * @{inheritDoc
         */
        @Override
        public SLQueryResult executeWithThisString( final String s ) throws Exception {
            final SLQuery justTheTargetTypeQuery = JavaTypeFinder.this.getSession().createQuery();
            justTheTargetTypeQuery.selectByNodeType().type(JavaType.class.getName()).subTypes().selectEnd().where().type(
                                                                                                                         JavaType.class.getName()).subTypes().each().property(
                                                                                                                                                                              "simpleName").equalsTo().value(
                                                                                                                                                                                                             s);
            final SLQueryResult result = justTheTargetTypeQuery.execute(this.allTypesFromSamePackages);
            return result;
        }

    }

    private class SimpleGetTypeFinder extends TypeFinderByQueryExecutor {

        /**
         * @param s
         */
        public SimpleGetTypeFinder(
                                    final String s ) {
            super(s);
        }

        /**
         * @{inheritDoc
         */
        @Override
        protected SLQueryResult executeWithThisString( final String s ) throws Exception {
            final SLQuery query = JavaTypeFinder.this.getSession().createQuery();
            query.selectByNodeType().type(JavaType.class.getName()).subTypes().selectEnd().where().type(JavaType.class.getName()).subTypes().each().property(
                                                                                                                                                             "completeName").equalsTo().value(
                                                                                                                                                                                              s).typeEnd();
            final SLQueryResult result = query.execute();
            return result;

        }

    }

    private abstract class TypeFinderByQueryExecutor {
        private final String firstString;

        private String       actualString;

        public TypeFinderByQueryExecutor(
                                          final String s ) {
            this.firstString = s;
            this.actualString = s;
        }

        protected abstract SLQueryResult executeWithThisString( String s ) throws Exception;

        protected String getNewString() {
            this.actualString = Strings.replaceLast(this.actualString, ".", "$");
            return this.actualString;
        }

        private SLNode getPrefferedType( final SLQueryResult result ) throws Exception {
            final Map<String, List<SLNode>> resultMap = new HashMap<String, List<SLNode>>();
            for (final SLContext ctx : JavaTypeFinder.this.getOrderedActiveContexts()) {
                resultMap.put(ctx.getID(), new ArrayList<SLNode>());
            }
            for (final SLNode n : result.getNodes()) {
                final List<SLNode> resultList = resultMap.get(n.getContext().getID());
                if (resultList != null) {
                    resultList.add(n);
                    if (resultList.size() > 1) {
                        Exceptions.logAndThrow(new IllegalStateException(
                                                                         MessageFormat.format(
                                                                                              "Two nodes of the same type and name on the same context: node {0} (parent {2}) inside context {1}",
                                                                                              n.getName(),
                                                                                              n.getContext().getID(),
                                                                                              n.getParent().getName())));
                    }
                }
            }
            for (final SLContext ctx : JavaTypeFinder.this.getOrderedActiveContexts()) {
                final List<SLNode> resultList = resultMap.get(ctx.getID());
                if (resultList.size() > 0) {
                    return resultList.get(0);
                }
            }
            return null;
        }

        public SLNode getTypeByAllPossibleNames() throws Exception {

            SLNode result = this.getPrefferedType(this.executeWithThisString(this.firstString));
            if (result != null) {
                return result;
            }
            String newName = this.firstString;
            while (newName.indexOf(".") != -1) {
                newName = this.getNewString();
                result = this.getPrefferedType(this.executeWithThisString(newName));
                if (result != null) {
                    return result;
                }
            }
            return null;

        }

    }

    private static final List<Class<? extends SLLink>>   implementationInheritanceLinks = new ArrayList<Class<? extends SLLink>>();
    private static final List<Class<? extends SLLink>>   interfaceInheritanceLinks      = new ArrayList<Class<? extends SLLink>>();

    private static final List<Class<? extends SLLink>>   primitiveHierarchyLinks        = new ArrayList<Class<? extends SLLink>>();

    private static final List<Class<? extends JavaType>> primitiveTypes                 = new ArrayList<Class<? extends JavaType>>();

    static {
        JavaTypeFinder.implementationInheritanceLinks.add(Extends.class);
        JavaTypeFinder.interfaceInheritanceLinks.add(Implements.class);
        JavaTypeFinder.primitiveTypes.add(JavaTypePrimitive.class);
        JavaTypeFinder.primitiveHierarchyLinks.add(Extends.class);//FIXME

    }

    public JavaTypeFinder(
                           final SLContext abstractContext, final List<SLContext> orderedActiveContexts,
                           final boolean enableBoxing, final SLGraphSession session ) {
        super(JavaTypeFinder.implementationInheritanceLinks, JavaTypeFinder.interfaceInheritanceLinks,
              JavaTypeFinder.primitiveHierarchyLinks, abstractContext, orderedActiveContexts, JavaTypeFinder.primitiveTypes,
              enableBoxing, session);
    }

    @Override
    public <T extends JavaType, A extends JavaType> List<T> getAllChildren( final A activeType,
                                                                            final org.openspotlight.bundle.dap.language.java.support.TypeFinder.ResultOrder order )
        throws NodeNotFoundException {
        // TODO Auto-generated method stub
        return super.getAllChildren(activeType, order);
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <T extends JavaType> T getType( final String typeToSolve ) throws NodeNotFoundException {
        try {
            final SLNode slNode = new SimpleGetTypeFinder(typeToSolve).getTypeByAllPossibleNames();
            if (slNode == null) {
                throw Exceptions.logAndReturn(new NodeNotFoundException());
            }
            return (T)this.getTypedNode(slNode);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, NodeNotFoundException.class);
        }
    }

    /**
     * @{inheritDoc
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public <T extends JavaType, A extends JavaType> T getType( final String typeToSolve,
                                                               final A activeType,
                                                               final List<? extends JavaType> parametrizedTypes )
        throws NodeNotFoundException {
        try {
            final List<SLNode> inheritedTypes = new LinkedList<SLNode>();
            inheritedTypes.add(activeType);
            for (final Class<? extends SLLink> linkType : JavaTypeFinder.implementationInheritanceLinks) {
                final SLQuery inheritanceTreeQuery = this.getSession().createQuery();
                inheritanceTreeQuery.selectByLinkType().type(JavaType.class.getName()).subTypes().comma().byLink(
                                                                                                                 linkType.getName()).b().selectEnd().keepResult().executeXTimes();
                final Collection<SLNode> resultFromQuery = inheritanceTreeQuery.execute(inheritedTypes).getNodes();
                inheritedTypes.addAll(resultFromQuery);
            }
            for (final Class<? extends SLLink> linkType : JavaTypeFinder.interfaceInheritanceLinks) {
                final SLQuery inheritanceTreeQuery = this.getSession().createQuery();
                inheritanceTreeQuery.selectByLinkType().type(JavaType.class.getName()).subTypes().comma().byLink(
                                                                                                                 linkType.getName()).b().selectEnd().keepResult().executeXTimes();
                final Collection<SLNode> resultFromQuery = inheritanceTreeQuery.execute(inheritedTypes).getNodes();
                inheritedTypes.addAll(resultFromQuery);
            }

            final SLQuery allTypesFromSamePackagesQuery = this.getSession().createQuery();
            allTypesFromSamePackagesQuery.selectByLinkType().type(JavaPackage.class.getName()).comma().byLink(
                                                                                                              PackageType.class.getName()).a().selectEnd();
            allTypesFromSamePackagesQuery.selectByLinkType().type(JavaType.class.getName()).subTypes().comma().byLink(
                                                                                                                      PackageType.class.getName()).b().selectEnd();
            final Collection<SLNode> allTypesFromSamePackages = allTypesFromSamePackagesQuery.execute(inheritedTypes).getNodes();

            final SLNode slNode = new ComplexTypeFinderQueryExecutor(typeToSolve, allTypesFromSamePackages).getTypeByAllPossibleNames();
            if (slNode == null) {
                throw Exceptions.logAndReturn(new NodeNotFoundException());
            }
            final T typed = (T)this.getTypedNode(slNode);
            return typed;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, NodeNotFoundException.class);
        }
    }

    /**
     * @{inheritDoc
     */

    private <T extends JavaType> T getTypedNode( final SLNode slNode ) throws Exception {
        @SuppressWarnings( "unchecked" )
        final T typedNode = (T)this.getSession().getNodeByID(slNode.getID());
        return typedNode;

    }

}
