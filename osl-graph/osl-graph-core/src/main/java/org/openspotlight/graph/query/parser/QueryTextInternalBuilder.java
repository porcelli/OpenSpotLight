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
import java.io.Serializable;
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
import javassist.LoaderClassPath;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.openspotlight.common.util.ClassLoaderUtil;
import org.openspotlight.common.util.ClassPathResource;
import org.openspotlight.common.util.Sha1;
import org.openspotlight.graph.exception.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.InvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQLVariable;
import org.openspotlight.graph.query.SLQueryTextInternal;

/**
 * The Class SLQueryTextInternalBuilder. This class genarates, based on slql external dsl, a new instance SLQueryTextInternal.
 * 
 * @author porcelli
 */
public class QueryTextInternalBuilder {

    /**
     * The Enum SLQLVariableDataType.
     * 
     * @author porcelli
     */
    private enum SLQLVariableDataType {

        /** The INTEGER data type. */
        INTEGER,

        /** The DECIMAL data type. */
        DECIMAL,

        /** The STRING data type. */
        STRING,

        /** The BOOLEAN data type. */
        BOOLEAN
    }

    private CtClass[] CONSTRUCTOR_ARGS;
    private CtClass[] CONSTRUCTOR_THROWS;
    private CtClass[] EXECUTE_ARGS;
    private CtClass[] EXECUTE_THROWS;

    private CtClass   EXECUTE_RETURN_TYPE;

    /**
     * Builds the SLQueryTextInternal based on input
     * 
     * @param slqlText the slql text
     * @return the sL query text internal
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     */
    public SLQueryTextInternal build( final String slqlText ) throws SLInvalidQuerySyntaxException {
        final QueryTextInternalInfo queryInfo = buildQueryInfo(slqlText);

        SLQueryTextInternal target = null;
        if (queryInfo.hasTarget()) {
            target = buildTargetQuery(queryInfo.getTargetUniqueId(), queryInfo.getDefineTargetContent(),
                                      queryInfo.getStringsConstant());
        }

        final Set<SLQLVariable> variables = buildVariableCollection(queryInfo);

        return buildQuery(queryInfo.getId(), variables, queryInfo.getOutputModelName(), queryInfo.getStringsConstant(), target,
                          queryInfo.getContent());
    }

    /**
     * Builds the query.
     * 
     * @param id the id
     * @param variables the variables
     * @param outputModelName the output model name
     * @param stringConstants the string constants
     * @param target the target
     * @param executeContent the execute content
     * @return the sL query text internal
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     */
    private SLQueryTextInternal buildQuery( final String id,
                                            final Set<SLQLVariable> variables,
                                            final String outputModelName,
                                            final Map<Integer, String> stringConstants,
                                            final SLQueryTextInternal target,
                                            final String executeContent ) throws SLInvalidQuerySyntaxException {
        try {
            final String className = getClassName(id);

            if (!ClassLoaderUtil.existsClass(className)) {
                createNewQueryClass(className, executeContent);
            }

            @SuppressWarnings( "unchecked" )
            final Class<AbstractSLQueryTextInternal> queryResult = (Class<AbstractSLQueryTextInternal>)ClassLoaderUtil.getClass(className);

            Constructor<AbstractSLQueryTextInternal> constr;
            constr = queryResult.getConstructor(String.class, Set.class, String.class, SLQueryTextInternal.class, Map.class);
            return constr.newInstance(id, variables, outputModelName, target, stringConstants);

        } catch (final Exception e) {
            throw new InvalidQuerySyntaxException(e);
        }
    }

    private class CaseInsensitiveStringStream extends ANTLRStringStream {
        public CaseInsensitiveStringStream(
                                            String input ) {
            super(input);
        }

        public int LA( int i ) {
            int result = super.LA(i);
            if (result == 0) {
                return 0; // undefined
            }
            if (result == CharStream.EOF) {
                return CharStream.EOF;
            }
            return Character.toLowerCase(result);
        }

    }

    /**
     * Builds the query info.
     * 
     * @param slqlText the slql text
     * @return the sL query text internal info
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     */
    private QueryTextInternalInfo buildQueryInfo( final String slqlText ) throws SLInvalidQuerySyntaxException {
        try {
            final InputStream stream = ClassPathResource.getResourceFromClassPath(this.getClass(), "SLQLTemplate.stg");
            final Reader reader = new InputStreamReader(stream);
            final StringTemplateGroup templates = new StringTemplateGroup(reader);
            reader.close();
            final CaseInsensitiveStringStream inputStream = new CaseInsensitiveStringStream(slqlText);
            final SLQLLexer lex = new SLQLLexer(inputStream);
            final CommonTokenStream tokens = new CommonTokenStream(lex);

            final SLQLParser parser = new SLQLParser(tokens);
            parser.setIsTesting(false);
            if (parser.hasErrors()) {
                throw parser.getErrors().get(0);
            }
            final CommonTree result = (CommonTree)parser.compilationUnit().tree;

            final String uniqueId = Sha1.getSha1SignatureEncodedAsHexa(result.toStringTree().toLowerCase());

            String targetUniqueId = null;
            if (parser.getDefineTargetTreeResult() != null) {
                targetUniqueId = Sha1.getSha1SignatureEncodedAsHexa(parser.getDefineTargetTreeResult());
            }

            final CommonTreeNodeStream treeNodes = new CommonTreeNodeStream(result);

            final SLQLWalker walker = new SLQLWalker(treeNodes);
            walker.setTemplateLib(templates);

            final QueryTextInternalInfo queryInfo = walker.compilationUnit().queryInfoReturn;
            queryInfo.setId(uniqueId);
            queryInfo.setTargetUniqueId(targetUniqueId);

            return queryInfo;
        } catch (final Exception e) {
            throw new InvalidQuerySyntaxException(e);
        }
    }

    /**
     * Builds the target query.
     * 
     * @param targetUniqueId the target unique id
     * @param defineTargetContent the define target content
     * @param stringConstants the string constants
     * @return the sL query text internal
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     */
    private SLQueryTextInternal buildTargetQuery( final String targetUniqueId,
                                                  final String defineTargetContent,
                                                  final Map<Integer, String> stringConstants )
        throws SLInvalidQuerySyntaxException {
        try {
            final String className = getClassName(targetUniqueId);

            if (!ClassLoaderUtil.existsClass(className)) {
                createNewQueryClass(className, defineTargetContent);
            }

            @SuppressWarnings( "unchecked" )
            final Class<AbstractSLQueryTextInternal> queryResult = (Class<AbstractSLQueryTextInternal>)ClassLoaderUtil.getClass(className);

            Constructor<AbstractSLQueryTextInternal> constr;
            constr = queryResult.getConstructor(String.class, Set.class, String.class, SLQueryTextInternal.class, Map.class);
            return constr.newInstance(targetUniqueId, null, null, null, stringConstants);

        } catch (final Exception e) {
            throw new InvalidQuerySyntaxException(e);
        }
    }

    /**
     * Builds the variable collection.
     * 
     * @param queryInfo the query info
     * @return the set< slql variable>
     */
    private Set<SLQLVariable> buildVariableCollection( final QueryTextInternalInfo queryInfo ) {
        final Set<SLQLVariable> result = new HashSet<SLQLVariable>();

        final Collection<SLQLVariable> tempBoolVars = getVariablesByDataType(SLQLVariableDataType.BOOLEAN,
                                                                             queryInfo.getBoolVariables(),
                                                                             queryInfo.getMessageVariables(),
                                                                             queryInfo.getDomainVariables());
        final Collection<SLQLVariable> tempIntVars = getVariablesByDataType(SLQLVariableDataType.INTEGER,
                                                                            queryInfo.getIntVariables(),
                                                                            queryInfo.getMessageVariables(),
                                                                            queryInfo.getDomainVariables());
        final Collection<SLQLVariable> tempDecVars = getVariablesByDataType(SLQLVariableDataType.DECIMAL,
                                                                            queryInfo.getDecVariables(),
                                                                            queryInfo.getMessageVariables(),
                                                                            queryInfo.getDomainVariables());
        final Collection<SLQLVariable> tempStringVars = getVariablesByDataType(SLQLVariableDataType.STRING,
                                                                               queryInfo.getStringVariables(),
                                                                               queryInfo.getMessageVariables(),
                                                                               queryInfo.getDomainVariables());

        result.addAll(tempBoolVars);
        result.addAll(tempIntVars);
        result.addAll(tempDecVars);
        result.addAll(tempStringVars);

        return result;
    }

    /**
     * Creates the new query class.
     * 
     * @param className the class name
     * @param executeContent the execute content
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     */
    private void createNewQueryClass( final String className,
                                      final String executeContent ) throws SLInvalidQuerySyntaxException {
        try {

            final ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(new LoaderClassPath(AbstractSLQueryTextInternal.class.getClassLoader()));
            final CtClass superClass = pool.get(AbstractSLQueryTextInternal.class.getName());
            final CtClass clas = pool.makeClass(className, superClass);

            if (CONSTRUCTOR_ARGS == null) {
                for (final Constructor<?> constructor : AbstractSLQueryTextInternal.class.getConstructors()) {
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

                for (final Method method : AbstractSLQueryTextInternal.class.getMethods()) {
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

            final CtConstructor newConstructor = CtNewConstructor.make(CONSTRUCTOR_ARGS, CONSTRUCTOR_THROWS, clas);
            clas.addConstructor(newConstructor);

            final CtMethod newMethod = CtNewMethod.make(EXECUTE_RETURN_TYPE, "execute", EXECUTE_ARGS, EXECUTE_THROWS,
                                                        executeContent, clas);
            clas.addMethod(newMethod);

            clas.toClass(QueryTextInternalBuilder.class.getClassLoader(),
                         QueryTextInternalBuilder.class.getProtectionDomain());
        } catch (final Exception e) {
            throw new InvalidQuerySyntaxException(e);
        }
    }

    /**
     * Gets the class name.
     * 
     * @param id the id
     * @return the class name
     */
    private String getClassName( final String id ) {
        return "org.openspotlight.graph.query.SLQLQuery$A" + id;
    }

    /**
     * Gets the variables by data type.
     * 
     * @param dataType the data type
     * @param variables the variables
     * @param messageVariables the message variables
     * @param domainVariables the domain variables
     * @return the variables by data type
     */
    private Collection<SLQLVariable> getVariablesByDataType( final SLQLVariableDataType dataType,
                                                             final Collection<String> variables,
                                                             final Map<String, String> messageVariables,
                                                             final Map<String, Set<Serializable>> domainVariables ) {
        final Set<SLQLVariable> result = new HashSet<SLQLVariable>(variables.size());
        for (final String activeVariableName : variables) {
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
}
