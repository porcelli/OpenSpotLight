package org.openspotlight.graph.query.parser;

import org.antlr.gunit.gUnitBaseTest;

public class TestSLQLTestWalker extends gUnitBaseTest {

    public void setUp() {
        this.packagePath = "./org/openspotlight/slql/parser";
        this.lexerPath = "org.openspotlight.slql.parser.SLQLLexer";
        this.parserPath = "org.openspotlight.slql.parser.SLQLParser";
        this.treeParserPath = "org.openspotlight.slql.parser.SLQLTestWalker";
    }


    public void testUseCollatorLevel_walks_UseCollatorLevel1() throws Exception {
        // test input: "use collator level identical"
        Object retval = execTreeParser("useCollatorLevel", "useCollatorLevel", "use collator level identical", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "useCollatorLevel", expecting, actual);
    }

    public void testUseCollatorLevel_walks_UseCollatorLevel2() throws Exception {
        // test input: "use collator level primary"
        Object retval = execTreeParser("useCollatorLevel", "useCollatorLevel", "use collator level primary", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "useCollatorLevel", expecting, actual);
    }

    public void testUseCollatorLevel_walks_UseCollatorLevel3() throws Exception {
        // test input: "use collator level secondary"
        Object retval = execTreeParser("useCollatorLevel", "useCollatorLevel", "use collator level secondary", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "useCollatorLevel", expecting, actual);
    }

    public void testUseCollatorLevel_walks_UseCollatorLevel4() throws Exception {
        // test input: "use collator level tertiary"
        Object retval = execTreeParser("useCollatorLevel", "useCollatorLevel", "use collator level tertiary", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "useCollatorLevel", expecting, actual);
    }

    public void testUseCollatorLevel_walks_UseCollatorLevel5() throws Exception {
        // test input: "use collator level identical"
        Object retval = execTreeParser("useCollatorLevel", "useCollatorLevel", "use collator level identical", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "useCollatorLevel", expecting, actual);
    }

    public void testUseCollatorLevel_walks_UseCollatorLevel6() throws Exception {
        // test input: "use collator level primary"
        Object retval = execTreeParser("useCollatorLevel", "useCollatorLevel", "use collator level primary", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "useCollatorLevel", expecting, actual);
    }

    public void testUseCollatorLevel_walks_UseCollatorLevel7() throws Exception {
        // test input: "use collator level secondary"
        Object retval = execTreeParser("useCollatorLevel", "useCollatorLevel", "use collator level secondary", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "useCollatorLevel", expecting, actual);
    }

    public void testUseCollatorLevel_walks_UseCollatorLevel8() throws Exception {
        // test input: "use collator level tertiary"
        Object retval = execTreeParser("useCollatorLevel", "useCollatorLevel", "use collator level tertiary", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "useCollatorLevel", expecting, actual);
    }

    public void testDefineOutput_walks_DefineOutput1() throws Exception {
        // test input: "define output = generalDiagram;"
        Object retval = execTreeParser("defineOutput", "defineOutput", "define output = generalDiagram;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineOutput", expecting, actual);
    }

    public void testDefineOutput_walks_DefineOutput2() throws Exception {
        // test input: "define output = [composed name];"
        Object retval = execTreeParser("defineOutput", "defineOutput", "define output = [composed name];", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineOutput", expecting, actual);
    }

    public void testDefineOutput_walks_DefineOutput3() throws Exception {
        // test input: "define output = [composed name].something;"
        Object retval = execTreeParser("defineOutput", "defineOutput", "define output = [composed name].something;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineOutput", expecting, actual);
    }

    public void testDefineOutput_walks_DefineOutput4() throws Exception {
        // test input: "define output = org.something.ClassName;"
        Object retval = execTreeParser("defineOutput", "defineOutput", "define output = org.something.ClassName;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineOutput", expecting, actual);
    }

    public void testDefineOutput_walks_DefineOutput5() throws Exception {
        // test input: "define output = org.[something].test;"
        Object retval = execTreeParser("defineOutput", "defineOutput", "define output = org.[something].test;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineOutput", expecting, actual);
    }

    public void testDefineOutput_walks_DefineOutput6() throws Exception {
        // test input: "define output = org.[something with space].test;"
        Object retval = execTreeParser("defineOutput", "defineOutput", "define output = org.[something with space].test;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineOutput", expecting, actual);
    }

    public void testDefineOutput_walks_DefineOutput7() throws Exception {
        // test input: "define output = generalDiagram;"
        Object retval = execTreeParser("defineOutput", "defineOutput", "define output = generalDiagram;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineOutput", expecting, actual);
    }

    public void testDefineOutput_walks_DefineOutput8() throws Exception {
        // test input: "define output = [composed name];"
        Object retval = execTreeParser("defineOutput", "defineOutput", "define output = [composed name];", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineOutput", expecting, actual);
    }

    public void testDefineOutput_walks_DefineOutput9() throws Exception {
        // test input: "define output = [composed name].something;"
        Object retval = execTreeParser("defineOutput", "defineOutput", "define output = [composed name].something;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineOutput", expecting, actual);
    }

    public void testDefineOutput_walks_DefineOutput10() throws Exception {
        // test input: "define output = org.something.ClassName;"
        Object retval = execTreeParser("defineOutput", "defineOutput", "define output = org.something.ClassName;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineOutput", expecting, actual);
    }

    public void testDefineOutput_walks_DefineOutput11() throws Exception {
        // test input: "define output = org.[something].test;"
        Object retval = execTreeParser("defineOutput", "defineOutput", "define output = org.[something].test;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineOutput", expecting, actual);
    }

    public void testDefineOutput_walks_DefineOutput12() throws Exception {
        // test input: "define output = org.[something with space].test;"
        Object retval = execTreeParser("defineOutput", "defineOutput", "define output = org.[something with space].test;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineOutput", expecting, actual);
    }

    public void testDefineTarget_walks_DefineTarget1() throws Exception {
        // test input: "define target = testeTarget;"
        Object retval = execTreeParser("defineTarget", "defineTarget", "define target = testeTarget;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineTarget", expecting, actual);
    }

    public void testDefineTarget_walks_DefineTarget2() throws Exception {
        // test input: "define target = testeTarget keep result;"
        Object retval = execTreeParser("defineTarget", "defineTarget", "define target = testeTarget keep result;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineTarget", expecting, actual);
    }

    public void testDefineTarget_walks_DefineTarget3() throws Exception {
        // test input: "define target = testeTarget;"
        Object retval = execTreeParser("defineTarget", "defineTarget", "define target = testeTarget;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineTarget", expecting, actual);
    }

    public void testDefineTarget_walks_DefineTarget4() throws Exception {
        // test input: "define target = testeTarget keep result;"
        Object retval = execTreeParser("defineTarget", "defineTarget", "define target = testeTarget keep result;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineTarget", expecting, actual);
    }

    public void testDefineTarget_walks_DefineTarget5() throws Exception {
        // test input: "define target = select *;"
        Object retval = execTreeParser("defineTarget", "defineTarget", "define target = select *;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineTarget", expecting, actual);
    }

    public void testDefineTarget_walks_DefineTarget6() throws Exception {
        // test input: "define target = \n\tselect ** \n\t\twhere org.test.Something property myProperty == \"TesteValue\"; "
        Object retval = execTreeParser("defineTarget", "defineTarget", "define target = \n\tselect ** \n\t\twhere org.test.Something property myProperty == \"TesteValue\"; ", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineTarget", expecting, actual);
    }

    public void testDefineTarget_walks_DefineTarget7() throws Exception {
        // test input: "define target = \n\tselect ** \n\t\twhere org.test.Something property myProperty == \"TesteValue\" keep result; "
        Object retval = execTreeParser("defineTarget", "defineTarget", "define target = \n\tselect ** \n\t\twhere org.test.Something property myProperty == \"TesteValue\" keep result; ", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineTarget", expecting, actual);
    }

    public void testDefineMessage_walks_DefineMessage1() throws Exception {
        // test input: "define message $teste = \"entre com um valor\";"
        Object retval = execTreeParser("defineMessage", "defineMessage", "define message $teste = \"entre com um valor\";", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineMessage", expecting, actual);
    }

    public void testDefineMessage_walks_DefineMessage2() throws Exception {
        // test input: "define message @teste = \"entre com um valor\";"
        Object retval = execTreeParser("defineMessage", "defineMessage", "define message @teste = \"entre com um valor\";", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineMessage", expecting, actual);
    }

    public void testDefineMessage_walks_DefineMessage3() throws Exception {
        // test input: "define message &teste = \"entre com um valor\";"
        Object retval = execTreeParser("defineMessage", "defineMessage", "define message &teste = \"entre com um valor\";", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineMessage", expecting, actual);
    }

    public void testDefineMessage_walks_DefineMessage4() throws Exception {
        // test input: "define message #teste = \"entre com um valor\";"
        Object retval = execTreeParser("defineMessage", "defineMessage", "define message #teste = \"entre com um valor\";", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineMessage", expecting, actual);
    }

    public void testDefineMessage_walks_DefineMessage5() throws Exception {
        // test input: "define message $teste = \"entre com um valor\";"
        Object retval = execTreeParser("defineMessage", "defineMessage", "define message $teste = \"entre com um valor\";", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineMessage", expecting, actual);
    }

    public void testDefineMessage_walks_DefineMessage6() throws Exception {
        // test input: "define message @teste = \"entre com um valor\";"
        Object retval = execTreeParser("defineMessage", "defineMessage", "define message @teste = \"entre com um valor\";", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineMessage", expecting, actual);
    }

    public void testDefineMessage_walks_DefineMessage7() throws Exception {
        // test input: "define message &teste = \"entre com um valor\";"
        Object retval = execTreeParser("defineMessage", "defineMessage", "define message &teste = \"entre com um valor\";", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineMessage", expecting, actual);
    }

    public void testDefineMessage_walks_DefineMessage8() throws Exception {
        // test input: "define message #teste = \"entre com um valor\";"
        Object retval = execTreeParser("defineMessage", "defineMessage", "define message #teste = \"entre com um valor\";", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineMessage", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues1() throws Exception {
        // test input: "define domain values $teste = \"teste1\" ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values $teste = \"teste1\" ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues2() throws Exception {
        // test input: "define domain values $teste = \"1\" ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values $teste = \"1\" ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues3() throws Exception {
        // test input: "define domain values #teste = 1 ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values #teste = 1 ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues4() throws Exception {
        // test input: "define domain values &teste = 1 ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values &teste = 1 ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues5() throws Exception {
        // test input: "define domain values &teste = 1.1 ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values &teste = 1.1 ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues6() throws Exception {
        // test input: "define domain values $teste = \"1\", \"2\", \"12\" ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values $teste = \"1\", \"2\", \"12\" ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues7() throws Exception {
        // test input: "define domain values $teste = \"teste1\", \"teste2\", \"teste3\" ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values $teste = \"teste1\", \"teste2\", \"teste3\" ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues8() throws Exception {
        // test input: "define domain values #teste = 1,2,3,14 ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values #teste = 1,2,3,14 ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues9() throws Exception {
        // test input: "define domain values &teste = 1.1,2,33.3,4 ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values &teste = 1.1,2,33.3,4 ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues10() throws Exception {
        // test input: "define domain values $teste = \"teste1\" ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values $teste = \"teste1\" ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues11() throws Exception {
        // test input: "define domain values #teste = 1 ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values #teste = 1 ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues12() throws Exception {
        // test input: "define domain values &teste = 1 ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values &teste = 1 ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues13() throws Exception {
        // test input: "define domain values &teste = 1.1 ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values &teste = 1.1 ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues14() throws Exception {
        // test input: "define domain values $teste = \"teste1\", \"teste2\", \"teste3\" ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values $teste = \"teste1\", \"teste2\", \"teste3\" ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues15() throws Exception {
        // test input: "define domain values #teste = 1,2,3,14 ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values #teste = 1,2,3,14 ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testDefineDominValues_walks_DefineDominValues16() throws Exception {
        // test input: "define domain values &teste = 1.1,2,33.3,4 ;"
        Object retval = execTreeParser("defineDominValues", "defineDominValues", "define domain values &teste = 1.1,2,33.3,4 ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "defineDominValues", expecting, actual);
    }

    public void testExecuting_walks_Executing1() throws Exception {
        // test input: "executing 1 times"
        Object retval = execTreeParser("executing", "executing", "executing 1 times", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "executing", expecting, actual);
    }

    public void testExecuting_walks_Executing2() throws Exception {
        // test input: "executing 100 times"
        Object retval = execTreeParser("executing", "executing", "executing 100 times", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "executing", expecting, actual);
    }

    public void testExecuting_walks_Executing3() throws Exception {
        // test input: "executing #test times"
        Object retval = execTreeParser("executing", "executing", "executing #test times", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "executing", expecting, actual);
    }

    public void testExecuting_walks_Executing4() throws Exception {
        // test input: "executing n times"
        Object retval = execTreeParser("executing", "executing", "executing n times", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "executing", expecting, actual);
    }

    public void testExecuting_walks_Executing5() throws Exception {
        // test input: "executing 1 times"
        Object retval = execTreeParser("executing", "executing", "executing 1 times", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "executing", expecting, actual);
    }

    public void testExecuting_walks_Executing6() throws Exception {
        // test input: "executing 100 times"
        Object retval = execTreeParser("executing", "executing", "executing 100 times", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "executing", expecting, actual);
    }

    public void testExecuting_walks_Executing7() throws Exception {
        // test input: "executing #test times"
        Object retval = execTreeParser("executing", "executing", "executing #test times", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "executing", expecting, actual);
    }

    public void testExecuting_walks_Executing8() throws Exception {
        // test input: "executing n times"
        Object retval = execTreeParser("executing", "executing", "executing n times", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "executing", expecting, actual);
    }

    public void testOrderBy_walks_OrderBy1() throws Exception {
        // test input: "order by org.test.JavaType"
        Object retval = execTreeParser("orderBy", "orderBy", "order by org.test.JavaType", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "orderBy", expecting, actual);
    }

    public void testOrderBy_walks_OrderBy2() throws Exception {
        // test input: "order by \n\torg.test.JavaType property myPropertyName, property xxx"
        Object retval = execTreeParser("orderBy", "orderBy", "order by \n\torg.test.JavaType property myPropertyName, property xxx", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "orderBy", expecting, actual);
    }

    public void testOrderBy_walks_OrderBy3() throws Exception {
        // test input: "order by \n\torg.test.JavaType property myPropertyName, property otherProperty,\n\torg.test.JavaPackage property otherProperty, property myProperty"
        Object retval = execTreeParser("orderBy", "orderBy", "order by \n\torg.test.JavaType property myPropertyName, property otherProperty,\n\torg.test.JavaPackage property otherProperty, property myProperty", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "orderBy", expecting, actual);
    }

    public void testOrderBy_walks_OrderBy4() throws Exception {
        // test input: "order by org.test.JavaType, org.test.JavaPackage "
        Object retval = execTreeParser("orderBy", "orderBy", "order by org.test.JavaType, org.test.JavaPackage ", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "orderBy", expecting, actual);
    }

    public void testOrderBy_walks_OrderBy5() throws Exception {
        // test input: "order by org.test.JavaType "
        Object retval = execTreeParser("orderBy", "orderBy", "order by org.test.JavaType ", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "orderBy", expecting, actual);
    }

    public void testOrderBy_walks_OrderBy6() throws Exception {
        // test input: "order by \n\torg.test.JavaType property myPropertyName, property xxx"
        Object retval = execTreeParser("orderBy", "orderBy", "order by \n\torg.test.JavaType property myPropertyName, property xxx", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "orderBy", expecting, actual);
    }

    public void testOrderBy_walks_OrderBy7() throws Exception {
        // test input: "order by \n\torg.test.JavaType property myPropertyName, property otherProperty,\n\torg.test.JavaPackage property otherProperty, property myProperty"
        Object retval = execTreeParser("orderBy", "orderBy", "order by \n\torg.test.JavaType property myPropertyName, property otherProperty,\n\torg.test.JavaPackage property otherProperty, property myProperty", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "orderBy", expecting, actual);
    }

    public void testOrderBy_walks_OrderBy8() throws Exception {
        // test input: "order by org.test.JavaType, org.test.JavaPackage "
        Object retval = execTreeParser("orderBy", "orderBy", "order by org.test.JavaType, org.test.JavaPackage ", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "orderBy", expecting, actual);
    }

    public void testSelect_walks_Select1() throws Exception {
        // test input: "select *;"
        Object retval = execTreeParser("select", "select", "select *;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select2() throws Exception {
        // test input: "select * keep result;"
        Object retval = execTreeParser("select", "select", "select * keep result;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select3() throws Exception {
        // test input: "select * keep result use collator level primary;"
        Object retval = execTreeParser("select", "select", "select * keep result use collator level primary;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select4() throws Exception {
        // test input: "select * use collator level primary;"
        Object retval = execTreeParser("select", "select", "select * use collator level primary;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select5() throws Exception {
        // test input: "select **\nwhere myType property myProperty == \"TesteValue\"\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == \"TesteValue\"\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select6() throws Exception {
        // test input: "select **\nwhere myType property myProperty == 1\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == 1\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select7() throws Exception {
        // test input: "select **\nwhere myType property myProperty == 1.1\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == 1.1\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select8() throws Exception {
        // test input: "select **\nwhere myType property myProperty == @teste\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == @teste\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select9() throws Exception {
        // test input: "select **\nwhere myType property myProperty == #teste\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == #teste\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select10() throws Exception {
        // test input: "select **\nwhere myType property myProperty == $teste\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == $teste\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select11() throws Exception {
        // test input: "select **\nwhere myType property myProperty == &teste\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == &teste\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select12() throws Exception {
        // test input: "select **\nwhere myType property myProperty == true\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == true\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select13() throws Exception {
        // test input: "select **\nwhere myType property myProperty == true\nlimit 10\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == true\nlimit 10\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select14() throws Exception {
        // test input: "select **\nwhere myType property myProperty == true\nlimit 10 offset 11\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == true\nlimit 10 offset 11\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select15() throws Exception {
        // test input: "select **\nwhere myType property myProperty == true\nlimit #test offset #test2\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == true\nlimit #test offset #test2\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select16() throws Exception {
        // test input: "select **\nwhere myType property myProperty == true\nlimit #test2 offset 10\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == true\nlimit #test2 offset 10\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select17() throws Exception {
        // test input: "select **\nwhere myType property myProperty == false\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == false\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select18() throws Exception {
        // test input: "select **\nwhere myType property myProperty == null\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere myType property myProperty == null\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select19() throws Exception {
        // test input: "select nodeType1;"
        Object retval = execTreeParser("select", "select", "select nodeType1;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select20() throws Exception {
        // test input: "select nodeType2, nodeType3;"
        Object retval = execTreeParser("select", "select", "select nodeType2, nodeType3;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select21() throws Exception {
        // test input: "select nodeType2, nodeType3\nwhere nodeType1 property myProperty == 3;"
        Object retval = execTreeParser("select", "select", "select nodeType2, nodeType3\nwhere nodeType1 property myProperty == 3;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select22() throws Exception {
        // test input: "select nodeType1.*, nodeType3\nwhere nodeType2 property myProperty == 3;"
        Object retval = execTreeParser("select", "select", "select nodeType1.*, nodeType3\nwhere nodeType2 property myProperty == 3;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select23() throws Exception {
        // test input: "select *\nwhere myType property myProperty == \"TesteValue\"\n;"
        Object retval = execTreeParser("select", "select", "select *\nwhere myType property myProperty == \"TesteValue\"\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select24() throws Exception {
        // test input: "select org.test.JavaType\nwhere \n\torg.test.JavaType \n\t(\tproperty myProperty == \"TesteValue\" &&\n\t\tproperty myOtherProperty == @myBooleanVar ) ||\n\t\tlink org.test.PackageDeclaresType (b) == 1\n;"
        Object retval = execTreeParser("select", "select", "select org.test.JavaType\nwhere \n\torg.test.JavaType \n\t(\tproperty myProperty == \"TesteValue\" &&\n\t\tproperty myOtherProperty == @myBooleanVar ) ||\n\t\tlink org.test.PackageDeclaresType (b) == 1\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select25() throws Exception {
        // test input: "select **\nwhere \n\torg.test.JavaType \n\t(\tproperty myProperty == \"TesteValue\" &&\n\t\tproperty myOtherProperty == @myBooleanVar ) ||\n\t\tlink org.test.PackageDeclaresType (b) == 1\n\torg.test.JavaPackage \n\t(\tproperty myPropertyOfPackage == \"something\" &&\n\t\tproperty myOtherPropertyOfPackage == $someOtherValue ) ||\n\t\tlink org.test.PackageDeclaresType (a) > 10\n\n;"
        Object retval = execTreeParser("select", "select", "select **\nwhere \n\torg.test.JavaType \n\t(\tproperty myProperty == \"TesteValue\" &&\n\t\tproperty myOtherProperty == @myBooleanVar ) ||\n\t\tlink org.test.PackageDeclaresType (b) == 1\n\torg.test.JavaPackage \n\t(\tproperty myPropertyOfPackage == \"something\" &&\n\t\tproperty myOtherPropertyOfPackage == $someOtherValue ) ||\n\t\tlink org.test.PackageDeclaresType (a) > 10\n\n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select26() throws Exception {
        // test input: "select * by link org.test.PackageDeclaresType (a), org.test.TypeDeclaresMethod (b) \n;"
        Object retval = execTreeParser("select", "select", "select * by link org.test.PackageDeclaresType (a), org.test.TypeDeclaresMethod (b) \n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testSelect_walks_Select27() throws Exception {
        // test input: "select org.test.JavaType.*, org.test.JavaMethod.* \n\tby link org.test.PackageDeclaresType (a), org.test.TypeDeclaresMethod (a,b) \n;"
        Object retval = execTreeParser("select", "select", "select org.test.JavaType.*, org.test.JavaMethod.* \n\tby link org.test.PackageDeclaresType (a), org.test.TypeDeclaresMethod (a,b) \n;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "select", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit1() throws Exception {
        // test input: "select org.test.Package;\nselect * by link org.test.PackageDeclaresType (a);"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "select org.test.Package;\nselect * by link org.test.PackageDeclaresType (a);", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit2() throws Exception {
        // test input: "define target = org.test.Package;\nselect * by link org.test.PackageDeclaresType (a);"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "define target = org.test.Package;\nselect * by link org.test.PackageDeclaresType (a);", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit3() throws Exception {
        // test input: "define target = org.test.Package.*;\nselect * by link org.test.PackageDeclaresType (a);"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "define target = org.test.Package.*;\nselect * by link org.test.PackageDeclaresType (a);", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit4() throws Exception {
        // test input: "use collator level primary;\ndefine output = generalDiaram;\ndefine target = org.test.Package.*;\nselect * by link org.test.PackageDeclaresType (a);"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "use collator level primary;\ndefine output = generalDiaram;\ndefine target = org.test.Package.*;\nselect * by link org.test.PackageDeclaresType (a);", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit5() throws Exception {
        // test input: "use collator level primary;\ndefine output = generalDiaram;\ndefine target = org.test.Package.*;\nselect * by link org.test.PackageDeclaresType (a);"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "use collator level primary;\ndefine output = generalDiaram;\ndefine target = org.test.Package.*;\nselect * by link org.test.PackageDeclaresType (a);", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit6() throws Exception {
        // test input: "use collator level primary;\ndefine output = generalDiaram;\ndefine message $teste = \"enter with some data\";\ndefine message $teste2 = \"enter with some data\";\ndefine target = org.test.Package.*;\nselect * by link org.test.PackageDeclaresType (a);"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "use collator level primary;\ndefine output = generalDiaram;\ndefine message $teste = \"enter with some data\";\ndefine message $teste2 = \"enter with some data\";\ndefine target = org.test.Package.*;\nselect * by link org.test.PackageDeclaresType (a);", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit7() throws Exception {
        // test input: "use collator level primary;\ndefine output = generalDiaram;\ndefine message $teste = \"enter with some data\";\ndefine domain values $teste = \"value1\", \"value2\";\ndefine target = org.test.Package.*;\nselect * by link org.test.PackageDeclaresType (a);"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "use collator level primary;\ndefine output = generalDiaram;\ndefine message $teste = \"enter with some data\";\ndefine domain values $teste = \"value1\", \"value2\";\ndefine target = org.test.Package.*;\nselect * by link org.test.PackageDeclaresType (a);", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit8() throws Exception {
        // test input: "select org.test.JavaType.* ;"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "select org.test.JavaType.* ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit9() throws Exception {
        // test input: "select org.test.JavaType.*\nwhere\n\torg.test.JavaType.*\n\t\tproperty myPropertyName == \"someValue\" && \n\t\tlink org.test.JavaTypeDeclares (b) > 2\n\t ;"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "select org.test.JavaType.*\nwhere\n\torg.test.JavaType.*\n\t\tproperty myPropertyName == \"someValue\" && \n\t\tlink org.test.JavaTypeDeclares (b) > 2\n\t ;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit10() throws Exception {
        // test input: "select org.test.JavaType.*\nwhere\n\torg.test.JavaType.*\n\t\tproperty myPropertyName == \"someValue\" && \n\t\tlink org.test.JavaTypeDeclares (b) > 2\norder by\n\torg.test.JavaType.*\n\t\tproperty myPropertyName;"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "select org.test.JavaType.*\nwhere\n\torg.test.JavaType.*\n\t\tproperty myPropertyName == \"someValue\" && \n\t\tlink org.test.JavaTypeDeclares (b) > 2\norder by\n\torg.test.JavaType.*\n\t\tproperty myPropertyName;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit11() throws Exception {
        // test input: "select org.test.JavaType.*\n\twhere\n\t\torg.test.JavaType.*\n\t\t\tproperty myPropertyName == \"someValue\" && \n\t\t\tlink org.test.JavaTypeDeclares (b) > 2\norder by\n\torg.test.JavaTypeClass, org.test.JavaTypeInterface;"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "select org.test.JavaType.*\n\twhere\n\t\torg.test.JavaType.*\n\t\t\tproperty myPropertyName == \"someValue\" && \n\t\t\tlink org.test.JavaTypeDeclares (b) > 2\norder by\n\torg.test.JavaTypeClass, org.test.JavaTypeInterface;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit12() throws Exception {
        // test input: "select org.test.JavaType.*\n\twhere\n\t\torg.test.JavaType.*\n\t\t\tproperty myPropertyName == \"someValue\" && \n\t\t\tlink org.test.JavaTypeDeclares (b) > 2\norder by\n\torg.test.JavaTypeClass, org.test.JavaTypeInterface\nuse collator level secondary;"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "select org.test.JavaType.*\n\twhere\n\t\torg.test.JavaType.*\n\t\t\tproperty myPropertyName == \"someValue\" && \n\t\t\tlink org.test.JavaTypeDeclares (b) > 2\norder by\n\torg.test.JavaTypeClass, org.test.JavaTypeInterface\nuse collator level secondary;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit13() throws Exception {
        // test input: "select org.test.JavaType.*\n\twhere\n\t\torg.test.JavaType.*\n\t\t\tproperty myPropertyName == \"someValue\" && \n\t\t\tlink org.test.JavaTypeDeclares (b) > 2\norder by\n\torg.test.JavaTypeClass, org.test.JavaTypeInterface\nuse collator level secondary;"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "select org.test.JavaType.*\n\twhere\n\t\torg.test.JavaType.*\n\t\t\tproperty myPropertyName == \"someValue\" && \n\t\t\tlink org.test.JavaTypeDeclares (b) > 2\norder by\n\torg.test.JavaTypeClass, org.test.JavaTypeInterface\nuse collator level secondary;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit14() throws Exception {
        // test input: "select org.test.JavaType.*\n\twhere\n\t\torg.test.JavaType.*\n\t\t\tproperty myPropertyName == \"someValue\" && \n\t\t\tlink org.test.JavaTypeDeclares (b) > 2\nlimit 10\norder by\n\torg.test.JavaTypeClass, org.test.JavaTypeInterface\nuse collator level secondary;"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "select org.test.JavaType.*\n\twhere\n\t\torg.test.JavaType.*\n\t\t\tproperty myPropertyName == \"someValue\" && \n\t\t\tlink org.test.JavaTypeDeclares (b) > 2\nlimit 10\norder by\n\torg.test.JavaTypeClass, org.test.JavaTypeInterface\nuse collator level secondary;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit15() throws Exception {
        // test input: "select org.test.JavaType.*\n\twhere\n\t\torg.test.JavaType.*\n\t\t\tproperty myPropertyName == \"someValue\" && \n\t\t\tlink org.test.JavaTypeDeclares (b) > 2\nlimit 10 offset 11\norder by\n\torg.test.JavaTypeClass, org.test.JavaTypeInterface\nuse collator level secondary;"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "select org.test.JavaType.*\n\twhere\n\t\torg.test.JavaType.*\n\t\t\tproperty myPropertyName == \"someValue\" && \n\t\t\tlink org.test.JavaTypeDeclares (b) > 2\nlimit 10 offset 11\norder by\n\torg.test.JavaTypeClass, org.test.JavaTypeInterface\nuse collator level secondary;", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit16() throws Exception {
        // test input: "select org.test.JavaType.*\n\twhere\n\t\torg.test.JavaType.*\n\t\t\tproperty myPropertyName == \"someValue\" && \n\t\t\tlink org.test.JavaTypeDeclares (b) > 2\norder by\n\torg.test.JavaTypeClass, org.test.JavaTypeInterface;\nselect *;\t"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "select org.test.JavaType.*\n\twhere\n\t\torg.test.JavaType.*\n\t\t\tproperty myPropertyName == \"someValue\" && \n\t\t\tlink org.test.JavaTypeDeclares (b) > 2\norder by\n\torg.test.JavaTypeClass, org.test.JavaTypeInterface;\nselect *;\t", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit17() throws Exception {
        // test input: "use collator level primary;\ndefine output = generalDiaram;\ndefine message $teste = \"enter with some data\";\ndefine domain values $teste = \"value1\", \"value2\";\ndefine target = select *;\nselect * by link org.test.PackageDeclaresType (a);"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "use collator level primary;\ndefine output = generalDiaram;\ndefine message $teste = \"enter with some data\";\ndefine domain values $teste = \"value1\", \"value2\";\ndefine target = select *;\nselect * by link org.test.PackageDeclaresType (a);", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit18() throws Exception {
        // test input: "use collator level primary;\ndefine output = generalDiaram;\ndefine message $teste = \"enter with some data\";\ndefine domain values $teste = \"value1\", \"value2\";\ndefine target = select ** \n\t\twhere org.test.Something property myProperty == \"TesteValue\";\nselect * by link org.test.PackageDeclaresType (a);"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "use collator level primary;\ndefine output = generalDiaram;\ndefine message $teste = \"enter with some data\";\ndefine domain values $teste = \"value1\", \"value2\";\ndefine target = select ** \n\t\twhere org.test.Something property myProperty == \"TesteValue\";\nselect * by link org.test.PackageDeclaresType (a);", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit19() throws Exception {
        // test input: "use collator level primary;\ndefine output = generalDiaram;\ndefine message $teste = \"enter with some data\";\ndefine domain values $teste = \"value1\", \"value2\";\ndefine target = select ** \n\t\twhere org.test.Something property myProperty == \"TesteValue\" keep result;\nselect * by link org.test.PackageDeclaresType (a);"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "use collator level primary;\ndefine output = generalDiaram;\ndefine message $teste = \"enter with some data\";\ndefine domain values $teste = \"value1\", \"value2\";\ndefine target = select ** \n\t\twhere org.test.Something property myProperty == \"TesteValue\" keep result;\nselect * by link org.test.PackageDeclaresType (a);", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

    public void testCompilationUnit_walks_CompilationUnit20() throws Exception {
        // test input: "select org.openspotlight.graph.query.JavaInterface\n\twhere \n\t\torg.openspotlight.graph.query.JavaInterface\n\t\t\tproperty caption *... \"java.util.Collection\";\n\nselect org.openspotlight.graph.query.JavaTypeMethod \nby link \n\torg.openspotlight.graph.query.TypeContainsMethod (a);"
        Object retval = execTreeParser("compilationUnit", "compilationUnit", "select org.openspotlight.graph.query.JavaInterface\n\twhere \n\t\torg.openspotlight.graph.query.JavaInterface\n\t\t\tproperty caption *... \"java.util.Collection\";\n\nselect org.openspotlight.graph.query.JavaTypeMethod \nby link \n\torg.openspotlight.graph.query.TypeContainsMethod (a);", false);
        Object actual = examineExecResult(org.antlr.gunit.gUnitParser.OK, retval);
        Object expecting = "OK";

        assertEquals("testing rule " + "compilationUnit", expecting, actual);
    }

}
