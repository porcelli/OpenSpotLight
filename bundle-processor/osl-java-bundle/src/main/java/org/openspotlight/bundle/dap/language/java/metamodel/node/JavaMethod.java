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
 * OpenSpotLight - Plataforma de Governan√ßa de TI de C√≥digo Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribui√ß√£o de direito autoral declarada e atribu√≠da pelo autor.
 * Todas as contribui√ß√µes de terceiros est√£o distribu√≠das sob licen√ßa da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa √© software livre; voc√™ pode redistribu√≠-lo e/ou modific√°-lo sob os 
 * termos da Licen√ßa P√∫blica Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa √© distribu√≠do na expectativa de que seja √∫til, por√©m, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia impl√≠cita de COMERCIABILIDADE OU ADEQUA√á√ÉO A UMA
 * FINALIDADE ESPEC√çFICA. Consulte a Licen√ßa P√∫blica Geral Menor do GNU para mais detalhes.  
 * 
 * Voc√™ deve ter recebido uma c√≥pia da Licen√ßa P√∫blica Geral Menor do GNU junto com este
 * programa; se n√£o, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.bundle.dap.language.java.metamodel.node;

import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.annotation.SLDescription;
import org.openspotlight.graph.annotation.SLProperty;

// TODO: Auto-generated Javadoc
/**
 * The Interface for node Java Method Meta Model. {@link JavaType} should be used as parent.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SLDescription( "Java Method" )
public interface JavaMethod extends SLNode {

    /**
     * Gets the static.
     * 
     * @return the static
     */
    @SLProperty
    public Boolean getStatic();

    /**
     * Sets the static.
     * 
     * @param newStatic the new static
     */
    public void setStatic( Boolean newStatic );

    /**
     * Gets the version.
     * 
     * @return the version
     */
    @SLProperty
    public String getVersion();

    /**
     * Sets the version.
     * 
     * @param newVersion the new version
     */
    public void setVersion( String newVersion );

    /**
     * Gets the public.
     * 
     * @return the public
     */
    @SLProperty
    public Boolean getPublic();

    /**
     * Sets the public.
     * 
     * @param newPublic the new public
     */
    public void setPublic( Boolean newPublic );

    /**
     * Gets the protected.
     * 
     * @return the protected
     */
    @SLProperty
    public Boolean getProtected();

    /**
     * Sets the protected.
     * 
     * @param newProtected the new protected
     */
    public void setProtected( Boolean newProtected );

    /**
     * Gets the final.
     * 
     * @return the final
     */
    @SLProperty
    public Boolean getFinal();

    /**
     * Sets the final.
     * 
     * @param newFinal the new final
     */
    public void setFinal( Boolean newFinal );

    /**
     * Gets the synchronized.
     * 
     * @return the synchronized
     */
    @SLProperty
    public Boolean getSynchronized();

    /**
     * Sets the synchronized.
     * 
     * @param newSynchronized the new synchronized
     */
    public void setSynchronized( Boolean newSynchronized );

    /**
     * Gets the private.
     * 
     * @return the private
     */
    @SLProperty
    public Boolean getPrivate();

    /**
     * Sets the private.
     * 
     * @param newPrivate the new private
     */
    public void setPrivate( Boolean newPrivate );

    /**
     * Gets the simple name.
     * 
     * @return the simple name
     */
    @SLProperty
    public String getSimpleName();

    /**
     * Sets the simple name.
     * 
     * @param newSimpleName the new simple name
     */
    public void setSimpleName( String newSimpleName );

}
