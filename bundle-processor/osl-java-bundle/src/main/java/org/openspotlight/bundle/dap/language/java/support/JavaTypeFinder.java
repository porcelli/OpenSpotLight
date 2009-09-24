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

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Exceptions.logAndThrow;
import static org.openspotlight.common.util.Strings.replaceLast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspotlight.bundle.dap.language.java.metamodel.link.Extends;
import org.openspotlight.bundle.dap.language.java.metamodel.link.Implements;
import org.openspotlight.bundle.dap.language.java.metamodel.link.PackageType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
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
            this.actualString = replaceLast(this.actualString, ".", "$");
            return this.actualString;
        }

        private SLNode getPrefferedType( final SLQueryResult result ) throws Exception {
            final Map<String, List<SLNode>> resultMap = new HashMap<String, List<SLNode>>();
            for (final SLContext ctx : JavaTypeFinder.this.getOrderedActiveContexts()) {
                resultMap.put(ctx.getID(), new ArrayList<SLNode>());
            }
            for (final SLNode n : result.getNodes()) {
                final List<SLNode> resultList = resultMap.get(n.getContext().getID());
                if ((resultList != null)) {
                    resultList.add(n);
                    if (resultList.size() > 1) {
                        logAndThrow(new IllegalStateException(
                                                              format(
                                                                     "Two nodes of the same type and name on the same context: node {0} (parent {2}) inside context {1}",
                                                                     n.getName(), n.getContext().getID(), n.getParent().getName())));
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
        implementationInheritanceLinks.add(Extends.class);
        interfaceInheritanceLinks.add(Implements.class);
        primitiveTypes.add(JavaTypePrimitive.class);
        primitiveHierarchyLinks.add(Extends.class);//FIXME

    }

    public JavaTypeFinder(
                           final SLContext abstractContext, final List<SLContext> orderedActiveContexts,
                           final boolean enableBoxing, final SLGraphSession session ) {
        super(implementationInheritanceLinks, interfaceInheritanceLinks, primitiveHierarchyLinks, abstractContext,
              orderedActiveContexts, primitiveTypes, enableBoxing, session);
    }

    @Override
    public <T extends JavaType> T getType( final String typeToSolve ) throws NodeNotFoundException {
        try {
            final SLNode slNode = new SimpleGetTypeFinder(typeToSolve).getTypeByAllPossibleNames();
            if (slNode == null) {
                throw logAndReturn(new NodeNotFoundException());
            }
            return this.getTypedNode(slNode);
        } catch (final Exception e) {
            throw logAndReturnNew(e, NodeNotFoundException.class);
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    public <T extends JavaType, A extends JavaType> T getType( final String typeToSolve,
                                                               final A activeType,
                                                               final List<? extends JavaType> parametrizedTypes )
        throws NodeNotFoundException {
        try {
            final SLQuery inheritanceTreeQuery = this.getSession().createQuery();
            inheritanceTreeQuery.selectByNodeType().type(JavaType.class.getName()).subTypes().selectEnd().where().type(
                                                                                                                       JavaType.class.getName()).subTypes().each().property(
                                                                                                                                                                            "completeName").equalsTo().value(
                                                                                                                                                                                                             activeType.getCompleteName()).typeEnd().whereEnd().keepResult();
            inheritanceTreeQuery.selectByLinkType().type(JavaType.class.getName()).subTypes().comma().byLink(
                                                                                                             Extends.class.getName()).b().selectEnd().keepResult().executeXTimes();
            final Collection<SLNode> inheritedTypes = inheritanceTreeQuery.execute().getNodes();
            final SLQuery allTypesFromSamePackagesQuery = this.getSession().createQuery();
            allTypesFromSamePackagesQuery.selectByLinkType().type(JavaPackage.class.getName()).comma().byLink(
                                                                                                              PackageType.class.getName()).a().selectEnd();
            allTypesFromSamePackagesQuery.selectByLinkType().type(JavaType.class.getName()).subTypes().comma().byLink(
                                                                                                                      PackageType.class.getName()).b().selectEnd();
            final Collection<SLNode> allTypesFromSamePackages = allTypesFromSamePackagesQuery.execute(inheritedTypes).getNodes();

            final SLNode slNode = new ComplexTypeFinderQueryExecutor(typeToSolve, allTypesFromSamePackages).getTypeByAllPossibleNames();
            if (slNode == null) {
                throw logAndReturn(new NodeNotFoundException());
            }
            final T typed = this.getTypedNode(slNode);
            return typed;
            //FIXME finish the loop for all link types
        } catch (final Exception e) {
            throw logAndReturnNew(e, NodeNotFoundException.class);
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
