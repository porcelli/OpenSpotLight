
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
package org.openspotlight.graph.test.domain.node;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.annotation.SLDescription;
import org.openspotlight.graph.annotation.SLProperty;

/**
 * The Interface for node CAS Job.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com 
 */
@SLDescription("CAS Job")
public interface CASJobNode extends SLNode {
    
    /**
     * Gets the caption.
     * 
     * @return the caption
     * 
     * @throws SLGraphSessionException the SL graph session exception
     */
    @SLProperty
    public String getCaption() throws SLGraphSessionException;
    
    /**
     * Sets the caption.
     * 
     * @param caption the new caption
     * 
     * @throws SLGraphSessionException the SL graph session exception
     */
    public void setCaption(String caption) throws SLGraphSessionException;
}

        