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
package org.openspotlight.bundle.dap.language.java;

import org.junit.Test;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeInterface;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.bundle.dap.language.java.resolver.JavaGraphNodeSupport;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphFactoryImpl;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

public class ExampleGraphImport {

    @Test
    public void shouldImportSomeData() throws Exception {

        final SecurityFactory securityFactory = AbstractFactory.getDefaultInstance(SecurityFactory.class);
        final User simpleUser = securityFactory.createUser("testUser");
        final AuthenticatedUser user = securityFactory.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser, "password");

        final SLGraphFactory factory = new SLGraphFactoryImpl();
        final SLGraph graph = factory.createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        final SLGraphSession session = graph.openSession(user);
        final SLNode currentContextRootNode = session.createContext("Dynamo-1.0.1").getRootNode();
        final SLNode abstractContextRootNode = session.createContext(Constants.ABSTRACT_CONTEXT).getRootNode();
        final JavaGraphNodeSupport helper = new JavaGraphNodeSupport(session, currentContextRootNode, abstractContextRootNode);
        JavaType newType;
        JavaMethod method;

        newType = helper.addTypeOnAbstractContext(JavaTypeInterface.class, "dynamo.file.util",
                                                  "FileDescriptorManager$FileSavingListener");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.file.util", "FileDescriptorManager");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.file.util", "FileUtil");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.file.vo", "FileCollection");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.file.vo", "FileDescriptor");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.freemarker", "FreemarkerProcessor");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.main", "Main");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.runner", "FileHelper");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.runner", "RunningParameters");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.runner", "RunningParametersFactory");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.string", "StringTool");
        newType = helper.addTypeOnAbstractContext(JavaTypeInterface.class, "dynamo.file.util",
                                                  "FileDescriptorManager$FileSavingListener");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.file.util", "FileDescriptorManager");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.file.util", "FileUtil");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.file.vo", "FileCollection");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.file.vo", "FileDescriptor");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.freemarker", "FreemarkerProcessor");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.main", "Main");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.runner", "FileHelper");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.runner", "RunningParameters");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.runner", "RunningParametersFactory");
        newType = helper.addTypeOnAbstractContext(JavaTypeClass.class, "dynamo.string", "StringTool");
        newType = helper.addTypeOnCurrentContext(JavaTypeInterface.class, "dynamo.file.util",
                                                 "FileDescriptorManager$FileSavingListener", 1537);
        helper.addExtendsLinks("dynamo.file.util", "FileDescriptorManager$FileSavingListener", "java.lang", "Object");
        method = helper.createMethod(newType, "fileSaved(dynamo.file.vo.FileDescriptor)", "fileSaved", false, 1025);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.file.util", "FileDescriptorManager", 33);
        helper.addExtendsLinks("dynamo.file.util", "FileDescriptorManager", "java.lang", "Object");
        method = helper.createMethod(newType, "FileDescriptorManager()", "FileDescriptorManager", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "createFromResult(java.lang.String)", "createFromResult", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.util", "List", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        method = helper.createMethod(newType, "saveToFile(dynamo.runner.RunningParameters, dynamo.file.vo.FileDescriptor[])",
                                     "saveToFile", false, 129);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.file.util", "FileUtil", 33);
        helper.addExtendsLinks("dynamo.file.util", "FileUtil", "java.lang", "Object");
        method = helper.createMethod(newType, "FileUtil()", "FileUtil", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "readFromFile(java.io.File)", "readFromFile", false, 9);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.file.vo", "FileCollection", 33);
        helper.addExtendsLinks("dynamo.file.vo", "FileCollection", "java.lang", "Object");
        method = helper.createMethod(newType, "FileCollection()", "FileCollection", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getFiles()", "getFiles", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.util", "List", false, 0);
        method = helper.createMethod(newType, "setFiles(java.util.List)", "setFiles", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.file.vo", "FileDescriptor", 33);
        helper.addExtendsLinks("dynamo.file.vo", "FileDescriptor", "java.lang", "Object");
        method = helper.createMethod(newType, "FileDescriptor()", "FileDescriptor", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getName()", "getName", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setName(java.lang.String)", "setName", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getLocation()", "getLocation", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setLocation(java.lang.String)", "setLocation", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getContent()", "getContent", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setContent(java.lang.String)", "setContent", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.freemarker", "FreemarkerProcessor", 33);
        helper.addExtendsLinks("dynamo.freemarker", "FreemarkerProcessor", "java.lang", "Object");
        method = helper.createMethod(newType, "FreemarkerProcessor(java.lang.String)", "FreemarkerProcessor", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        method = helper.createMethod(newType, "processTemplate(java.lang.String, java.lang.String)", "processTemplate", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.main", "Main", 33);
        helper.addExtendsLinks("dynamo.main", "Main", "java.lang", "Object");
        method = helper.createMethod(newType, "Main()", "Main", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "main(java.lang.String[])", "main", false, 137);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.runner", "FileHelper", 33);
        helper.addExtendsLinks("dynamo.runner", "FileHelper", "java.lang", "Object");
        method = helper.createMethod(newType, "FileHelper()", "FileHelper", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "generate(dynamo.runner.RunningParameters)", "generate", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.runner", "RunningParameters", 33);
        helper.addExtendsLinks("dynamo.runner", "RunningParameters", "java.lang", "Object");
        method = helper.createMethod(newType, "getListeners()", "getListeners", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.util", "List", false, 0);
        method = helper.createMethod(newType, "setListeners(java.util.List)", "setListeners", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "addListener(dynamo.file.util.FileDescriptorManager$FileSavingListener)",
                                     "addListener", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "boolean", false, 0);
        method = helper.createMethod(newType, "removeListener(dynamo.file.util.FileDescriptorManager$FileSavingListener)",
                                     "removeListener", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "boolean", false, 0);
        method = helper.createMethod(newType, "RunningParameters()", "RunningParameters", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getOutputDir()", "getOutputDir", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setOutputDir(java.lang.String)", "setOutputDir", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getInputTemplates()", "getInputTemplates", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "java.lang", "String", true, 1);
        method = helper.createMethod(newType, "setInputTemplates(java.lang.String[])", "setInputTemplates", false, 129);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getInputXmls()", "getInputXmls", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "java.lang", "String", true, 1);
        method = helper.createMethod(newType, "setInputXmls(java.lang.String[])", "setInputXmls", false, 129);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getTemplatePath()", "getTemplatePath", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setTemplatePath(java.lang.String)", "setTemplatePath", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.runner", "RunningParametersFactory", 33);
        helper.addExtendsLinks("dynamo.runner", "RunningParametersFactory", "java.lang", "Object");
        helper.createField(newType, JavaType.class, "java.lang", "String", "PROP_OUTPUT_DIR", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "PROP_INPUT_TEMPLATES", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "PROP_INPUT_XMLS", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "PROP_TEMPLATE_PATH", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "ARG_TEMPLATE_PATH", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "ARG_OUTPUT_DIR", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "ARG_INPUT_TEMPLATES", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "ARG_INPUT_XMLS", 25, false, 0);
        method = helper.createMethod(newType, "RunningParametersFactory()", "RunningParametersFactory", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "createFromProperties(java.util.Properties)", "createFromProperties", false, 9);
        helper.createMethodReturnType(method, JavaType.class, "dynamo.runner", "RunningParameters", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        method = helper.createMethod(newType, "createFromArgs(java.lang.String[])", "createFromArgs", false, 137);
        helper.createMethodReturnType(method, JavaType.class, "dynamo.runner", "RunningParameters", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.string", "StringTool", 33);
        helper.addExtendsLinks("dynamo.string", "StringTool", "java.lang", "Object");
        method = helper.createMethod(newType, "StringTool()", "StringTool", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "lowerFirst(java.lang.String)", "lowerFirst", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "upperFirst(java.lang.String)", "upperFirst", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "camelCase(java.lang.String)", "camelCase", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "camelToLowerUnderlined(java.lang.String)", "camelToLowerUnderlined", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "camelToUpperUnderlined(java.lang.String)", "camelToUpperUnderlined", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "camelToUnderlined(java.lang.String)", "camelToUnderlined", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "getter(java.lang.String, java.lang.String)", "getter", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setter(java.lang.String, java.lang.String)", "setter", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "property(java.lang.String, java.lang.String)", "property", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "get(java.lang.String)", "get", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "set(java.lang.String, java.lang.String)", "set", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "getAttr(java.lang.String)", "getAttr", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setAttr(java.lang.String, java.lang.String)", "setAttr", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeInterface.class, "dynamo.file.util",
                                                 "FileDescriptorManager$FileSavingListener", 1537);
        helper.addExtendsLinks("dynamo.file.util", "FileDescriptorManager$FileSavingListener", "java.lang", "Object");
        method = helper.createMethod(newType, "fileSaved(dynamo.file.vo.FileDescriptor)", "fileSaved", false, 1025);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.file.util", "FileDescriptorManager", 33);
        helper.addExtendsLinks("dynamo.file.util", "FileDescriptorManager", "java.lang", "Object");
        method = helper.createMethod(newType, "FileDescriptorManager()", "FileDescriptorManager", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "createFromResult(java.lang.String)", "createFromResult", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.util", "List", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        method = helper.createMethod(newType, "saveToFile(dynamo.runner.RunningParameters, dynamo.file.vo.FileDescriptor[])",
                                     "saveToFile", false, 129);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.file.util", "FileUtil", 33);
        helper.addExtendsLinks("dynamo.file.util", "FileUtil", "java.lang", "Object");
        method = helper.createMethod(newType, "FileUtil()", "FileUtil", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "readFromFile(java.io.File)", "readFromFile", false, 9);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.file.vo", "FileCollection", 33);
        helper.addExtendsLinks("dynamo.file.vo", "FileCollection", "java.lang", "Object");
        method = helper.createMethod(newType, "FileCollection()", "FileCollection", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getFiles()", "getFiles", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.util", "List", false, 0);
        method = helper.createMethod(newType, "setFiles(java.util.List)", "setFiles", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.file.vo", "FileDescriptor", 33);
        helper.addExtendsLinks("dynamo.file.vo", "FileDescriptor", "java.lang", "Object");
        method = helper.createMethod(newType, "FileDescriptor()", "FileDescriptor", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getName()", "getName", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setName(java.lang.String)", "setName", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getLocation()", "getLocation", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setLocation(java.lang.String)", "setLocation", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getContent()", "getContent", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setContent(java.lang.String)", "setContent", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.freemarker", "FreemarkerProcessor", 33);
        helper.addExtendsLinks("dynamo.freemarker", "FreemarkerProcessor", "java.lang", "Object");
        method = helper.createMethod(newType, "FreemarkerProcessor(java.lang.String)", "FreemarkerProcessor", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        method = helper.createMethod(newType, "processTemplate(java.lang.String, java.lang.String)", "processTemplate", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.main", "Main", 33);
        helper.addExtendsLinks("dynamo.main", "Main", "java.lang", "Object");
        method = helper.createMethod(newType, "Main()", "Main", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "main(java.lang.String[])", "main", false, 137);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.runner", "FileHelper", 33);
        helper.addExtendsLinks("dynamo.runner", "FileHelper", "java.lang", "Object");
        method = helper.createMethod(newType, "FileHelper()", "FileHelper", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "generate(dynamo.runner.RunningParameters)", "generate", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.runner", "RunningParameters", 33);
        helper.addExtendsLinks("dynamo.runner", "RunningParameters", "java.lang", "Object");
        method = helper.createMethod(newType, "getListeners()", "getListeners", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.util", "List", false, 0);
        method = helper.createMethod(newType, "setListeners(java.util.List)", "setListeners", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "addListener(dynamo.file.util.FileDescriptorManager$FileSavingListener)",
                                     "addListener", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "boolean", false, 0);
        method = helper.createMethod(newType, "removeListener(dynamo.file.util.FileDescriptorManager$FileSavingListener)",
                                     "removeListener", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "boolean", false, 0);
        method = helper.createMethod(newType, "RunningParameters()", "RunningParameters", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getOutputDir()", "getOutputDir", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setOutputDir(java.lang.String)", "setOutputDir", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getInputTemplates()", "getInputTemplates", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "java.lang", "String", true, 1);
        method = helper.createMethod(newType, "setInputTemplates(java.lang.String[])", "setInputTemplates", false, 129);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getInputXmls()", "getInputXmls", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "java.lang", "String", true, 1);
        method = helper.createMethod(newType, "setInputXmls(java.lang.String[])", "setInputXmls", false, 129);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "getTemplatePath()", "getTemplatePath", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setTemplatePath(java.lang.String)", "setTemplatePath", false, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.runner", "RunningParametersFactory", 33);
        helper.addExtendsLinks("dynamo.runner", "RunningParametersFactory", "java.lang", "Object");
        helper.createField(newType, JavaType.class, "java.lang", "String", "PROP_OUTPUT_DIR", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "PROP_INPUT_TEMPLATES", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "PROP_INPUT_XMLS", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "PROP_TEMPLATE_PATH", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "ARG_TEMPLATE_PATH", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "ARG_OUTPUT_DIR", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "ARG_INPUT_TEMPLATES", 25, false, 0);
        helper.createField(newType, JavaType.class, "java.lang", "String", "ARG_INPUT_XMLS", 25, false, 0);
        method = helper.createMethod(newType, "RunningParametersFactory()", "RunningParametersFactory", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "createFromProperties(java.util.Properties)", "createFromProperties", false, 9);
        helper.createMethodReturnType(method, JavaType.class, "dynamo.runner", "RunningParameters", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        method = helper.createMethod(newType, "createFromArgs(java.lang.String[])", "createFromArgs", false, 137);
        helper.createMethodReturnType(method, JavaType.class, "dynamo.runner", "RunningParameters", false, 0);
        helper.addThrowsOnMethod(method, "java.lang", "Exception");
        // #########################################################
        newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, "dynamo.string", "StringTool", 33);
        helper.addExtendsLinks("dynamo.string", "StringTool", "java.lang", "Object");
        method = helper.createMethod(newType, "StringTool()", "StringTool", true, 1);
        helper.createMethodReturnType(method, JavaTypePrimitive.class, "", "void", false, 0);
        method = helper.createMethod(newType, "lowerFirst(java.lang.String)", "lowerFirst", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "upperFirst(java.lang.String)", "upperFirst", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "camelCase(java.lang.String)", "camelCase", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "camelToLowerUnderlined(java.lang.String)", "camelToLowerUnderlined", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "camelToUpperUnderlined(java.lang.String)", "camelToUpperUnderlined", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "camelToUnderlined(java.lang.String)", "camelToUnderlined", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "getter(java.lang.String, java.lang.String)", "getter", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setter(java.lang.String, java.lang.String)", "setter", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "property(java.lang.String, java.lang.String)", "property", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "get(java.lang.String)", "get", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "set(java.lang.String, java.lang.String)", "set", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "getAttr(java.lang.String)", "getAttr", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        method = helper.createMethod(newType, "setAttr(java.lang.String, java.lang.String)", "setAttr", false, 1);
        helper.createMethodReturnType(method, JavaType.class, "java.lang", "String", false, 0);
        // #########################################################
        session.save();
        session.close();
        graph.shutdown();

    }
}
