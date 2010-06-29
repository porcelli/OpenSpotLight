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
package org.openspotlight.persist.util;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings( "unused" )
public class SimplePersistVisitorSupportTest {

    private static class NodeNameVisitor implements SimpleNodeTypeVisitor<NodeObject> {
        private final List<String> names = new ArrayList<String>();

        public List<String> getNames() {
            return names;
        }

        public void visitBean( final NodeObject bean ) {
            bean.getName().toString();
            names.add(bean.getName());
        }
    }

    private static class NodeObject implements SimpleNodeType {

        private String           name;

        private NodeObject       parent;

        private List<NodeObject> children = new ArrayList<NodeObject>();

        public List<NodeObject> getChildren() {
            return children;
        }

        @KeyProperty
        public String getName() {
            return name;
        }

        @ParentProperty
        public NodeObject getParent() {
            return parent;
        }

        public void setChildren( final List<NodeObject> children ) {
            this.children = children;
        }

        public void setName( final String name ) {
            this.name = name;
        }

        public void setParent( final NodeObject parent ) {
            this.parent = parent;
        }

    }

    private static class SimpleObject1 implements SimpleNodeType {

        private boolean             flag;

        private String              name;

        private List<SimpleObject2> children        = new ArrayList<SimpleObject2>();

        private List<SimpleObject2> anotherChildren = new ArrayList<SimpleObject2>();

        public List<SimpleObject2> getAnotherChildren() {
            return anotherChildren;
        }

        public List<SimpleObject2> getChildren() {
            return children;
        }

        @KeyProperty
        public String getName() {
            return name;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setAnotherChildren( final List<SimpleObject2> anotherChildren ) {
            this.anotherChildren = anotherChildren;
        }

        public void setChildren( final List<SimpleObject2> children ) {
            this.children = children;
        }

        public void setFlag( final boolean flag ) {
            this.flag = flag;
        }

        public void setName( final String name ) {
            this.name = name;
        }

    }

    private static class SimpleObject2 implements SimpleNodeType {

        private boolean                    flag;

        private String                     name;

        private SimpleObject1              parent;

        private Map<String, SimpleObject3> map = new HashMap<String, SimpleObject3>();

        public Map<String, SimpleObject3> getMap() {
            return map;
        }

        @KeyProperty
        public String getName() {
            return name;
        }

        @ParentProperty
        public SimpleObject1 getParent() {
            return parent;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag( final boolean flag ) {
            this.flag = flag;
        }

        public void setMap( final Map<String, SimpleObject3> map ) {
            this.map = map;
        }

        public void setName( final String name ) {
            this.name = name;
        }

        public void setParent( final SimpleObject1 parent ) {
            this.parent = parent;
        }

    }

    private static class SimpleObject3 implements SimpleNodeType {

        private boolean       flag;

        private String        name;

        private SimpleObject2 parent;

        private SimpleObject4 child;

        public SimpleObject4 getChild() {
            return child;
        }

        @KeyProperty
        public String getName() {
            return name;
        }

        @ParentProperty
        public SimpleObject2 getParent() {
            return parent;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setChild( final SimpleObject4 child ) {
            this.child = child;
        }

        public void setFlag( final boolean flag ) {
            this.flag = flag;
        }

        public void setName( final String name ) {
            this.name = name;
        }

        public void setParent( final SimpleObject2 parent ) {
            this.parent = parent;
        }

    }

    private static class SimpleObject4 implements SimpleNodeType {

        private boolean       flag;

        private String        name;
        private SimpleObject4 autoRelationShip;
        private SimpleObject4 parentObj4;
        private SimpleObject3 parent;

        public SimpleObject4 getAutoRelationShip() {
            return autoRelationShip;
        }

        @KeyProperty
        public String getName() {
            return name;
        }

        @ParentProperty
        public SimpleObject3 getParent() {
            return parent;
        }

        @ParentProperty
        public SimpleObject4 getParentObj4() {
            return parentObj4;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setAutoRelationShip( final SimpleObject4 autoRelationShip ) {
            this.autoRelationShip = autoRelationShip;
        }

        public void setFlag( final boolean flag ) {
            this.flag = flag;
        }

        public void setName( final String name ) {
            this.name = name;
        }

        public void setParent( final SimpleObject3 parent ) {
            this.parent = parent;
        }

        public void setParentObj4( final SimpleObject4 parentObj4 ) {
            this.parentObj4 = parentObj4;
        }

    }

    public SimpleObject1 createSampleData() {
        final SimpleObject1 so1 = new SimpleObject1();
        so1.setName("root");
        so1.setAnotherChildren(null);
        final SimpleObject2 so2 = new SimpleObject2();
        so2.setName("2");
        so2.setParent(so1);
        so1.getChildren().add(so2);
        final SimpleObject2 so22 = new SimpleObject2();
        so22.setName("22");
        so22.setParent(so1);
        so1.getChildren().add(so22);
        final SimpleObject3 so3 = new SimpleObject3();
        so3.setName("3");
        so3.setParent(so2);
        so2.getMap().put("3", so3);
        final SimpleObject3 so33 = new SimpleObject3();
        so33.setName("33");
        so33.setParent(so2);
        so2.getMap().put("33", so33);
        final SimpleObject3 so333 = new SimpleObject3();
        so33.setName("333");
        so33.setParent(so22);
        so22.getMap().put("333", so333);
        final SimpleObject4 so4 = new SimpleObject4();
        so4.setName("4");
        so4.setParent(so33);
        so33.setChild(so4);
        final SimpleObject4 so44 = new SimpleObject4();
        so44.setName("44");
        so44.setAutoRelationShip(so4);
        so4.setAutoRelationShip(so44);// circular reference
        return so1;
    }

    @Test
    public void shouldVisitCollectionProperties() throws Exception {
        final SimpleObject1 root = createSampleData();
        final AtomicInteger count = new AtomicInteger(0);
        SimpleNodeTypeVisitorSupport.acceptVisitorOn(SimpleObject2.class, root, new SimpleNodeTypeVisitor<SimpleObject2>() {

            public void visitBean( final SimpleObject2 bean ) {
                count.incrementAndGet();

            }
        });
        Assert.assertThat(count.get(), Is.is(2));
    }

    @Test
    public void shouldVisitInCorrectOrder() throws Exception {
        final NodeObject root = new NodeObject();
        root.setName("0");
        final NodeObject child1 = new NodeObject();
        child1.setName("1");
        final NodeObject child2 = new NodeObject();
        child2.setName("2");
        final NodeObject child3 = new NodeObject();
        child3.setName("3");
        final NodeObject child4 = new NodeObject();
        child4.setName("4");
        final NodeObject child2_1 = new NodeObject();
        child2_1.setName("2-1");
        final NodeObject child2_2 = new NodeObject();
        child2_2.setName("2-2");
        final NodeObject child2_3 = new NodeObject();
        child2_3.setName("2-3");
        final NodeObject child3_1 = new NodeObject();
        child3_1.setName("3-1");
        final NodeObject child3_2 = new NodeObject();
        child3_2.setName("3-2");
        final NodeObject child3_3 = new NodeObject();
        child3_3.setName("3-3");

        child1.setParent(root);
        child2.setParent(root);
        child3.setParent(root);
        child4.setParent(root);
        child2_1.setParent(child2);
        child2_2.setParent(child2);
        child2_3.setParent(child2);
        child3_1.setParent(child3);
        child3_2.setParent(child3);
        child3_3.setParent(child3);
        root.getChildren().add(child1);
        root.getChildren().add(child2);
        root.getChildren().add(child3);
        root.getChildren().add(child4);
        child2.getChildren().add(child2_1);
        child2.getChildren().add(child2_2);
        child2.getChildren().add(child2_3);
        child3.getChildren().add(child3_1);
        child3.getChildren().add(child3_2);
        child3.getChildren().add(child3_3);

        final NodeNameVisitor visitor = new NodeNameVisitor();
        SimpleNodeTypeVisitorSupport.acceptVisitorOn(NodeObject.class, root, visitor);

        final List<String> foundNames = visitor.getNames();
        final List<String> expectedNames = Arrays.asList("0", "1", "2", "3", "4", "2-1", "2-2", "2-3", "3-1", "3-2", "3-3");
        Assert.assertThat(foundNames.toString(), Is.is(expectedNames.toString()));

    }

    @Test
    public void shouldVisitMapValueProperties() throws Exception {
        final SimpleObject1 root = createSampleData();
        final AtomicInteger count = new AtomicInteger(0);
        SimpleNodeTypeVisitorSupport.acceptVisitorOn(SimpleObject3.class, root, new SimpleNodeTypeVisitor<SimpleObject3>() {

            public void visitBean( final SimpleObject3 bean ) {
                count.incrementAndGet();

            }
        });
        Assert.assertThat(count.get(), Is.is(3));
    }

    @Test
    public void shouldVisitSimpleProperties() throws Exception {
        final SimpleObject1 root = createSampleData();
        final AtomicInteger count = new AtomicInteger(0);
        SimpleNodeTypeVisitorSupport.acceptVisitorOn(SimpleObject4.class, root, new SimpleNodeTypeVisitor<SimpleObject4>() {

            public void visitBean( final SimpleObject4 bean ) {
                count.incrementAndGet();

            }
        });
        Assert.assertThat(count.get(), Is.is(2));
    }
}
