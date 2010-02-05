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
package org.openspotlight.bundle.language.java.metamodel.node;

import org.openspotlight.graph.annotation.SLDescription;
import org.openspotlight.graph.annotation.SLProperty;

// TODO: Auto-generated Javadoc
/**
 * The Interface for node Java Data Field Meta Model.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SLDescription("Java Data Field")
public interface JavaDataField extends JavaData {

	/**
	 * Gets the final.
	 * 
	 * @return the final
	 */
	@SLProperty
	public Boolean getFinal();

	/**
	 * Gets the private.
	 * 
	 * @return the private
	 */
	@SLProperty
	public Boolean getPrivate();

	/**
	 * Gets the protected.
	 * 
	 * @return the protected
	 */
	@SLProperty
	public Boolean getProtected();

	/**
	 * Gets the public.
	 * 
	 * @return the public
	 */
	@SLProperty
	public Boolean getPublic();

	@SLProperty
	public String getQualifiedName();

	/**
	 * Gets the static.
	 * 
	 * @return the static
	 */
	@SLProperty
	public Boolean getStatic();

	/**
	 * Gets the transient.
	 * 
	 * @return the transient
	 */
	@SLProperty
	public Boolean getTransient();

	/**
	 * Gets the volatile.
	 * 
	 * @return the volatile
	 */
	@SLProperty
	public Boolean getVolatile();

	/**
	 * Sets the final.
	 * 
	 * @param newFinal
	 *            the new final
	 */
	public void setFinal(Boolean newFinal);

	/**
	 * Sets the private.
	 * 
	 * @param newPrivate
	 *            the new private
	 */
	public void setPrivate(Boolean newPrivate);

	/**
	 * Sets the protected.
	 * 
	 * @param newProtected
	 *            the new protected
	 */
	public void setProtected(Boolean newProtected);

	/**
	 * Sets the public.
	 * 
	 * @param newPublic
	 *            the new public
	 */
	public void setPublic(Boolean newPublic);

	public void setQualifiedName(String newQualifiedName);

	/**
	 * Sets the static.
	 * 
	 * @param newStatic
	 *            the new static
	 */
	public void setStatic(Boolean newStatic);

	/**
	 * Sets the transient.
	 * 
	 * @param newTransient
	 *            the new transient
	 */
	public void setTransient(Boolean newTransient);

	/**
	 * Sets the volatile.
	 * 
	 * @param newVolatile
	 *            the new volatile
	 */
	public void setVolatile(Boolean newVolatile);

}
