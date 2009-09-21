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
        <location>bundle-processor/osl-java-bundle/src/main/java/org/openspotlight/bundle/dap/language/${package.@packageName}/metamodel/node</location>
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
package org.openspotlight.bundle.dap.language.${package.@packageName}.metamodel.node;

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