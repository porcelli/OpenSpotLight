package org.openspotlight.persist.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class SimplePersistVisitorSupportTest {

	private static class SimpleObject1 implements SimpleNodeType {

		private boolean flag;

		private String name;

		private List<SimpleObject2> children = new ArrayList<SimpleObject2>();

		private List<SimpleObject2> anotherChildren = new ArrayList<SimpleObject2>();

		public List<SimpleObject2> getAnotherChildren() {
			return this.anotherChildren;
		}

		public List<SimpleObject2> getChildren() {
			return this.children;
		}

		@KeyProperty
		public String getName() {
			return this.name;
		}

		public boolean isFlag() {
			return this.flag;
		}

		public void setAnotherChildren(final List<SimpleObject2> anotherChildren) {
			this.anotherChildren = anotherChildren;
		}

		public void setChildren(final List<SimpleObject2> children) {
			this.children = children;
		}

		public void setFlag(final boolean flag) {
			this.flag = flag;
		}

		public void setName(final String name) {
			this.name = name;
		}

	}

	private static class SimpleObject2 implements SimpleNodeType {

		private boolean flag;

		private String name;

		private SimpleObject1 parent;

		private Map<String, SimpleObject3> map = new HashMap<String, SimpleObject3>();

		public Map<String, SimpleObject3> getMap() {
			return this.map;
		}

		@KeyProperty
		public String getName() {
			return this.name;
		}

		@ParentProperty
		public SimpleObject1 getParent() {
			return this.parent;
		}

		public boolean isFlag() {
			return this.flag;
		}

		public void setFlag(final boolean flag) {
			this.flag = flag;
		}

		public void setMap(final Map<String, SimpleObject3> map) {
			this.map = map;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public void setParent(final SimpleObject1 parent) {
			this.parent = parent;
		}

	}

	private static class SimpleObject3 implements SimpleNodeType {

		private boolean flag;

		private String name;

		private SimpleObject2 parent;

		private SimpleObject4 child;

		public SimpleObject4 getChild() {
			return this.child;
		}

		@KeyProperty
		public String getName() {
			return this.name;
		}

		@ParentProperty
		public SimpleObject2 getParent() {
			return this.parent;
		}

		public boolean isFlag() {
			return this.flag;
		}

		public void setChild(final SimpleObject4 child) {
			this.child = child;
		}

		public void setFlag(final boolean flag) {
			this.flag = flag;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public void setParent(final SimpleObject2 parent) {
			this.parent = parent;
		}

	}

	private static class SimpleObject4 implements SimpleNodeType {

		private boolean flag;

		private String name;
		private SimpleObject4 autoRelationShip;
		private SimpleObject4 parentObj4;
		private SimpleObject3 parent;

		public SimpleObject4 getAutoRelationShip() {
			return this.autoRelationShip;
		}

		@KeyProperty
		public String getName() {
			return this.name;
		}

		@ParentProperty
		public SimpleObject3 getParent() {
			return this.parent;
		}

		@ParentProperty
		public SimpleObject4 getParentObj4() {
			return this.parentObj4;
		}

		public boolean isFlag() {
			return this.flag;
		}

		public void setAutoRelationShip(final SimpleObject4 autoRelationShip) {
			this.autoRelationShip = autoRelationShip;
		}

		public void setFlag(final boolean flag) {
			this.flag = flag;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public void setParent(final SimpleObject3 parent) {
			this.parent = parent;
		}

		public void setParentObj4(final SimpleObject4 parentObj4) {
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
		final SimpleObject1 root = this.createSampleData();
		final AtomicInteger count = new AtomicInteger(0);
		SimpleNodeTypeVisitorSupport.acceptVisitorOn(SimpleObject2.class, root,
				new SimpleNodeTypeVisitor<SimpleObject2>() {

					public <X extends SimpleObject2> void visitBean(final X bean) {
						count.incrementAndGet();

					}
				});
		Assert.assertThat(count.get(), Is.is(2));
	}

	@Test
	public void shouldVisitMapValueProperties() throws Exception {
		final SimpleObject1 root = this.createSampleData();
		final AtomicInteger count = new AtomicInteger(0);
		SimpleNodeTypeVisitorSupport.acceptVisitorOn(SimpleObject3.class, root,
				new SimpleNodeTypeVisitor<SimpleObject3>() {

					public <X extends SimpleObject3> void visitBean(final X bean) {
						count.incrementAndGet();

					}
				});
		Assert.assertThat(count.get(), Is.is(3));
	}

	@Test
	public void shouldVisitSimpleProperties() throws Exception {
		final SimpleObject1 root = this.createSampleData();
		final AtomicInteger count = new AtomicInteger(0);
		SimpleNodeTypeVisitorSupport.acceptVisitorOn(SimpleObject4.class, root,
				new SimpleNodeTypeVisitor<SimpleObject4>() {

					public <X extends SimpleObject4> void visitBean(final X bean) {
						count.incrementAndGet();

					}
				});
		Assert.assertThat(count.get(), Is.is(2));
	}
}
