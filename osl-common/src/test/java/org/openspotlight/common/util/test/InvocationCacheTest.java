package org.openspotlight.common.util.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.util.InvocationCacheFactory;


@SuppressWarnings( "boxing" )
public class InvocationCacheTest {

    private ExampleClass exampleWrapped;
    private ExampleClass exampleCreated;

    @Before
    public void createExample() throws Exception {
        this.exampleWrapped = InvocationCacheFactory.wrapIntoCached(new ExampleClass());
        this.exampleCreated = InvocationCacheFactory.createIntoCached(ExampleClassWithNonDefaultConstructor.class,
                                                                      new Class<?>[] {String.class}, new Object[] {""});
    }

    @Test
    public void shouldInvokeVoidMethodFasterWithinCreated() throws Exception {
        final long start1 = System.currentTimeMillis();
        this.exampleCreated.expensiveVoidMethod();
        final long end1 = System.currentTimeMillis();
        final long diff1 = end1 - start1;
        final long start2 = System.currentTimeMillis();
        this.exampleCreated.expensiveVoidMethod();
        final long end2 = System.currentTimeMillis();
        final long diff2 = end2 - start2;
        assertThat(diff1 > diff2, is(true));
    }

    @Test
    public void shouldInvokeVoidMethodFasterWithinWrapped() throws Exception {
        final long start1 = System.currentTimeMillis();
        this.exampleWrapped.expensiveVoidMethod();
        final long end1 = System.currentTimeMillis();
        final long diff1 = end1 - start1;
        final long start2 = System.currentTimeMillis();
        this.exampleWrapped.expensiveVoidMethod();
        final long end2 = System.currentTimeMillis();
        final long diff2 = end2 - start2;
        assertThat(diff1 > diff2, is(true));
    }

    @Test
    public void shouldReturnDifferentObjectsFasterWithinCreated() throws Exception {
        final long start1 = System.currentTimeMillis();
        final Object o1 = this.exampleCreated.someExpensiveMethodWithParameter(false, "1");
        final long end1 = System.currentTimeMillis();
        final long start2 = System.currentTimeMillis();
        final Object o2 = this.exampleCreated.someExpensiveMethodWithParameter(false, "1");
        final long end2 = System.currentTimeMillis();
        final long start3 = System.currentTimeMillis();
        final Object o3 = this.exampleCreated.someExpensiveMethodWithParameter(false, "2");
        final long end3 = System.currentTimeMillis();
        final long start4 = System.currentTimeMillis();
        final Object o4 = this.exampleCreated.someExpensiveMethodWithParameter(false, "2");
        final long end4 = System.currentTimeMillis();
        final long diff1 = end1 - start1;
        final long diff2 = end2 - start2;
        final long diff3 = end3 - start3;
        final long diff4 = end4 - start4;
        assertThat(o1 == o2, is(true));
        assertThat(diff1 > diff2, is(true));

        assertThat(o3 == o4, is(true));
        assertThat(diff3 > diff4, is(true));

        assertThat(o1 != o3, is(true));
        assertThat(o1 != o4, is(true));

    }

    @Test
    public void shouldReturnDifferentObjectsFasterWithinWrapped() throws Exception {
        final long start1 = System.currentTimeMillis();
        final Object o1 = this.exampleWrapped.someExpensiveMethodWithParameter(false, "1");
        final long end1 = System.currentTimeMillis();
        final long start2 = System.currentTimeMillis();
        final Object o2 = this.exampleWrapped.someExpensiveMethodWithParameter(false, "1");
        final long end2 = System.currentTimeMillis();
        final long start3 = System.currentTimeMillis();
        final Object o3 = this.exampleWrapped.someExpensiveMethodWithParameter(false, "2");
        final long end3 = System.currentTimeMillis();
        final long start4 = System.currentTimeMillis();
        final Object o4 = this.exampleWrapped.someExpensiveMethodWithParameter(false, "2");
        final long end4 = System.currentTimeMillis();
        final long diff1 = end1 - start1;
        final long diff2 = end2 - start2;
        final long diff3 = end3 - start3;
        final long diff4 = end4 - start4;
        assertThat(o1 == o2, is(true));
        assertThat(diff1 > diff2, is(true));

        assertThat(o3 == o4, is(true));
        assertThat(diff3 > diff4, is(true));

        assertThat(o1 != o3, is(true));
        assertThat(o1 != o4, is(true));

    }

    @Test
    public void shouldReturnSameObjectFasterWithinCreated() throws Exception {
        final long start1 = System.currentTimeMillis();
        final Object o1 = this.exampleCreated.expensiveNonVoidMethod();
        final long end1 = System.currentTimeMillis();
        final long diff1 = end1 - start1;
        final long start2 = System.currentTimeMillis();
        final Object o2 = this.exampleCreated.expensiveNonVoidMethod();
        final long end2 = System.currentTimeMillis();
        final long diff2 = end2 - start2;
        assertThat(o1 == o2, is(true));
        assertThat(diff1 > diff2, is(true));
    }

    @Test
    public void shouldReturnSameObjectFasterWithinWrapped() throws Exception {
        final long start1 = System.currentTimeMillis();
        final Object o1 = this.exampleWrapped.expensiveNonVoidMethod();
        final long end1 = System.currentTimeMillis();
        final long diff1 = end1 - start1;
        final long start2 = System.currentTimeMillis();
        final Object o2 = this.exampleWrapped.expensiveNonVoidMethod();
        final long end2 = System.currentTimeMillis();
        final long diff2 = end2 - start2;
        assertThat(o1 == o2, is(true));
        assertThat(diff1 > diff2, is(true));
    }

    @Test
    public void shouldThrowDifferentExceptionsFasterWithinCreated() throws Exception {
        Exception e1 = null, e2 = null;
        Exception e3 = null, e4 = null;
        final long start1 = System.currentTimeMillis();
        try {
            this.exampleCreated.someExpensiveMethodWithParameter(true, "1");
        } catch (final Exception e) {
            e1 = e;
        }
        final long end1 = System.currentTimeMillis();
        final long diff1 = end1 - start1;
        final long start2 = System.currentTimeMillis();
        try {
            this.exampleCreated.someExpensiveMethodWithParameter(true, "1");
        } catch (final Exception e) {
            e2 = e;
        }
        final long end2 = System.currentTimeMillis();
        final long diff2 = end2 - start2;

        final long start3 = System.currentTimeMillis();
        try {
            this.exampleCreated.someExpensiveMethodWithParameter(true, "2");
        } catch (final Exception e) {
            e3 = e;
        }
        final long end3 = System.currentTimeMillis();
        final long diff3 = end3 - start3;
        final long start4 = System.currentTimeMillis();
        try {
            this.exampleCreated.someExpensiveMethodWithParameter(true, "2");
        } catch (final Exception e) {
            e4 = e;
        }
        final long end4 = System.currentTimeMillis();
        final long diff4 = end4 - start4;

        assertThat(e1, is(notNullValue()));
        assertThat(e2, is(notNullValue()));
        assertThat(e1 == e2, is(true));
        assertThat(diff1 > diff2, is(true));

        assertThat(e3, is(notNullValue()));
        assertThat(e4, is(notNullValue()));
        assertThat(e3 == e4, is(true));
        assertThat(diff3 > diff4, is(true));

        assertThat(e1 != e3, is(true));
        assertThat(e1 != e4, is(true));
    }

    @Test
    public void shouldThrowDifferentExceptionsFasterWithinWrapped() throws Exception {
        Exception e1 = null, e2 = null;
        Exception e3 = null, e4 = null;
        final long start1 = System.currentTimeMillis();
        try {
            this.exampleWrapped.someExpensiveMethodWithParameter(true, "1");
        } catch (final Exception e) {
            e1 = e;
        }
        final long end1 = System.currentTimeMillis();
        final long diff1 = end1 - start1;
        final long start2 = System.currentTimeMillis();
        try {
            this.exampleWrapped.someExpensiveMethodWithParameter(true, "1");
        } catch (final Exception e) {
            e2 = e;
        }
        final long end2 = System.currentTimeMillis();
        final long diff2 = end2 - start2;

        final long start3 = System.currentTimeMillis();
        try {
            this.exampleWrapped.someExpensiveMethodWithParameter(true, "2");
        } catch (final Exception e) {
            e3 = e;
        }
        final long end3 = System.currentTimeMillis();
        final long diff3 = end3 - start3;
        final long start4 = System.currentTimeMillis();
        try {
            this.exampleWrapped.someExpensiveMethodWithParameter(true, "2");
        } catch (final Exception e) {
            e4 = e;
        }
        final long end4 = System.currentTimeMillis();
        final long diff4 = end4 - start4;

        assertThat(e1, is(notNullValue()));
        assertThat(e2, is(notNullValue()));
        assertThat(e1 == e2, is(true));
        assertThat(diff1 > diff2, is(true));

        assertThat(e3, is(notNullValue()));
        assertThat(e4, is(notNullValue()));
        assertThat(e3 == e4, is(true));
        assertThat(diff3 > diff4, is(true));

        assertThat(e1 != e3, is(true));
        assertThat(e1 != e4, is(true));
    }

    @Test
    public void shouldThrowExceptionFasterWithinCreated() throws Exception {
        Exception e1 = null, e2 = null;
        final long start1 = System.currentTimeMillis();
        try {
            this.exampleCreated.dangerousExpensiveMethod();
        } catch (final Exception e) {
            e1 = e;
        }
        final long end1 = System.currentTimeMillis();
        final long diff1 = end1 - start1;
        final long start2 = System.currentTimeMillis();
        try {
            this.exampleCreated.dangerousExpensiveMethod();
        } catch (final Exception e) {
            e2 = e;
        }
        final long end2 = System.currentTimeMillis();
        final long diff2 = end2 - start2;
        assertThat(e1, is(notNullValue()));
        assertThat(e2, is(notNullValue()));
        assertThat(e1 == e2, is(true));
        assertThat(diff1 > diff2, is(true));
    }

    @Test
    public void shouldThrowExceptionFasterWithinWrapped() throws Exception {
        Exception e1 = null, e2 = null;
        final long start1 = System.currentTimeMillis();
        try {
            this.exampleWrapped.dangerousExpensiveMethod();
        } catch (final Exception e) {
            e1 = e;
        }
        final long end1 = System.currentTimeMillis();
        final long diff1 = end1 - start1;
        final long start2 = System.currentTimeMillis();
        try {
            this.exampleWrapped.dangerousExpensiveMethod();
        } catch (final Exception e) {
            e2 = e;
        }
        final long end2 = System.currentTimeMillis();
        final long diff2 = end2 - start2;
        assertThat(e1, is(notNullValue()));
        assertThat(e2, is(notNullValue()));
        assertThat(e1 == e2, is(true));
        assertThat(diff1 > diff2, is(true));
    }

}
