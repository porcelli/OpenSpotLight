<files>
<#list doc.root.package as package>
<#assign nodes = package.nodes>
<#list nodes.nodeData as node>
    <#assign parentClassName="SLNode">
    <#assign className = node.@typeName?replace(" ","")?replace(".","")?replace("-","")>
	<#if node.@parentTypeName != "">
	    <#assign parentClassName = node.@parentTypeName?replace(" ","")?replace(".","")?replace("-","") >
	    <#assign className = parentClassName + node.@typeName?replace(" ","")?replace(".","")?replace("-","")>
	</#if>
    <file>
        <name>${className}.java</name>
        <location>bundle-processor/osl-${package.@packageName}-bundle/src/main/java/org/openspotlight/bundle/${package.@packageName}/metamodel/node</location>
        <content>
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
 * OpenSpotLight - Plataforma de Governan�a de TI de C�digo Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribui��o de direito autoral declarada e atribu�da pelo autor.
 * Todas as contribui��es de terceiros est�o distribu�das sob licen�a da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo sob os 
 * termos da Licen�a P�blica Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU ADEQUA��O A UMA
 * FINALIDADE ESPEC�FICA. Consulte a Licen�a P�blica Geral Menor do GNU para mais detalhes.  
 * 
 * Voc� deve ter recebido uma c�pia da Licen�a P�blica Geral Menor do GNU junto com este
 * programa; se n�o, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.bundle.${package.@packageName}.metamodel.node;

<#if node.@parentTypeName == "">
import org.openspotlight.graph.SLNode;
</#if>
import org.openspotlight.graph.annotation.SLProperty;
import org.openspotlight.graph.annotation.SLDescription;

/**
 * The Interface for node ${(node.@parentTypeName + " " + node.@typeName)?trim} Meta Model.
 *
<#list node.validParent as parent>
<#assign linkedParentClassName=parent?replace(" ","")?replace(".","")?replace("-","")>
 * {@link ${linkedParentClassName}} should be used as parent.
</#list>
 *
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com 
 */
@SLDescription("${(node.@parentTypeName + " " + node.@typeName)?trim}")
public interface ${className} extends ${parentClassName}<#if node.@parentTypeName != ""></#if> {

<#list node.property as property>
    @SLProperty
    public ${property.@propertyType} get${t.upperFirst(property.@propertyName)}();
    public void set${t.upperFirst(property.@propertyName)}(${property.@propertyType} new${t.upperFirst(property.@propertyName)});

</#list>
    
}

        </content>
    </file>
</#list>
</#list>
</files>