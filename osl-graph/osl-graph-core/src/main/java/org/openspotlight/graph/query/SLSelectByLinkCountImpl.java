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
package org.openspotlight.graph.query;

import java.util.ArrayList;
import java.util.List;

import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.query.info.SLSelectByLinkCountInfo;
import org.openspotlight.graph.query.info.SLSelectInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkCountInfo;
import org.openspotlight.graph.query.info.SLSelectByLinkCountInfo.SLSelectTypeInfo;

/**
 * The Class SLSelectByLinkCountImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLSelectByLinkCountImpl implements SLSelectByLinkCount, SLSelectInfoGetter {

	/** The select info. */
	private SLSelectByLinkCountInfo selectInfo;
	
	/** The types. */
	private List<Type> types;
	
	/** The select end. */
	private End selectEnd;
	
	/**
	 * Instantiates a new sL select by link count impl.
	 * 
	 * @param selectFacade the select facade
	 */
	public SLSelectByLinkCountImpl(SLSelectFacade selectFacade) {
		this.selectInfo = new SLSelectByLinkCountInfo();
		this.types = new ArrayList<Type>();
		this.selectEnd = new EndImpl(selectFacade, selectInfo);
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLSelectStatement#type(java.lang.String)
	 */
	public Type type(String typeName) {
		SLSelectTypeInfo typeInfo = selectInfo.addType(typeName);
		Type type = new TypeImpl(this, typeInfo);
		types.add(type);
		return type;
	}


	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLSelectStatement#end()
	 */
	public End end() {
		verifyIfLastItemTerminatedWithComma();
		return selectEnd;
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLSelectStatementInfoGetter#getSelectInfo()
	 */
	public SLSelectInfo getSelectInfo() {
		return selectInfo;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return selectInfo.toString();
	}
	
	/**
	 * Verify if last item terminated with comma.
	 */
	private void verifyIfLastItemTerminatedWithComma() {
		int commaCount = 0;
		for (SLSelectTypeInfo typeInfo : selectInfo.getTypeInfoList()) {
			commaCount += typeInfo.isComma() ? 1 : 0;
		}
	}
	
	/**
	 * The Class TypeImpl.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static class TypeImpl implements Type {
		
		/** The select by link count. */
		private SLSelectByLinkCount selectByLinkCount;
		
		/** The type info. */
		private SLSelectTypeInfo typeInfo;

		/**
		 * Instantiates a new type impl.
		 * 
		 * @param selectByNodeType the select by node type
		 * @param typeInfo the type info
		 */
		TypeImpl(SLSelectByLinkCount selectByNodeType, SLSelectTypeInfo typeInfo) {
			this.selectByLinkCount = selectByNodeType;
			this.typeInfo = typeInfo;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkCount.Type#comma()
		 */
		public SLSelectByLinkCount comma() {
			typeInfo.setComma(true);
			return selectByLinkCount;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkCount.Type#selectEnd()
		 */
		public End selectEnd() {
			return selectByLinkCount.end();
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkCount.Type#subTypes()
		 */
		public Type subTypes() {
			typeInfo.setSubTypes(true);
			return this;
		}
		
	}
	
	/**
	 * The Class EndImpl.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static class EndImpl implements End {
		
		/** The select facade. */
		private SLSelectFacade selectFacade;

		/** The select info. */
		private SLSelectByLinkCountInfo selectInfo;
		
		/** The where. */
		private SLWhereByLinkCount where;
		
		/** The order by. */
		private SLOrderByStatement orderBy;

		/**
		 * Instantiates a new end impl.
		 * 
		 * @param selectFacade the select facade
		 * @param selectInfo the select info
		 */
		EndImpl(SLSelectFacade selectFacade, SLSelectByLinkCountInfo selectInfo) {
			this.selectFacade = selectFacade;
			this.selectInfo = selectInfo;
			//this.orderBy = new SLOrderByStatementImpl();
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkCount.End#where()
		 */
		public SLWhereByLinkCount where() {
			if (this.where == null) {
				SLWhereByLinkCountInfo whereStatementInfo = new SLWhereByLinkCountInfo(selectInfo);
				selectInfo.setWhereStatementInfo(whereStatementInfo);
				this.where = new SLWhereByLinkCountImpl(selectFacade, orderBy, whereStatementInfo);
			}
			return where;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkCount.End#orderBy()
		 */
		public SLOrderByStatement orderBy() {
			return orderBy;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkCount.End#keepResult()
		 */
		public End keepResult() {
			selectInfo.setKeepResult(true);
			return this;
		}
		
        /**
         * {@inheritDoc}
         */
        public End limit( Integer limit ) {
            selectInfo.setLimit(limit);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End limit( Integer limit,
                          Integer offset ) {
            selectInfo.setLimit(limit);
            selectInfo.setOffset(offset);
            return this;
        }

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkCount.End#executeXTimes()
		 */
		/**
		 * Execute x times.
		 * 
		 * @return the end
		 */
		public End executeXTimes() {
			selectInfo.setXTimes(0);
			return this;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkCount.End#executeXTimes(int)
		 */
		/**
		 * Execute x times.
		 * 
		 * @param x the x
		 * 
		 * @return the end
		 */
		public End executeXTimes(int x) {
			selectInfo.setXTimes(x);
			return this;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectFacade#selectByLinkType()
		 */
		public SLSelectByLinkType selectByLinkType() throws SLGraphSessionException {
			return selectFacade.selectByLinkType();
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectFacade#selectByNodeType()
		 */
		public SLSelectByNodeType selectByNodeType() throws SLGraphSessionException {
			return selectFacade.selectByNodeType();
		}
		
		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectFacade#selectByLinkCount()
		 */
		public SLSelectByLinkCount selectByLinkCount() throws SLGraphSessionException {
			return selectFacade.selectByLinkCount();
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectFacade#select()
		 */
		public SLSelectStatement select() throws SLGraphSessionException {
			return selectFacade.select();
		}
	}
}
