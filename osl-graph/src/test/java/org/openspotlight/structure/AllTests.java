package org.openspotlight.structure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openspotlight.structure.predicates.PredicatesTestCase;

@RunWith( value = Suite.class )
@SuiteClasses( value = {SLStructureTestCase.class, PredicatesTestCase.class} )
public class AllTests {
}
