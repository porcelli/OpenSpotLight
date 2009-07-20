package org.openspotlight.structure.predicates;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.apache.commons.collections15.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.structure.SLStructure;
import org.openspotlight.structure.SLStructureImpl;
import org.openspotlight.structure.elements.SLProperty;
import org.openspotlight.structure.elements.SLSimpleType;
import org.openspotlight.structure.predicates.ClassTypePredicate;
import org.openspotlight.structure.predicates.Example;
import org.openspotlight.structure.predicates.ExamplePredicate;
import org.openspotlight.structure.predicates.SimplePropertyPredicate;
import org.openspotlight.structure.test.domain.link.ClassDeclares;
import org.openspotlight.structure.test.domain.link.JavaClassMethod;
import org.openspotlight.structure.test.domain.link.TableViewColumn;
import org.openspotlight.structure.test.domain.type.ComputedColumn;
import org.openspotlight.structure.test.domain.type.DatabaseColumn;
import org.openspotlight.structure.test.domain.type.DatabaseTable;
import org.openspotlight.structure.test.domain.type.DatabaseView;
import org.openspotlight.structure.test.domain.type.JavaClass;
import org.openspotlight.structure.test.domain.type.JavaClassImpl;
import org.openspotlight.structure.test.domain.type.JavaField;
import org.openspotlight.structure.test.domain.type.JavaMethod;
import org.openspotlight.structure.test.domain.type.SimpleColumn;
import org.openspotlight.structure.test.domain.type.TableView;

public class PredicatesTestCase {

    private SLStructure struct;

    @Before
    public void testAssertCorrectTypes() throws Exception {
        struct = new SLStructureImpl();
        JavaClass c1 = new JavaClass("A", 0L);
        JavaClass c2 = new JavaClass("B", 0L);
        JavaClassImpl c3 = new JavaClassImpl("C", 0L);
        JavaClass c4 = new JavaClass("D", 0L);
        JavaClass c5 = new JavaClass("E", 0L);
        JavaClass c6 = new JavaClass("e", 0L);
        JavaMethod m1 = new JavaMethod("save", 1L);
        JavaMethod m2 = new JavaMethod("remove", 1L);

        m1.addProperty(new SLProperty<String>("Scope", "public"));
        m2.addProperty(new SLProperty<String>("Scope", "private"));

        JavaField f1 = new JavaField("f1", 1L);
        JavaField f2 = new JavaField("f2", 1L);

        TableView t1 = new TableView("Coração", 0L);
        DatabaseView t2 = new DatabaseView("João", 0L);
        DatabaseTable t3 = new DatabaseTable("nao", 0L);

        c1 = (JavaClass)struct.addType(c1);
        c2 = (JavaClass)struct.addType(c2);
        c3 = (JavaClassImpl)struct.addType(c3);
        c4 = (JavaClass)struct.addType(c4);
        c5 = (JavaClass)struct.addType(c5);
        c6 = (JavaClass)struct.addType(c6);
        m1 = (JavaMethod)struct.addType(m1);
        m2 = (JavaMethod)struct.addType(m2);
        f1 = (JavaField)struct.addType(f1);
        f2 = (JavaField)struct.addType(f2);

        struct.addLink(new ClassDeclares(), c1, m1);
        struct.addLink(new ClassDeclares(), c1, m2);
        struct.addLink(new ClassDeclares(), c1, f1);
        struct.addLink(new ClassDeclares(), c1, f2);
        struct.addLink(new JavaClassMethod(), c1, m1);
        struct.addLink(new JavaClassMethod(), c1, m2);

        t1 = (TableView)struct.addType(t1);
        t2 = (DatabaseView)struct.addType(t2);
        t3 = (DatabaseTable)struct.addType(t3);

        ComputedColumn col1 = new ComputedColumn("col1", t3.getHandle());
        ComputedColumn col2 = new ComputedColumn("col2", t3.getHandle());
        ComputedColumn col3 = new ComputedColumn("col3", t3.getHandle());
        SimpleColumn col4 = new SimpleColumn("col4", t3.getHandle());

        col1 = (ComputedColumn)struct.addType(col1);
        col2 = (ComputedColumn)struct.addType(col2);
        col3 = (ComputedColumn)struct.addType(col3);
        col4 = (SimpleColumn)struct.addType(col4);
        struct.addType(col4);

        struct.addLink(new TableViewColumn(), t1, col1);
        struct.addLink(new TableViewColumn(), t1, col2);
        struct.addLink(new TableViewColumn(), t1, col3);
        struct.addLink(new TableViewColumn(), t1, col4);
    }

    @Test
    public void testPredicate2() {
        TableView v = new TableView();
        v.setContextHandle(0L);
        Example example = Example.create(v).allowEmpty("contextHandle").ignoreProperties("path");
        assertEquals(3, struct.getType(new ExamplePredicate(example)).size());
    }

    @Test
    public void testPredicateByExampleCollate1() {
        TableView model = new TableView();
        model.setKey("Coração");
        Example example = Example.create(model);
        ExamplePredicate teste = new ExamplePredicate(example);
        assertEquals(1, struct.getType(teste).size());
    }

    @Test
    public void testPredicateByExampleCollate2() {
        TableView model = new TableView();
        model.setKey("Coracao");
        Example example = Example.create(model);
        ExamplePredicate teste = new ExamplePredicate(example);
        assertEquals(1, CollectionUtils.select(struct.getTypes(), teste).size());
    }

    @Test
    public void testPredicateByExampleCollate3() {
        TableView model = new TableView();
        model.setKey("não");
        Example example = Example.create(model).useSubType(false);
        ExamplePredicate teste = new ExamplePredicate(example);

        assertEquals(0, CollectionUtils.select(struct.getTypes(), teste).size());
    }

    @Test
    public void testPredicateByExampleCollate4() {
        TableView model = new TableView();
        model.setKey("não");

        Example example = Example.create(model).useSubType(true);
        ExamplePredicate teste = new ExamplePredicate(example);

        assertEquals(1, CollectionUtils.select(struct.getTypes(), teste).size());
    }

    @Test
    public void testPredicateByExampleCollate5() {
        TableView model = new DatabaseTable();
        model.setKey("não");

        Example example = Example.create(model);
        ExamplePredicate teste = new ExamplePredicate(example);
        assertEquals(1, CollectionUtils.select(struct.getTypes(), teste).size());
    }

    @Test
    public void testPredicateByExampleCollate6() {
        TableView model = new DatabaseTable();
        model.setKey("nao");

        Example example = Example.create(model).useSubType(false);
        ExamplePredicate teste = new ExamplePredicate(example);
        assertEquals(1, CollectionUtils.select(struct.getTypes(), teste).size());
    }

    @Test
    public void testPredicateByExampleNonCollate() {
        JavaClass model = new JavaClass("e", 0L);

        Example example = Example.create(model).useSubType(false);
        ExamplePredicate teste = new ExamplePredicate(example);
        assertEquals(1, CollectionUtils.select(struct.getTypes(), teste).size());
    }

    @Test
    public void testPredicateByExampleContextHandleAndWrongType() {
        TableView model = new TableView();
        model.setContextHandle(1L);

        Example example = Example.create(model);
        ExamplePredicate teste = new ExamplePredicate(example);
        assertEquals(0, CollectionUtils.select(struct.getTypes(), teste).size());
    }

    @Test
    public void testPredicateByExampleContextHandleAndCorrectType() {
        JavaMethod model = new JavaMethod();
        model.setContextHandle(1L);

        Example example = Example.create(model).useSubType(false);
        ExamplePredicate teste = new ExamplePredicate(example);
        assertEquals(2, CollectionUtils.select(struct.getTypes(), teste).size());
    }

    @Test
    public void testPredicateByExampleContextHandleTypesAndNoSubTypes() {
        SimplePropertyPredicate predicate = new SimplePropertyPredicate("contextHandle", 0L);
        assertEquals(5, CollectionUtils.select(struct.getType(predicate), new ClassTypePredicate(JavaClass.class, false)).size());

        JavaClass model = new JavaClass();
        model.setContextHandle(0L);
        Example example = Example.create(model).useSubType(false).allowEmpty("contextHandle");
        ExamplePredicate teste = new ExamplePredicate(example);
        assertEquals(5, struct.getType(teste).size());
    }

    @Test
    public void testPredicateByExampleContextHandleTypesAndJustSubType() {
        SimplePropertyPredicate predicate = new SimplePropertyPredicate("contextHandle", 0L);
        assertEquals(1,
                     CollectionUtils.select(struct.getType(predicate), new ClassTypePredicate(JavaClassImpl.class, false)).size());

        JavaClassImpl model = new JavaClassImpl();
        model.setContextHandle(0L);
        Example example = Example.create(model).useSubType(false).allowEmpty("contextHandle");
        ExamplePredicate teste = new ExamplePredicate(example);
        assertEquals(1, struct.getType(teste).size());
    }

    @Test
    public void testPredicateByExampleContextHandleAndSubTypes() {
        SimplePropertyPredicate predicate = new SimplePropertyPredicate("contextHandle", 0L);
        assertEquals(6, CollectionUtils.select(struct.getType(predicate), new ClassTypePredicate(JavaClass.class, true)).size());

        JavaClass model = new JavaClass();
        model.setContextHandle(0L);
        Example example = Example.create(model).useSubType(true).allowEmpty("contextHandle").ignoreProperties("label");
        ExamplePredicate teste = new ExamplePredicate(example);
        assertEquals(6, struct.getType(teste).size());
    }

    @Test
    public void testPredicateByExampleContextHandle() {
        SimplePropertyPredicate predicate = new SimplePropertyPredicate("contextHandle", 0L);
        assertEquals(9, struct.getType(predicate).size());

        SLSimpleType model = new SLSimpleType();
        model.setContextHandle(0L);
        Example example = Example.create(model).useSubType(true).allowEmpty("contextHandle").ignoreProperties("label",
                                                                                                              "collatorLevel");
        assertEquals(9, struct.getType(new ExamplePredicate(example)).size());
    }

    @Test
    public void testRemoveType() {
        struct.removeType(JavaClass.class, false);
        assertEquals(8, struct.getTypes().size());
    }

    @Test
    public void testRemoveTypeAndSubType() {
        struct.removeType(JavaClass.class, true);
        assertEquals(7, struct.getTypes().size());
    }

    @Test
    public void testRemoveLink() {
        assertEquals(17, struct.getTypes().size());
        struct.removeLink(ClassDeclares.class);
        assertEquals(6, struct.getLinkCount());
        assertEquals(17, struct.getTypes().size());
    }

    @Test
    public void testRemoveLink2() {
        assertEquals(17, struct.getTypes().size());
        struct.removeLink(JavaClassMethod.class);
        assertEquals(8, struct.getLinkCount());
        assertEquals(17, struct.getTypes().size());
    }

    @Test
    public void testTypeByLink() {
        assertEquals(4, struct.getConnectedTypes(1L).size());
        assertEquals(1, struct.getConnectedTypes(7L).size());
        assertEquals(5, struct.getTypeByLink(ClassDeclares.class).size());
        assertEquals(3, struct.getTypeByLink(JavaClassMethod.class).size());
        assertEquals(4, struct.getTypeByLink(ClassDeclares.class, 1L).size());
        assertEquals(2, struct.getTypeByLink(JavaClassMethod.class, 1L).size());
        assertEquals(2, struct.getTypeByLink(ClassDeclares.class, 1L, JavaMethod.class, false).size());
        assertEquals(2, struct.getTypeByLink(ClassDeclares.class, 1L, JavaField.class, false).size());

        assertEquals(1, struct.getTypeByLink(ClassDeclares.class, 7L).size());
        assertEquals(1, struct.getTypeByLink(JavaClassMethod.class, 7L).size());
        assertEquals(0, struct.getTypeByLink(ClassDeclares.class, 7L, JavaMethod.class, false).size());
        assertEquals(0, struct.getTypeByLink(ClassDeclares.class, 7L, JavaField.class, false).size());
        assertEquals(1, struct.getTypeByLink(ClassDeclares.class, 7L, JavaClass.class, false).size());
        assertEquals(1, struct.getTypeByLink(ClassDeclares.class, 7L, JavaClass.class, true).size());

        assertEquals(5, struct.getTypeByLink(TableViewColumn.class).size());
        assertEquals(4, struct.getTypeByLink(TableViewColumn.class, 17L).size());

        assertEquals(3, struct.getTypeByLink(TableViewColumn.class, 17L, ComputedColumn.class, false).size());
        assertEquals(1, struct.getTypeByLink(TableViewColumn.class, 17L, SimpleColumn.class, false).size());
        assertEquals(0, struct.getTypeByLink(TableViewColumn.class, 17L, DatabaseColumn.class, false).size());
        assertEquals(4, struct.getTypeByLink(TableViewColumn.class, 17L, DatabaseColumn.class, true).size());
    }

    @Test
    public void testSimplePropertyPredicate() throws Exception {
        struct = new SLStructureImpl();
        JavaClass c1 = new JavaClass("A", 0L);
        JavaClass c2 = new JavaClass("B", 0L);
        struct.addType(c1);
        struct.addType(c2);
        Collection<SLSimpleType> nodes = struct.getType(new SimplePropertyPredicate("contextHandle", 0L));
        assertEquals(2, nodes.size());
    }
}
