package org.openspotlight.persist.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.persist.test.LevelOneObj;
import org.openspotlight.persist.test.LevelThreeObj;
import org.openspotlight.persist.test.LevelTwoObj;
import org.openspotlight.persist.test.ListItemObj;
import org.openspotlight.persist.test.MapValueObj;
import org.openspotlight.persist.test.RootObj;

@Ignore
public class SimplePersistVisitorSupportTest {

	public RootObj createSampleData() {
		final RootObj root = new RootObj();
		final LevelOneObj obj1 = new LevelOneObj();
		final LevelTwoObj obj2 = new LevelTwoObj();
		final LevelThreeObj obj3 = new LevelThreeObj();
		final ListItemObj li1 = new ListItemObj();
		li1.setName("1");
		li1.setValue(1);
		final ListItemObj li2 = new ListItemObj();
		li2.setName("2");
		li2.setValue(2);
		final ListItemObj li3 = new ListItemObj();
		li3.setName("3");
		li3.setValue(3);
		obj3.getObjList().add(li1);
		obj3.getObjList().add(li2);
		obj3.getObjList().add(li3);
		final MapValueObj mapVal1 = new MapValueObj();
		mapVal1.setName("1");
		final MapValueObj mapVal2 = new MapValueObj();
		mapVal2.setName("2");
		final MapValueObj mapVal3 = new MapValueObj();
		mapVal3.setName("3");
		obj3.getObjMap().put(1, mapVal1);
		obj3.getObjMap().put(2, mapVal2);
		obj3.getObjMap().put(3, mapVal3);
		obj1.setRootObj(root);
		obj2.setLevelOneObj(obj1);
		obj3.setLevelTwoObj(obj2);
		obj3.setBooleanList(new ArrayList<Boolean>());
		obj3.getBooleanList().add(Boolean.TRUE);
		obj3.getBooleanList().add(Boolean.FALSE);
		obj3.getBooleanList().add(Boolean.TRUE);
		obj3.getBooleanList().add(Boolean.TRUE);
		obj3.setNumberMap(new HashMap<Double, Integer>());
		obj3.getNumberMap().put(1.0, 3);
		obj3.getNumberMap().put(2.0, 2);
		obj3.getNumberMap().put(3.0, 1);
		return root;
	}

	@Test
	public void shouldVisitCollectionProperties() throws Exception {
		final RootObj root = this.createSampleData();
		final AtomicInteger count = new AtomicInteger(0);
		SimpleNodeTypeVisitorSupport.acceptVisitorOn(ListItemObj.class, root,
				new SimpleNodeTypeVisitor<ListItemObj>() {

					public <X extends ListItemObj> void visitBean(final X bean) {
						count.incrementAndGet();

					}
				});
		Assert.assertThat(count.get(), Is.is(3));
	}

	@Test
	public void shouldVisitMapValueProperties() throws Exception {
		final RootObj root = this.createSampleData();
		final AtomicInteger count = new AtomicInteger(0);
		SimpleNodeTypeVisitorSupport.acceptVisitorOn(MapValueObj.class, root,
				new SimpleNodeTypeVisitor<MapValueObj>() {

					public <X extends MapValueObj> void visitBean(final X bean) {
						count.incrementAndGet();

					}
				});
		Assert.assertThat(count.get(), Is.is(2));
	}

	@Test
	public void shouldVisitSimpleProperties() throws Exception {
		final RootObj root = this.createSampleData();
		final AtomicInteger count = new AtomicInteger(0);
		SimpleNodeTypeVisitorSupport.acceptVisitorOn(LevelTwoObj.class, root,
				new SimpleNodeTypeVisitor<LevelTwoObj>() {

					public <X extends LevelTwoObj> void visitBean(final X bean) {
						count.incrementAndGet();

					}
				});
		Assert.assertThat(count.get(), Is.is(1));
	}
}
