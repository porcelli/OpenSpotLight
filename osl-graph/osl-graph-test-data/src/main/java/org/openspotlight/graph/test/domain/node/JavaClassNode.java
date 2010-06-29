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
package org.openspotlight.graph.test.domain.node;

import org.openspotlight.graph.annotation.*;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;

import java.util.Date;

//@SLTransient
//@CollatorLevel(IDENTICAL)
//@RenderHint(key="format", value="cube");
//@RenderHint(key="foreGroundColor" value="back");

/**
 * The Interface JavaClassNode.
 * 
 * @author Vitor Hugo Chagas
 */

@SLDescription( "Java Class" )
@SLRenderHints( {@SLRenderHint( name = "format", value = "cube" ), @SLRenderHint( name = "foreground", value = "gold" )} )
public interface JavaClassNode extends JavaElementNode {

    /** The Constant MODIFIER_PUBLIC. */
    public static final Integer MODIFIER_PUBLIC    = 1;

    /** The Constant MODIFIER_PRIVATE. */
    public static final Integer MODIFIER_PRIVATE   = 2;

    /** The Constant MODIFIER_PROTECTED. */
    public static final Integer MODIFIER_PROTECTED = 3;

    /** The Constant MODIFIER_DEFAULT. */
    public static final Integer MODIFIER_DEFAULT   = 4;

    // @SLProperty(collatorLevel=IDENTICAL)
    /**
     * Gets the class name.
     * 
     * @return the class name
     */
    @SLProperty
    @SLVisibility( VisibilityLevel.INTERNAL )
    public String getClassName();

    /**
     * Sets the class name.
     * 
     * @param className the new class name
     */
    public void setClassName( String className );

    /**
     * Gets the modifier.
     * 
     * @return the modifier
     */
    @SLProperty
    public Integer getModifier();

    /**
     * Sets the modifier.
     * 
     * @param modifier the new modifier
     */
    public void setModifier( Integer modifier );

    /**
     * Gets the creation time.
     * 
     * @return the creation time
     */
    @SLProperty
    public Date getCreationTime();

    /**
     * Sets the creation time.
     * 
     * @param creationTime the new creation time
     */
    public void setCreationTime( Date creationTime );
}
