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
 * OpenSpotLight - Plataforma de Governana de TI de C—digo Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribui‹o de direito autoral declarada e atribu’da pelo autor.
 * Todas as contribui›es de terceiros est‹o distribu’das sob licena da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa Ž software livre; voc pode redistribu’-lo e/ou modific‡-lo sob os 
 * termos da Licena Pœblica Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa Ž distribu’do na expectativa de que seja œtil, porŽm, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia impl’cita de COMERCIABILIDADE OU ADEQUA‚ÌO A UMA
 * FINALIDADE ESPECêFICA. Consulte a Licena Pœblica Geral Menor do GNU para mais detalhes.  
 * 
 * Voc deve ter recebido uma c—pia da Licena Pœblica Geral Menor do GNU junto com este
 * programa; se n‹o, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.graph;

import org.openspotlight.SLRuntimeException;

/**
 * The Class NamePredicate.
 * 
 * @author Vitor Hugo Chagas
 */
public class NamePredicate implements SLNodePredicate {
	
	/** The name. */
	private String name;
	
	/**
	 * Instantiates a new name predicate.
	 * 
	 * @param name the name
	 */
	public NamePredicate(String name) {
		this.name = name;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.apache.commons.collections15.Predicate#evaluate(java.lang.Object)
	 */
	public boolean evaluate(SLNode node) {
		try {
			return node.getName().indexOf(name) > -1;
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to evaluate name predicate.", e);
		}
	}
}
