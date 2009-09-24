<files>
    <file>
        <name>java-initial-data-for-${doc.TypeDefinitionSet.name}-${doc.TypeDefinitionSet.version}.bsh</name>
        <location>beanshell</location>
        <content><![CDATA[

// global variables to be reused for each type

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphFactoryImpl;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.bundle.dap.language.java.metamodel.link.*;
import org.openspotlight.bundle.dap.language.java.metamodel.node.*;
import org.openspotlight.bundle.dap.language.java.support.JavaGraphNodeSupport;
import org.openspotlight.bundle.dap.language.java.Constants;

import java.util.Map;
import java.util.TreeMap;

SLGraphFactory factory = new SLGraphFactoryImpl();
SLGraph graph = factory.createTempGraph(true);
SLGraphSession session = graph.openSession();
SLNode currentContextRootNode = session.createContext("${doc.TypeDefinitionSet.name}-${doc.TypeDefinitionSet.version}").getRootNode();
SLNode abstractContextRootNode = session.createContext(Constants.ABSTRACT_CONTEXT).getRootNode();

JavaGraphNodeSupport helper = new JavaGraphNodeSupport(session, currentContextRootNode,abstractContextRootNode);

// global variables to be reused for each type
JavaType newType;
JavaPackage newPackage;
JavaType newSuperType;
JavaPackage newSuperPackage;
JavaType fieldType;
JavaPackage fieldPackage;
JavaDataField field;
JavaMethod method;
JavaDataParameter parameter;
PackageType packageTypeLink;
PackageType fieldPackageTypeLink;
DataType fieldTypeLink;
boolean isPublic;
boolean isPrivate;
boolean isStatic;
boolean isFinal;
boolean isProtected;
boolean isFieldPublic;
boolean isFieldPrivate;
boolean isFieldStatic;
boolean isFieldFinal;
boolean isFieldProtected;
boolean isFieldTransient;
boolean isFieldVolatile;
boolean isMethodPublic;
boolean isMethodPrivate;
boolean isMethodStatic;
boolean isMethodFinal;
boolean isMethodProtected;
boolean isMethodSynchronized;
PackageType superPackageTypeLink;
TypeDeclares typeDeclaresMethod;
JavaType methodReturnTypeType;
MethodReturns methodReturnsType;
JavaPackage methodParameterTypePackage;
JavaType methodParameterTypeType;
PackageType methodParameterTypePackageTypeLink;
MethodParameterDefinition methodParametersType;
JavaPackage newExceptionPackage;
JavaType newExceptionType;
PackageType exceptionPackageTypeLink;
MethodThrows methodThrowsType;
JavaPackage methodReturnTypePackage;
PackageType methodReturnTypePackageTypeLink;
String arraySquareBrackets;
Extends extendsSuper;
Implements implementsSuper;
boolean isArray = false;
int arrayDimensions = 0;

<#list doc.TypeDefinitionSet.types.TypeDefinition as javaType>
<#if javaType.isPrivate=="false">
newType = helper.addTypeOnAbstractContext(JavaType${t.upperFirst(javaType.type?lower_case)}.class,"${javaType.packageName}", "${javaType.typeName}");
<#else>
//ignoring type ${javaType.packageName}${javaType.typeName}
</#if>
</#list>



<#list doc.TypeDefinitionSet.types.TypeDefinition as javaType>
<#if javaType.isPrivate=="false">
newType = helper.addTypeOnCurrentContext(JavaType${t.upperFirst(javaType.type?lower_case)}.class,"${javaType.packageName}", "${javaType.typeName}",${javaType.access});
<#if javaType.extendsDef.packageName?is_string>
    helper.addExtendsLinks("${javaType.packageName}", "${javaType.typeName}","${javaType.extendsDef.packageName}","${javaType.extendsDef.typeName}");
</#if>
<#list javaType.implementsDef.SimpleTypeReference as interface>
    helper.addImplementsLinks("${javaType.packageName}", "${javaType.typeName}","${interface.packageName}","${interface.typeName}");
</#list>

<#list javaType.fields.FieldDeclaration as field>
<#if field.isPrivate=="false">
    isArray = false;
    arrayDimensions = 0;
    // starting field ${javaType.packageName}.${javaType.typeName}#${field.name}
	field = newType.addNode(JavaDataField.class,"${field.name}"); 
	<#if field.type.@class=="SimpleTypeReference">
	fieldType = helper.addTypeOnAbstractContext(JavaType.class, "${field.type.packageName}","${field.type.typeName}");
    <#elseif field.type.@class="PrimitiveTypeReference">
	fieldType = helper.addTypeOnAbstractContext(JavaTypePrimitive.class, "","${field.type.type?lower_case}");
    <#elseif field.type.@class="ArrayTypeReferenceerence">
    // starting array
    arrayDimensions = ${field.type.arrayDimensions};
    <#if field.type.type.@class=="SimpleTypeReference">
    fieldType = helper.addTypeOnAbstractContext(JavaType.class, "${field.type.type.packageName}","${field.type.type.typeName}");
    <#elseif field.type.type.@class="PrimitiveTypeReference">
    fieldType = helper.addTypeOnAbstractContext(JavaTypePrimitive.class, "","${field.type.type.type?lower_case}");
    </#if>
    //ending array
    <#else>
    //field needs to be processed: ${field.type.@class}
    </#if>
    helper.insertFieldData(field, fieldType, ${field.access}, isArray, arrayDimensions);
    // finishing field ${javaType.packageName}.${javaType.typeName}#${field.name}
<#else>
// ignoring field ${javaType.packageName}.${javaType.typeName}#${field.name} 
</#if>
</#list>
<#list javaType.methods.MethodDeclaration as method>
<#if method.private=="false">
    method = helper.createMethod(newType,"${method.fullName}","${method.name}",${method.constructor},${method.access});
    <#if method.returnType.@class=="SimpleTypeReference">
    helper.createMethodReturnType(method,JavaType.class,"${method.returnType.packageName}","${method.returnType.typeName}",false,0);
    <#elseif method.returnType.@class="PrimitiveTypeReference">
    helper.createMethodReturnType(method,JavaTypePrimitive.class,"", "${method.returnType.type?lower_case}",false,0);
    <#elseif method.returnType.@class="ArrayTypeReference">
    <#if method.returnType.type.@class=="SimpleTypeReference">
    helper.createMethodReturnType(method,JavaTypePrimitive.class,"${method.returnType.type.packageName}","${method.returnType.type.typeName}",true,${method.returnType.arrayDimensions});
    <#elseif method.returnType.type.@class="PrimitiveTypeReference">
    helper.createMethodReturnType(method,JavaTypePrimitive.class,"", "${method.returnType.type.type?lower_case}",true,${method.returnType.arrayDimensions});
    </#if>
    <#else>
    //method needs to be processed: ${method.returnType.@class}
    </#if>
    <#list method.thrownExceptions.SimpleTypeReference as exception>
        newExceptionType = helper.addTypeOnAbstractContext(JavaType.class, "${exception.packageName}","${exception.typeName}");
        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
        // ending throws exception ${exception.packageName}.${exception.typeName}
    </#list>
    
    <#list method.parameters.MethodParameter as parameter>
	    isArray = false;
	    arrayDimensions = 0;
        // starting parameter #${parameter.position}
	    <#if parameter.dataType.@class=="SimpleTypeReference">
	    methodParameterTypeType = helper.addTypeOnAbstractContext(JavaType.class, "${parameter.dataType.packageName}","${parameter.dataType.typeName}");
	    <#elseif parameter.dataType.@class="PrimitiveTypeReference">
        methodReturnTypeType = helper.addTypeOnAbstractContext(JavaTypePrimitive.class,"", "${parameter.dataType.type?lower_case}");
	    <#elseif parameter.dataType.@class="ArrayTypeReferenceerence">
	    isArray = true;
        arrayDimensions = ${parameter.dataType.arrayDimensions};
        
	    // starting array
	    <#if parameter.dataType.type.@class=="SimpleTypeReference">
        methodParameterTypeType = helper.addTypeOnAbstractContext(JavaType.class, "${parameter.dataType.type.packageName}","${parameter.dataType.type.typeName}");
	    <#elseif parameter.dataType.type.@class="PrimitiveTypeReference">
	    methodReturnTypeType = helper.addTypeOnAbstractContext(JavaTypePrimitive.class,"", "${parameter.dataType.type.type?lower_case}");
	    </#if>
	    //ending array
	    <#else>
	    //method param needs to be processed: ${parameter.dataType.@class}
	    </#if>
	    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(${parameter.position});
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);
    
        // finishing parameter #${parameter.position}
    </#list>
    // finishing method ${javaType.packageName}.${javaType.typeName}#${method.fullName}
<#else>
    // ignoring method ${javaType.packageName}.${javaType.typeName}#${method.fullName}
</#if>
</#list>

// finishing type ${javaType.packageName}.${javaType.typeName}
// #########################################################
</#if>

</#list>
session.save();

session.close();
graph.shutdown();
        ]]></content>
    </file>
</files>