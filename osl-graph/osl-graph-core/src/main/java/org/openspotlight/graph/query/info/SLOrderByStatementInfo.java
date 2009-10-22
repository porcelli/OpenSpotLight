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
package org.openspotlight.graph.query.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class SLOrderByStatementInfo.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLOrderByStatementInfo implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The select statement info. */
	private SLSelectStatementInfo selectStatementInfo;
	
	/** The order by type info list. */
	private List<SLOrderByTypeInfo> orderByTypeInfoList = new ArrayList<SLOrderByTypeInfo>();
	
	/**
	 * Instantiates a new sL order by statement info.
	 * 
	 * @param selectStatementInfo the select statement info
	 */
	public SLOrderByStatementInfo(SLSelectStatementInfo selectStatementInfo) {
		this.selectStatementInfo = selectStatementInfo;
	}

	/**
	 * Gets the select statement info.
	 * 
	 * @return the select statement info
	 */
	public SLSelectStatementInfo getSelectStatementInfo() {
		return selectStatementInfo;
	}

	/**
	 * Sets the select statement info.
	 * 
	 * @param selectStatementInfo the new select statement info
	 */
	public void setSelectStatementInfo(SLSelectStatementInfo selectStatementInfo) {
		this.selectStatementInfo = selectStatementInfo;
	}

	/**
	 * Gets the order by type info list.
	 * 
	 * @return the order by type info list
	 */
	public List<SLOrderByTypeInfo> getOrderByTypeInfoList() {
		return orderByTypeInfoList;
	}

	/**
	 * Sets the order by type info list.
	 * 
	 * @param orderByTypeInfoList the new order by type info list
	 */
	public void setOrderByTypeInfoList(List<SLOrderByTypeInfo> orderByTypeInfoList) {
		this.orderByTypeInfoList = orderByTypeInfoList;
	}
	
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("\nORDER BY\n");
        for (SLOrderByTypeInfo orderByType : orderByTypeInfoList) {
            buffer.append(orderByType.toString());
        }

        return buffer.toString();
    }
}
