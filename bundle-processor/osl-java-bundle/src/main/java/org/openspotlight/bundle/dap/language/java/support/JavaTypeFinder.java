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

import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspotlight.bundle.dap.language.java.metamodel.link.Extends;
import org.openspotlight.bundle.dap.language.java.metamodel.link.Implements;
import org.openspotlight.bundle.dap.language.java.metamodel.link.JavaLink;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLQuery;
import org.openspotlight.graph.query.SLQueryResult;

/**
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class JavaTypeFinder extends TypeFinder<JavaType, JavaLink> {

    private static final List<Class<? extends JavaLink>> implementationInheritanceLinks = new ArrayList<Class<? extends JavaLink>>();
    private static final List<Class<? extends JavaLink>> interfaceInheritanceLinks      = new ArrayList<Class<? extends JavaLink>>();
    private static final List<Class<? extends JavaLink>> primitiveHierarchyLinks        = new ArrayList<Class<? extends JavaLink>>();
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

    /**
     * @{inheritDoc
     */

    @Override
    public <T extends JavaType> T getType( final String typeToSolve ) throws NodeNotFoundException {
        try {
            final SLNode slNode = this.internalGetNodeByAllPossibleNames(typeToSolve);
            if (slNode == null) {
                throw logAndReturn(new NodeNotFoundException());
            }
            @SuppressWarnings( "unchecked" )
            final T typedNode = (T)this.getSession().getNodeByID(slNode.getID());
            return typedNode;
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
                                                               final List<? extends JavaType> parametrizedTypes ) {
        //find all nodes with name = typeToSolve and linked with packages with name=

        throw new UnsupportedOperationException("not implemented yet");
    }

    private SLNode internalGetNodeByAllPossibleNames( final String typeToSolve ) throws Exception {
        SLNode result = this.internalGetType(typeToSolve);
        if (result != null) {
            return result;
        }
        String newName = typeToSolve;
        while (newName.indexOf(".") != -1) {
            newName = newName.substring(0, newName.lastIndexOf(".")) + "$"
                      + newName.substring(".".length() + newName.lastIndexOf("."));
            result = this.internalGetType(newName);
            if (result != null) {
                return result;
            }
        }
        return null;

    }

    private SLNode internalGetType( final String typeToSolve ) throws Exception {
        final SLQuery query = this.getSession().createQuery();
        query.selectByNodeType().type(JavaType.class.getName()).subTypes().selectEnd().where().type(JavaType.class.getName()).each().property(
                                                                                                                                              "completeName").equalsTo().value(
                                                                                                                                                                               typeToSolve).typeEnd();
        final SLQueryResult result = query.execute();
        final Map<String, List<SLNode>> resultMap = new HashMap<String, List<SLNode>>();
        for (final SLContext ctx : super.getOrderedActiveContexts()) {
            resultMap.put(ctx.getID(), new ArrayList<SLNode>());
        }
        for (final SLNode n : result.getNodes()) {
            final List<SLNode> resultList = resultMap.get(n.getContext().getID());
            if ((resultList != null)) {
                resultList.add(n);
                if (resultList.size() > 1) {
                    //FIXME uncomment this lines
                    //                    logAndThrow(new IllegalStateException(
                    //                                                          format(
                    //                                                                 "Two nodes of the same type and name on the same context: node {0} (parent {2}) inside context {1}",
                    //                                                                 n.getName(), n.getContext().getID(), n.getParent().getName())));
                }
            }
        }
        for (final SLContext ctx : super.getOrderedActiveContexts()) {
            final List<SLNode> resultList = resultMap.get(ctx.getID());
            if (resultList.size() > 0) {
                return resultList.get(0);
            }
        }
        return null;
    }

}
