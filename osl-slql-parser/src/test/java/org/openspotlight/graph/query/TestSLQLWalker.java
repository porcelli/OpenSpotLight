package org.openspotlight.graph.query;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.ClassPathResource;
import org.openspotlight.slql.parser.SLQLLexer;
import org.openspotlight.slql.parser.SLQLParser;
import org.openspotlight.slql.parser.SLQLQueryInfo;
import org.openspotlight.slql.parser.SLQLWalker;
import org.openspotlight.slql.parser.SLQueryLanguageParserException;

public class TestSLQLWalker {

    @Test
    public void testSelectStar() throws SLException, IOException {
        String select = "select *;";
        SLQLQueryInfo result = execute(select);

        Assert.assertEquals("query.select().allTypes().selectEnd();", result.getContent());
    }

    @Test
    public void testSelectType() throws SLException, IOException {
        String select = "select org.test.Name.Something;";
        SLQLQueryInfo result = execute(select);

        Assert.assertEquals("query.select().allTypes().selectEnd();", result.getContent());
    }

    @Test
    public void testSelectByLink() throws SLException, IOException {
        String select = "select org.type.Packages; select * by link org.test.PackageDeclaresType (a), org.test.TypeDeclaresMethod (b);";
        SLQLQueryInfo result = execute(select);

        Assert.assertEquals("query.select().allTypes().selectEnd();", result.getContent());
    }

    @Test
    public void testSelectWhere() throws SLException, IOException {
        String select = "select ** where myType property myProperty == \"TesteValue\";";
        SLQLQueryInfo result = execute(select);

        Assert.assertEquals("query.select().allTypes().selectEnd();", result.getContent());
    }

    public SLQLQueryInfo execute( String input ) throws SLQueryLanguageParserException {
        try {
            InputStream stream = ClassPathResource.getResourceFromClassPath("org/openspotlight/slql/parser/SLQLTemplate.stg");
            Reader reader = new InputStreamReader(stream);
            StringTemplateGroup templates = new StringTemplateGroup(reader);
            reader.close();
            ANTLRStringStream inputStream = new ANTLRStringStream(input);
            SLQLLexer lex = new SLQLLexer(inputStream);
            CommonTokenStream tokens = new CommonTokenStream(lex);

            SLQLParser parser = new SLQLParser(tokens);
            parser.setIsTesting(false);
            if (parser.hasErrors()) {
                throw parser.getErrors().get(0);
            }
            Tree result = (Tree)parser.compilationUnit().tree;
            CommonTreeNodeStream treeNodes = new CommonTreeNodeStream(result);

            SLQLWalker walker = new SLQLWalker(treeNodes);
            walker.setTemplateLib(templates);

            return walker.compilationUnit().queryInfoReturn;
        } catch (Exception e) {
            throw new SLQueryLanguageParserException(e);
        }
    }

}
