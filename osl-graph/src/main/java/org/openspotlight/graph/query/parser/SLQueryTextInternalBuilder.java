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
package org.openspotlight.graph.query.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQLVariable;
import org.openspotlight.graph.query.SLQueryTextInternal;

public class SLQueryTextInternalBuilder {

    private static CtClass[] CONSTRUCTOR_ARGS;
    private static CtClass[] CONSTRUCTOR_THROWS;
    private static CtClass[] EXECUTE_ARGS;
    private static CtClass[] EXECUTE_THROWS;
    private static CtClass   EXECUTE_RETURN_TYPE;

    public SLQueryTextInternal build( final String slqlText ) throws SLInvalidQuerySyntaxException {
        SLQueryTextInternalInfo queryInfo = buildQueryInfo(slqlText);

        SLQueryTextInternal target = null;
        if (queryInfo.hasTarget()) {
            target = buildTargetQuery(queryInfo.getTargetUniqueId(), queryInfo.getDefineTargetContent());
        }

        Set<SLQLVariable> variables = buildVariableCollection(queryInfo);

        return buildQuery(queryInfo.getId(), variables, queryInfo.getOutputModelName(), target, queryInfo.getContent());
    }

    private SLQueryTextInternal buildQuery( final String id,
                                  final Set<SLQLVariable> variables,
                                  final String outputModelName,
                                  final SLQueryTextInternal target,
                                  final String executeContent ) throws SLInvalidQuerySyntaxException {
        try {
            String className = getClassName(id);

            if (!ClassLoaderUtil.existsClass(className)) {
                createNewQueryClass(className, executeContent);
            }

            @SuppressWarnings( "unchecked" )
            Class<AbstractSLQueryTextInternal> queryResult = (Class<AbstractSLQueryTextInternal>)ClassLoaderUtil.getClass(className);

            Constructor<AbstractSLQueryTextInternal> constr;
            constr = queryResult.getConstructor(String.class, Set.class, String.class,
                                                SLQueryTextInternal.class);
            return constr.newInstance(id, variables, outputModelName, target);

        } catch (Exception e) {
            throw new SLInvalidQuerySyntaxException(e);
        }
    }

    private SLQueryTextInternal buildTargetQuery( final String targetUniqueId,
                                        final String defineTargetContent ) throws SLInvalidQuerySyntaxException {
        try {
            String className = getClassName(targetUniqueId);

            if (!ClassLoaderUtil.existsClass(className)) {
                createNewQueryClass(className, defineTargetContent);
            }

            @SuppressWarnings( "unchecked" )
            Class<AbstractSLQueryTextInternal> queryResult = (Class<AbstractSLQueryTextInternal>)ClassLoaderUtil.getClass(className);

            Constructor<AbstractSLQueryTextInternal> constr;
            constr = queryResult.getConstructor(String.class, Set.class, String.class,
                                                SLQueryTextInternal.class);
            return constr.newInstance(targetUniqueId, null, null, null);

        } catch (Exception e) {
            throw new SLInvalidQuerySyntaxException(e);
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

    private Set<SLQLVariable> buildVariableCollection( SLQueryTextInternalInfo queryInfo ) {
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

    private SLQueryTextInternalInfo buildQueryInfo( final String slqlText ) throws SLInvalidQuerySyntaxException {
        try {
            InputStream stream = ClassPathResource.getResourceFromClassPath(getClass(), "SLQLTemplate.stg");
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

            SLQueryTextInternalInfo queryInfo = walker.compilationUnit().queryInfoReturn;
            queryInfo.setId(uniqueId);
            queryInfo.setTargetUniqueId(targetUniqueId);

            return queryInfo;
        } catch (Exception e) {
            throw new SLInvalidQuerySyntaxException(e);
        }
    }

    protected void createNewQueryClass( String className,
                                        String executeContent ) throws SLInvalidQuerySyntaxException {
        try {

            ClassPool pool = ClassPool.getDefault();
            CtClass superClass = pool.get(AbstractSLQueryTextInternal.class.getName());
            CtClass clas = pool.makeClass(className, superClass);

            if (CONSTRUCTOR_ARGS == null) {
                for (Constructor<?> constructor : AbstractSLQueryTextInternal.class.getConstructors()) {
                    if (constructor.getParameterTypes().length > 0) {
                        CONSTRUCTOR_ARGS = new CtClass[constructor.getParameterTypes().length];
                        CONSTRUCTOR_THROWS = new CtClass[constructor.getExceptionTypes().length];
                        for (int i = 0; i < constructor.getParameterTypes().length; i++) {
                            CONSTRUCTOR_ARGS[i] = pool.get(constructor.getParameterTypes()[i].getName());
                        }
                        for (int i = 0; i < constructor.getExceptionTypes().length; i++) {
                            CONSTRUCTOR_THROWS[i] = pool.get(constructor.getExceptionTypes()[i].getName());
                        }
                        break;
                    }
                }

                for (Method method : AbstractSLQueryTextInternal.class.getMethods()) {
                    if (method.getName().equals("execute")) {
                        EXECUTE_ARGS = new CtClass[method.getParameterTypes().length];
                        EXECUTE_THROWS = new CtClass[method.getExceptionTypes().length];
                        for (int i = 0; i < method.getParameterTypes().length; i++) {
                            EXECUTE_ARGS[i] = pool.get(method.getParameterTypes()[i].getName());
                        }
                        for (int i = 0; i < method.getExceptionTypes().length; i++) {
                            EXECUTE_THROWS[i] = pool.get(method.getExceptionTypes()[i].getName());
                        }
                        EXECUTE_RETURN_TYPE = pool.get(method.getReturnType().getName());
                        break;
                    }
                }
            }

            //            System.out.println("executeContent: " + executeContent);

            CtConstructor newConstructor = CtNewConstructor.make(CONSTRUCTOR_ARGS, CONSTRUCTOR_THROWS, clas);
            clas.addConstructor(newConstructor);

            CtMethod newMethod = CtNewMethod.make(EXECUTE_RETURN_TYPE, "execute", EXECUTE_ARGS, EXECUTE_THROWS, executeContent, clas);
            clas.addMethod(newMethod);

            clas.toClass(SLQueryTextInternalBuilder.class.getClassLoader(), SLQueryTextInternalBuilder.class.getProtectionDomain());
        } catch (Exception e) {
            throw new SLInvalidQuerySyntaxException(e);
        }
    }
}
