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
package org.openspotlight.slql.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.openspotlight.common.util.ClassLoaderUtil;
import org.openspotlight.common.util.ClassPathResource;
import org.openspotlight.common.util.Sha1;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;

public class SLQLQueryBuilder {

    public SLQLQuery build( final String slqlText ) throws SLQueryLanguageParserException {
        SLQLQueryInfo queryInfo = buildQueryInfo(slqlText);

        SLQLQuery target = null;
        if (queryInfo.hasTarget()) {
            target = buildTargetQuery(queryInfo.getTargetUniqueId(), queryInfo.getDefineTargetContent());
        }

        Set<SLQLVariable> variables = buildVariableCollection(queryInfo);

        return buildQuery(queryInfo.getId(), variables, queryInfo.getOutputModelName(), target, queryInfo.getContent());
    }

    private SLQLQuery buildQuery( final String id,
                                  final Set<SLQLVariable> variables,
                                  final String outputModelName,
                                  final SLQLQuery target,
                                  final String executeContent ) throws SLQueryLanguageParserException {
        try {
            String className = getClassName(id);

            if (!ClassLoaderUtil.existsClass(className)) {
                createNewQueryClass(className, executeContent);
            }

            @SuppressWarnings( "unchecked" )
            Class<AbstractSLQLQuery> queryResult = (Class<AbstractSLQLQuery>)ClassLoaderUtil.getClass(className);

            Constructor<AbstractSLQLQuery> constr;
            constr = queryResult.getConstructor(String.class, Set.class, String.class,
                                                boolean.class, SLQLQuery.class);
            return constr.newInstance(id, variables, outputModelName, false, target);

        } catch (Exception e) {
            throw new SLQueryLanguageParserException(e);
        }
    }

    private SLQLQuery buildTargetQuery( final String targetUniqueId,
                                        final String defineTargetContent ) throws SLQueryLanguageParserException {
        try {
            String className = getClassName(targetUniqueId);

            if (!ClassLoaderUtil.existsClass(className)) {
                createNewQueryClass(className, defineTargetContent);
            }

            @SuppressWarnings( "unchecked" )
            Class<AbstractSLQLQuery> queryResult = (Class<AbstractSLQLQuery>)ClassLoaderUtil.getClass(className);

            Constructor<AbstractSLQLQuery> constr;
            constr = queryResult.getConstructor(String.class, Set.class, String.class,
                                                boolean.class, SLQLQuery.class);
            return constr.newInstance(targetUniqueId, null, null, true, null);

        } catch (Exception e) {
            throw new SLQueryLanguageParserException(e);
        }
    }

    private String getClassName( String id ) {
        return "org.openspotlight.slql.parser.SLQLQuery$A" + id;
    }

    private enum SLQLVariableDataType {
        INTEGER,
        DECIMAL,
        STRING,
        BOOLEAN
    }

    private Set<SLQLVariable> buildVariableCollection( SLQLQueryInfo queryInfo ) {
        Set<SLQLVariable> result = new HashSet<SLQLVariable>();

        Collection<SLQLVariable> tempBoolVars = getVariablesByDataType(SLQLVariableDataType.BOOLEAN, queryInfo.getBoolVariables(), queryInfo.getMessageVariables(), queryInfo.getDomainVariables());
        Collection<SLQLVariable> tempIntVars = getVariablesByDataType(SLQLVariableDataType.INTEGER, queryInfo.getIntVariables(), queryInfo.getMessageVariables(), queryInfo.getDomainVariables());
        Collection<SLQLVariable> tempDecVars = getVariablesByDataType(SLQLVariableDataType.DECIMAL, queryInfo.getDecVariables(), queryInfo.getMessageVariables(), queryInfo.getDomainVariables());
        Collection<SLQLVariable> tempStringVars = getVariablesByDataType(SLQLVariableDataType.STRING, queryInfo.getStringVariables(), queryInfo.getMessageVariables(), queryInfo.getDomainVariables());

        result.addAll(tempBoolVars);
        result.addAll(tempIntVars);
        result.addAll(tempDecVars);
        result.addAll(tempStringVars);

        return result;
    }

    private Collection<SLQLVariable> getVariablesByDataType( final SLQLVariableDataType dataType,
                                                             final Collection<String> variables,
                                                             final Map<String, String> messageVariables,
                                                             final Map<String, Set<Object>> domainVariables ) {
        Set<SLQLVariable> result = new HashSet<SLQLVariable>(variables.size());
        for (String activeVariableName : variables) {
            SLQLVariable variable = null;
            switch (dataType) {
                case INTEGER:
                    variable = new SLQLVariableInteger(activeVariableName);
                    break;
                case DECIMAL:
                    variable = new SLQLVariableFloat(activeVariableName);
                    break;
                case STRING:
                    variable = new SLQLVariableString(activeVariableName);
                    break;
                case BOOLEAN:
                    variable = new SLQLVariableBoolean(activeVariableName);
                    break;
            }

            if (messageVariables.containsKey(activeVariableName)) {
                variable.setDisplayMessage(messageVariables.get(activeVariableName));
            }
            if (dataType != SLQLVariableDataType.BOOLEAN && domainVariables.containsKey(activeVariableName)) {
                variable.addAllDomainValue(domainVariables.get(activeVariableName));
            }
            result.add(variable);
        }
        return result;
    }

    private SLQLQueryInfo buildQueryInfo( final String slqlText ) throws SLQueryLanguageParserException {
        try {
            InputStream stream = ClassPathResource.getResourceFromClassPath("org/openspotlight/slql/parser/SLQLTemplate.stg");
            Reader reader = new InputStreamReader(stream);
            StringTemplateGroup templates = new StringTemplateGroup(reader);
            reader.close();
            ANTLRStringStream inputStream = new ANTLRStringStream(slqlText);
            SLQLLexer lex = new SLQLLexer(inputStream);
            CommonTokenStream tokens = new CommonTokenStream(lex);

            SLQLParser parser = new SLQLParser(tokens);
            parser.setIsTesting(false);
            if (parser.hasErrors()) {
                throw parser.getErrors().get(0);
            }
            CommonTree result = (CommonTree)parser.compilationUnit().tree;

            String uniqueId = Sha1.getSha1SignatureEncodedAsHexa(result.toStringTree().toLowerCase());

            String targetUniqueId = null;
            if (parser.getDefineTargetTreeResult() != null) {
                targetUniqueId = Sha1.getSha1SignatureEncodedAsHexa(parser.getDefineTargetTreeResult());
            }

            CommonTreeNodeStream treeNodes = new CommonTreeNodeStream(result);

            SLQLWalker walker = new SLQLWalker(treeNodes);
            walker.setTemplateLib(templates);

            SLQLQueryInfo queryInfo = walker.compilationUnit().queryInfoReturn;
            queryInfo.setId(uniqueId);
            queryInfo.setTargetUniqueId(targetUniqueId);

            return queryInfo;
        } catch (Exception e) {
            throw new SLQueryLanguageParserException(e);
        }
    }

    protected void createNewQueryClass( String className,
                                        String executeContent ) throws SLQueryLanguageParserException {
        try {
            ClassPool pool = ClassPool.getDefault();

            CtClass superClass = pool.get(AbstractSLQLQuery.class.getName());
            CtClass clas = pool.makeClass(className, superClass);

            CtClass[] CONSTRUCTOR_ARGS = {pool.get(String.class.getName()), pool.get(Set.class.getName()), pool.get(String.class.getName()), CtClass.booleanType, pool.get(SLQLQuery.class.getName())};
            CtClass[] NO_ARGS = {};

            CtConstructor newConstructor = CtNewConstructor.make(CONSTRUCTOR_ARGS, NO_ARGS, clas);
            clas.addConstructor(newConstructor);

            CtClass[] EXECUTE_ARGS = {pool.get(SLGraphSession.class.getName()), pool.get(Map.class.getName()), pool.get(Collection.class.getName())};
            CtClass[] EXECUTE_THROWS = {pool.get(SLGraphSessionException.class.getName())};

            CtMethod newMethod = CtNewMethod.make(pool.get(Collection.class.getName()), "execute", EXECUTE_ARGS, EXECUTE_THROWS, executeContent, clas);
            clas.addMethod(newMethod);

            clas.toClass(SLQLQueryBuilder.class.getClassLoader(), SLQLQueryBuilder.class.getProtectionDomain());
        } catch (Exception e) {
            throw new SLQueryLanguageParserException(e);
        }
    }
}
