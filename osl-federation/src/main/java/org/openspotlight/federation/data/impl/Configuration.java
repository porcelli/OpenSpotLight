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

package org.openspotlight.federation.data.impl;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.AbstractConfigurationNode;

/**
 * This is the root node of the configuration classes that contains the
 * following structure.
 * 
 * <pre>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 * &lt;configuration&gt;
 * 	&lt;repository name=&quot;development&quot; active=&quot;true&quot;
 * 		numberOfParallelThreads=&quot;15&quot;&gt;
 * 		&lt;project name=&quot;Open SpotLight&quot; active=&quot;true&quot;&gt;
 * 			&lt;bundle name=&quot;Java sources&quot; active=&quot;true&quot; type=&quot;fileSystem&quot;
 * 				initialLookup=&quot;/usr/src/osl&quot;&gt;
 * 				&lt;artifact
 * 					name=&quot;/usr/src/osl/src/main/java/org/openspotligth/Example.java&quot;
 * 					dataSha1=&quot;sha1&quot; /&gt;
 * 				&lt;artifact
 * 					name=&quot;/usr/src/osl/src/main/java/org/openspotligth/AnotherOneExample.java&quot;
 * 					dataSha1=&quot;sha1&quot; /&gt;
 * 				&lt;artifactMapping name=&quot;all java files&quot; active=&quot;true&quot;
 * 					included=&quot;/src/main/java/ **.java&quot; /&gt;
 * 				&lt;artifactMapping name=&quot;no older types&quot; active=&quot;true&quot;
 * 					excluded=&quot;** / *.asm&quot; /&gt;
 * 			&lt;/bundle&gt;
 * 		&lt;/project&gt;
 * 	&lt;/repository&gt;
 * &lt;/configuration&gt;
 * </pre>
 * 
 * This structure are used to pass the artifacts to the parser. All the classes
 * are thread save by default.
 * 
 * @see ConfigurationNodeMetadata
 * @see AbstractConfigurationNode
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@ThreadSafe
public final class Configuration {
    
}