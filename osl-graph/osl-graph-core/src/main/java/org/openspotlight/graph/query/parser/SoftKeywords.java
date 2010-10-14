/**
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

/**
 * This class stores all soft keywords used by sl-ql parser.
 * 
 * @author porcelli
 */
public interface SoftKeywords {

    public static final String SELECT    = "select";
    public static final String DEFINE    = "define";
    public static final String LINK      = "link";
    public static final String OUTPUT    = "output";
    public static final String KEEP      = "keep";
    public static final String USE       = "use";
    public static final String PRIMARY   = "primary";
    public static final String N         = "n";
    public static final String PROPERTY  = "property";
    public static final String TERTIARY  = "tertiary";
    public static final String SECONDARY = "secondary";
    public static final String IDENTICAL = "identical";
    public static final String TRUE      = "true";
    public static final String NULL      = "null";
    public static final String FALSE     = "false";
    public static final String WHERE     = "where";
    public static final String TARGET    = "target";
    public static final String MESSAGE   = "message";
    public static final String DOMAIN    = "domain";
    public static final String VALUES    = "values";
    public static final String A         = "a";
    public static final String B         = "b";
    public static final String BOTH      = "both";
    public static final String EXECUTING = "executing";
    public static final String TIMES     = "times";
    public static final String COLLATOR  = "collator";
    public static final String LEVEL     = "level";
    public static final String RESULT    = "result";
    public static final String ORDER     = "order";
    public static final String BY        = "by";
    public static final String LIMIT     = "limit";
    public static final String OFFSET    = "offset";
    public static final String ASC       = "asc";
    public static final String DESC      = "desc";

}
