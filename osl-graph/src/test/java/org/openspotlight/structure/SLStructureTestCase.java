package org.openspotlight.structure;

import static org.junit.Assert.assertEquals;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.Test;
import org.openspotlight.structure.SLStructure;
import org.openspotlight.structure.SLStructureImpl;
import org.openspotlight.structure.elements.SLProperty;
import org.openspotlight.structure.elements.SLSimpleType;
import org.openspotlight.structure.test.domain.link.ClassDeclares;
import org.openspotlight.structure.test.domain.link.JavaClassMethod;
import org.openspotlight.structure.test.domain.link.TableViewColumn;
import org.openspotlight.structure.test.domain.type.JavaClass;
import org.openspotlight.structure.test.domain.type.JavaClassImpl;
import org.openspotlight.structure.test.domain.type.JavaField;
import org.openspotlight.structure.test.domain.type.JavaMethod;
import org.openspotlight.structure.test.domain.type.TableView;
import org.w3c.dom.Node;

public class SLStructureTestCase {
    private SLStructure struct;

    @Test
    public void testAssertCorrectTypes() throws Exception {
        struct = new SLStructureImpl();
        JavaClass c1 = new JavaClass("A", 0L);
        JavaClass c2 = new JavaClass("B", 0L);
        JavaClassImpl c3 = new JavaClassImpl("A", 0L);
        JavaClass c4 = new JavaClass("A", 0L);
        JavaClassImpl c5 = new JavaClassImpl("C", 0L);
        JavaClass c6 = new JavaClass("C", 0L);
        c1.addProperty(new SLProperty<String>("Scope", "public"));
        c1.addProperty(new SLProperty<Integer>("Lines Of Code", 2));
        c3.addProperty(new SLProperty<Integer>("Lines Of Code", 250));
        c3.addProperty(new SLProperty<String>("Author", "somebody"));
        SLSimpleType c1Return = struct.addType(c1);
        struct.addType(c2);
        SLSimpleType c3Return = struct.addType(c3);
        SLSimpleType c4Return = struct.addType(c4);
        SLSimpleType c5Return = struct.addType(c5);
        SLSimpleType c6Return = struct.addType(c6);
        assertEquals(c1Return.getHandle(), c3Return.getHandle());
        assertEquals(c3.getClass().toString(), c3Return.getClass().toString());
        assertEquals(c3.getClass().toString(), c4Return.getClass().toString());
        assertEquals(c5Return.getHandle(), c6Return.getHandle());
        assertEquals(c5.getClass().toString(), c6Return.getClass().toString());
        assertEquals("public", c3Return.getProperty("Scope").getValue());
        assertEquals(250, c3Return.getProperty("Lines Of Code").getValue());
        assertEquals("somebody", c3Return.getProperty("Author").getValue());
    }

    @Test
    public void testAssertSameKeyAndContextHandle() throws Exception {
        struct = new SLStructureImpl();
        JavaClass c1 = new JavaClass("A", 0L);
        JavaMethod m1 = new JavaMethod("remove", 1L);
        JavaField f1 = new JavaField("remove", 1L);
        struct.addType(c1);
        struct.addType(m1);
        struct.addType(f1);
        assertEquals(3, struct.getTypeCount());
    }

    @Test
    public void testAssertCorrectLinks() throws Exception {
        struct = new SLStructureImpl();
        JavaClass c1 = new JavaClass("A", 0L);
        JavaMethod m1 = new JavaMethod("save", 1L);
        JavaMethod m2 = new JavaMethod("remove", 1L);
        JavaField f1 = new JavaField("remove", 1L);
        struct.addLink(new JavaClassMethod(), c1, m1);
        struct.addLink(new JavaClassMethod(), c1, m2);
        struct.addLink(new JavaClassMethod(), c1, m1);
        assertEquals(2, struct.getLinkCount());
        SLSimpleType c1Return = struct.addType(c1);
        struct.addType(m1);
        struct.addType(m2);
        struct.addType(f1);
        //add some edges from c1 to m1 and m2
        struct.addLink(new JavaClassMethod(), c1, m1);
        struct.addLink(new JavaClassMethod(), c1, m2);
        struct.addLink(new ClassDeclares(), c1, m1);
        struct.addLink(new ClassDeclares(), c1, m2);
        struct.addLink(new ClassDeclares(), c1, f1);
        JavaClassImpl c1Impl = new JavaClassImpl("A", 0L);
        SLSimpleType c1ImplReturn = struct.addType(c1Impl);
        assertEquals(5, struct.getLinkCount());
        assertEquals(c1Return.getHandle(), c1ImplReturn.getHandle());
        assertEquals(3, struct.getConnectedTypes(c1ImplReturn.getHandle()).size());
    }

    @Test
    public void testBidirectionalLink() throws Exception {
        struct = new SLStructureImpl();
        JavaClass c1 = new JavaClass("A", 0L);
        JavaMethod m1 = new JavaMethod("save", 1L);
        SLSimpleType c1Return = struct.addType(c1);
        SLSimpleType m1Return = struct.addType(m1);
        struct.addLink(new JavaClassMethod(), c1Return, m1Return);
        struct.addLink(new JavaClassMethod(), c1Return, m1Return);
        struct.addLink(new JavaClassMethod(), m1Return, c1Return);
        assertEquals(struct.getLinkCount(), 2);
    }

    @Test
    public void testCheckTypeReplacing() throws Exception {
        struct = new SLStructureImpl();
        JavaClass c1 = new JavaClass("A", 0L);
        JavaMethod m1 = new JavaMethod("save", 1L);
        SLSimpleType c1Return = struct.addType(c1);
        SLSimpleType m1Return = struct.addType(m1);
        struct.addLink(new JavaClassMethod(), c1Return, m1Return);
        struct.addLink(new JavaClassMethod(), c1Return, m1Return);
        struct.addLink(new JavaClassMethod(), m1Return, c1Return);
        JavaClassImpl sameC1 = new JavaClassImpl("A", 0L);
        struct.addType(sameC1);
        assertEquals(2, struct.getLinkCount());
        assertEquals(2, struct.getTypeCount());
    }

//    public void testXLN() throws Exception {
//        struct = new SLStructureImpl();
//        JavaClassImpl c1 = new JavaClassImpl("A", 0L);
//        struct.addType(c1);
//        struct.addLineReference(c1, "c:\\test\\Foo.java", 1, 1, 1, 10, "import");
//        struct.addLineReference(c1, "c:\\test\\Foo.java", 5, 5, 1, 15, "use");
//        StringWriter writer = new StringWriter();
//        StreamResult result = new StreamResult(writer);
//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer transformer = tf.newTransformer();
//        transformer.transform(struct.getXLNRepresentation(), result);
//        System.out.println(writer.toString());
//    }

//    @Test
//    public void testReallyLargeStructure() throws Exception {
//        struct = new SLStructureImpl();
//        long start = System.currentTimeMillis();
//        ArrayList<SLSimpleType> nodes = new ArrayList<SLSimpleType>(50000);
//        for (long i = 0; i < 30000; i++) {
//            JavaClass c = new JavaClass("" + i, i);
//            nodes.add(struct.addType(c));
//        }
//        for (long i = 30000; i < 50000; i++) {
//            JavaClassImpl c = new JavaClassImpl("" + i, i);
//            nodes.add(struct.addType(c));
//        }
//        long end = System.currentTimeMillis();
//        long total = end - start;
//        System.out.println("Total time to add " + total + " ms");
//        start = System.currentTimeMillis();
//        for (int i = 0; i < 12000; i++) {
//            JavaClass c = (JavaClass)nodes.get(i);
//            JavaClassImpl replace = new JavaClassImpl(c.getKey(), c.getContextHandle());
//            struct.addType(replace);
//        }
//        end = System.currentTimeMillis();
//        total += (end - start);
//        System.out.println("Total time to replace generic by implementation " + (end - start) + " ms");
//        start = System.currentTimeMillis();
//        for (int i = 30000; i < 36000; i++) {
//            JavaClassImpl c = (JavaClassImpl)nodes.get(i);
//            JavaClass replace = new JavaClass(c.getKey(), c.getContextHandle());
//            struct.addType(replace);
//        }
//        end = System.currentTimeMillis();
//        total += (end - start);
//        System.out.println("Total time to replace implementation by generic " + (end - start) + " ms");
//        System.out.println("Total time of execution " + total + " ms");
//        assertEquals(struct.getTypeCount(), 18000);
//    }

//    @Test
//    public void concurrentTest() throws Exception {
//        final int THREAD_COUNT = 5;
//        final int INSERTS_PER_THREAD = 10000;
//        struct = new SLStructureImpl();
//        Collection<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>(THREAD_COUNT);
//        for (int i = 0; i < THREAD_COUNT; i++) {
//            final int start = i * INSERTS_PER_THREAD;
//            final int end = start + INSERTS_PER_THREAD;
//            tasks.add(new Callable<Integer>() {
//                public Integer call() throws Exception {
//                    int total = 0;
//                    for (long i = start; i < end; i++) {
//                        total++;
//                        struct.addType(new JavaClass("" + i, i));
//                    }
//                    return total;
//                }
//            });
//        }
//        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
//        List<Future<Integer>> totals = executorService.invokeAll(tasks);
//        int totalNodes = 0;
//        for (Future<Integer> f : totals) {
//            totalNodes += f.get();
//        }
//        assertEquals(totalNodes, struct.getTypeCount());
//    }

//    @Test
//    public void testXOBFileExport() throws Exception {
//        struct = new SLStructureImpl();
//        JavaClass c1 = new JavaClass("A", 0L);
//        c1.setCaption("A Class");
//        JavaMethod m1 = new JavaMethod("save", 1L);
//        m1.setCaption("save()");
//        m1.addProperty(new SLProperty<String>("scope", "public"));
//        m1.addProperty(new SLProperty<Integer>("nada", 1));
//        SLSimpleType c1Return = struct.addType(c1);
//        SLSimpleType m1Return = struct.addType(m1);
//        struct.addLink(new JavaClassMethod(), c1Return, m1Return);
//        struct.addLink(new JavaClassMethod(), c1Return, m1Return);
//        struct.addLink(new JavaClassMethod(), m1Return, c1Return);
//        StringWriter writer = new StringWriter();
//        StreamResult result = new StreamResult(writer);
//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer transformer = tf.newTransformer();
//        transformer.transform(struct.getXOBRepresentation(), result);
//        System.out.println(writer.toString());
//    }

    @Test
    public void testCollatePrimaryTypes() throws Exception {
        struct = new SLStructureImpl();
        TableView c1 = new TableView("Cole��o", 0);
        struct.addType(c1);
        TableView C1 = new TableView("Colecao", 0);
        struct.addType(C1);
        assertEquals(struct.getTypeCount(), 1);
    }

    @Test
    public void testGetType() {
        struct = new SLStructureImpl();
        TableView c1 = new TableView("Coleção", 0);
        TableView cReturn = (TableView)struct.addType(c1);
        assertEquals(struct.getType(cReturn.getHandle()), cReturn);
        struct.removeType(cReturn.getHandle());
        assertEquals(0, struct.getTypes().size());
    }

    @Test
    public void testPath() {
        struct = new SLStructureImpl();
        SLSimpleType c1 = struct.addType(new JavaClass("A", 0L));
        SLSimpleType c2 = struct.addType(new JavaMethod("X", c1.getHandle()));
        System.out.println(c2.getPath());
    }

    @Test
    public void testLineReference() {
        struct = new SLStructureImpl();
        TableView c1 = new TableView("Coleção", 0);
        c1 = (TableView)struct.addType(c1);
        TableView c2 = new TableView("Coleção2", 0);
        c2 = (TableView)struct.addType(c2);
        struct.addLineReference(c1, "/test/test1.java", 10, 10, 25, 50, "VAR DECLARE");
        struct.addLineReference(c1, "/test/test1.java", 10, 10, 25, 51, "VAR DECLARE");
        struct.addLineReference(c1, "/test/test1.java", 10, 10, 26, 51, "VAR DECLARE");
        struct.addLineReference(c1, "/test/test1.java", 10, 11, 26, 51, "VAR DECLARE");
        struct.addLineReference(c1, "/test/test1.java", 11, 11, 26, 51, "VAR DECLARE");
        struct.addLineReference(c1, "/test/test1.java", 10, 10, 25, 50, "VAR DECLARE");
        struct.addLineReference(c2, "/test/test1.java", 10, 10, 25, 50, "VAR DECLARE");
        struct.addLineReference(c2, "/test/test1.java", 10, 10, 25, 50, "OTHER XXXXX");
        struct.addLineReference(c2, "/test/test1.java", 20, 20, 25, 50, "OTHER STATEMENT");
        struct.addLineReference(c2, "/test/test1.java", 20, 20, 25, 50, "OTHER STATEMENT");
        struct.addLineReference(c1, "/test/test2.java", 10, 10, 25, 50, "VAR DECLARE");
        DOMSource source = (DOMSource)struct.getXLNRepresentation();
        assertEquals(2, source.getNode().getFirstChild().getChildNodes().getLength());
        for (int i = 0; i < source.getNode().getFirstChild().getChildNodes().getLength(); i++) {
            Node activeFileNode = source.getNode().getFirstChild().getChildNodes().item(i);
            if (activeFileNode.getAttributes().getNamedItem("Path").getNodeValue().equals("/test/test1.java")) {
                assertEquals(8, activeFileNode.getChildNodes().getLength());
            } else if (activeFileNode.getAttributes().getNamedItem("Path").getNodeValue().equals("/test/test2.java")) {
                assertEquals(1, activeFileNode.getChildNodes().getLength());
            }
        }
    }

    @Test( expected = IllegalArgumentException.class )
    public void testLineReferenceProblem0() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2 = (TableView)struct.addType(c2);
        struct.addLineReference(c2, "/test/test1.java", -20, 20, 25, 50, "OTHER STATEMENT");
    }

    @Test( expected = IllegalArgumentException.class )
    public void testLineReferenceProblem1() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2 = (TableView)struct.addType(c2);
        struct.addLineReference(c2, "/test/test1.java", 20, -20, 25, 50, "OTHER STATEMENT");
    }

    @Test( expected = IllegalArgumentException.class )
    public void testLineReferenceProblem2() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2 = (TableView)struct.addType(c2);
        struct.addLineReference(c2, "/test/test1.java", 20, 20, -25, 50, "OTHER STATEMENT");
    }

    @Test( expected = IllegalArgumentException.class )
    public void testLineReferenceProblem3() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2 = (TableView)struct.addType(c2);
        struct.addLineReference(c2, "/test/test1.java", 20, 20, 25, -50, "OTHER STATEMENT");
    }

    @Test( expected = IllegalArgumentException.class )
    public void testLineReferenceProblem4() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2 = (TableView)struct.addType(c2);
        struct.addLineReference(c2, "/test/test1.java", 21, 20, 25, 50, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testLineReferenceProblem5() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2 = (TableView)struct.addType(c2);
        struct.addLineReference(null, "/test/test2.java", 10, 10, 25, 50, "VAR DECLARE");
    }

    @Test( expected = IllegalArgumentException.class )
    public void testLineReferenceProblem6() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2 = (TableView)struct.addType(c2);
        struct.addLineReference(c2, "/test/test2.java", 10, 10, 25, 50, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testLineReferenceProblem7() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2 = (TableView)struct.addType(c2);
        struct.addLineReference(c2, null, 10, 10, 25, 50, "STATEMENT");
    }

    @Test( expected = IllegalArgumentException.class )
    public void testLineReferenceProblem8() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2.setHandle(1L);
        struct.addLineReference(c2, "/test/teste1.java", 10, 10, 25, 50, "STATEMENT");
    }

    @Test
    public void testGetConnectedTypesInvalid() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2.setHandle(1L);
        assertEquals(0, struct.getConnectedTypes(30L).size());
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetTypeInvalid() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2.setHandle(1L);
        assertEquals(0, struct.getType(null).size());
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetTypeByLinkInvalid1() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2.setHandle(1L);
        assertEquals(0, struct.getTypeByLink(null).size());
    }

    @Test
    public void testGetTypeByLinkInvalid2() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2.setHandle(1L);
        assertEquals(0, struct.getTypeByLink(ClassDeclares.class, -1L).size());
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetTypeByLinkInvalid3() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2.setHandle(1L);
        assertEquals(0, struct.getTypeByLink(null, 1L).size());
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetTypeByLinkInvalid4() {
        struct = new SLStructureImpl();
        TableView c2 = new TableView("Coleção2", 0);
        c2.setHandle(1L);
        assertEquals(0, struct.getTypeByLink(ClassDeclares.class, 1L, null, true).size());
    }

    @Test( expected = IllegalArgumentException.class )
    public void testRemoveTypeInvalid1() {
        struct = new SLStructureImpl();
        struct.removeType(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testRemoveTypeInvalid2() {
        struct = new SLStructureImpl();
        struct.removeType(null, true);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testRemoveLinkTypeInvalid() {
        struct = new SLStructureImpl();
        struct.removeLink(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testNullType() {
        struct = new SLStructureImpl();
        TableView c1 = new TableView();
        struct.addType(c1);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testNullType2() {
        struct = new SLStructureImpl();
        struct.addType(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testNullLinkType0() {
        struct = new SLStructureImpl();
        TableView c1 = new TableView("Coleção", 0);
        c1 = (TableView)struct.addType(c1);
        TableView c2 = new TableView("não", 0);
        c2 = (TableView)struct.addType(c2);
        struct.addLink(null, c1, c2);
        assertEquals(0, struct.getLinkCount());
    }

    @Test( expected = IllegalArgumentException.class )
    public void testNullLinkType1() {
        struct = new SLStructureImpl();
        TableView c1 = new TableView("Coleção", 0);
        c1 = (TableView)struct.addType(c1);
        TableView c2 = new TableView("não", 0);
        c2 = (TableView)struct.addType(c2);
        struct.addLink(new TableViewColumn(), c1, null);
        assertEquals(0, struct.getLinkCount());
    }

    @Test( expected = IllegalArgumentException.class )
    public void testNullLinkType2() {
        struct = new SLStructureImpl();
        TableView c1 = new TableView("Coleção", 0);
        c1 = (TableView)struct.addType(c1);
        TableView c2 = new TableView("não", 0);
        c2 = (TableView)struct.addType(c2);
        struct.addLink(new TableViewColumn(), null, c2);
        assertEquals(0, struct.getLinkCount());
    }
}
