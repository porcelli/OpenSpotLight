import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;

import org.openspotlight.bundle.language.java.metamodel.link.*;
import org.openspotlight.bundle.language.java.metamodel.node.*;
import org.openspotlight.bundle.language.java.resolver.JavaGraphNodeSupport;
import org.openspotlight.bundle.language.java.JavaConstants;
import java.util.Map;
import java.util.TreeMap;

JavaType newType=null;
JavaMethod method=null;

<#list doc.TypeDefinitionSet.types.TypeDefinition as javaType>
<#if javaType.isPrivate=="false">
newType = helper.addTypeOnCurrentContext(JavaType${t.upperFirst(javaType.type?lower_case)}.class,"${javaType.packageName}", "${javaType.typeName}",${javaType.access});
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
    <#if field.type.@class=="SimpleTypeReference">
	helper.createField(newType,JavaType.class,"${field.type.packageName}","${field.type.typeName}","${field.name}",${field.access},false,0);
    <#elseif field.type.@class="PrimitiveTypeReference">
    helper.createField(newType,JavaTypePrimitive.class,"","${field.type.type?lower_case}","${field.name}",${field.access},false,0);
    <#elseif field.type.@class="ArrayTypeReferenceerence">
    <#if field.type.type.@class=="SimpleTypeReference">
    helper.createField(newType,JavaType.class,"${field.type.type.packageName}","${field.type.type.typeName}","${field.name}",${field.access},true,${field.type.arrayDimensions});
    <#elseif field.type.type.@class="PrimitiveTypeReference">
    helper.createField(newType,JavaTypePrimitive.class,"","${field.type.type.type?lower_case}","${field.name}",${field.access},true,${field.type.arrayDimensions});
    </#if>
    </#if>
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
    </#if>
    <#list method.thrownExceptions.SimpleTypeReference as exception>
        helper.addThrowsOnMethod(method, "${exception.packageName}","${exception.typeName}");
    </#list>
    <#list method.parameters.MethodParameter as parameter>
	    <#if parameter.dataType.@class=="SimpleTypeReference">
		helper.createMethodParameter(method,JavaType.class,${parameter.position},"${parameter.dataType.packageName}","${parameter.dataType.typeName}",false,0 ) ;
	    <#elseif parameter.dataType.@class="PrimitiveTypeReference">
	    helper.createMethodParameter(method,JavaTypePrimitive.class,${parameter.position},"", "${parameter.dataType.type?lower_case}",false,0 ) ;
	    <#elseif parameter.dataType.@class="ArrayTypeReferenceerence">
	    <#if parameter.dataType.type.@class=="SimpleTypeReference">
        helper.createMethodParameter(method,JavaType.class,${parameter.position},"${parameter.dataType.type.packageName}","${parameter.dataType.type.typeName}",true,${parameter.dataType.arrayDimensions}) ;
	    <#elseif parameter.dataType.type.@class="PrimitiveTypeReference">
        helper.createMethodParameter(method,JavaTypePrimitive.class,${parameter.position},"", "${parameter.dataType.type.type?lower_case}",true,${parameter.dataType.arrayDimensions}) ;
	    </#if>
	    </#if>
    </#list>
<#else>
</#if>
</#list>
</#if>
</#list>