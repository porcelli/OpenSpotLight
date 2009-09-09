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

import static org.openspotlight.graph.query.SLSideType.ANY_SIDE;
import static org.openspotlight.graph.query.SLSideType.A_SIDE;
import static org.openspotlight.graph.query.SLSideType.BOTH_SIDES;
import static org.openspotlight.graph.query.SLSideType.B_SIDE;

import java.util.ArrayList;
import java.util.List;

import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.query.info.SLSelectByLinkTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo;
import org.openspotlight.graph.query.info.SLSelectByLinkTypeInfo.SLSelectByLinkInfo;
import org.openspotlight.graph.query.info.SLSelectByLinkTypeInfo.SLSelectTypeInfo;

/**
 * The Class SLSelectByLinkTypeImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLSelectByLinkTypeImpl implements SLSelectByLinkType, SLSelectInfoGetter {

	/** The select info. */
	private SLSelectByLinkTypeInfo selectInfo;
	
	/** The types. */
	private List<Type> types;
	
	/** The by links. */
	private List<ByLink> byLinks;
	
	/** The select end. */
	private End selectEnd;
	
	/**
	 * Instantiates a new sL select statement impl.
	 * 
	 * @param selectFacade the select facade
	 */
	public SLSelectByLinkTypeImpl(SLSelectFacade selectFacade) {
		this.selectInfo = new SLSelectByLinkTypeInfo();
		this.types = new ArrayList<Type>();
		this.byLinks = new ArrayList<ByLink>();
		this.selectEnd = new EndImpl(selectFacade, selectInfo);
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLSelectInfoGetter#getSelectInfo()
	 */
	public SLSelectByLinkTypeInfo getSelectInfo() {
		return selectInfo;
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
	 * @see org.openspotlight.graph.query.SLSelectStatement#byLink(java.lang.String)
	 */
	public ByLink byLink(String typeName) {
		SLSelectByLinkInfo byLinkInfo = selectInfo.addByLink(typeName);
		ByLink byLink = new ByLinkImpl(this, byLinkInfo);
		byLinks.add(byLink);
		return byLink;
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
	/**
	 * Gets the select by node type info.
	 * 
	 * @return the select by node type info
	 */
	public SLSelectByLinkTypeInfo getSelectByNodeTypeInfo() {
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
		for (SLSelectByLinkInfo byLinkInfo : selectInfo.getByLinkInfoList()) {
			commaCount += byLinkInfo.isComma() ? 1 : 0;
		}
		if (commaCount == selectInfo.getTypeInfoList().size() + selectInfo.getByLinkInfoList().size()) {
			throw new SLInvalidQuerySyntaxRuntimeException("last SELECT clause item must not preceed comma.");
		}
	}
	
	/**
	 * The Class TypeImpl.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static class TypeImpl implements Type {
		
		/** The select by link type. */
		private SLSelectByLinkType selectByLinkType;
		
		/** The type info. */
		private SLSelectTypeInfo typeInfo;

		/**
		 * Instantiates a new type impl.
		 * 
		 * @param selectByNodeType the select by node type
		 * @param typeInfo the type info
		 */
		TypeImpl(SLSelectByLinkType selectByNodeType, SLSelectTypeInfo typeInfo) {
			this.selectByLinkType = selectByNodeType;
			this.typeInfo = typeInfo;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.Type#comma()
		 */
		public SLSelectByLinkType comma() {
			typeInfo.setComma(true);
			return selectByLinkType;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.Type#selectEnd()
		 */
		public End selectEnd() {
			return selectByLinkType.end();
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.Type#subTypes()
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

		/** The select info. */
		private SLSelectByLinkTypeInfo selectInfo;
		
		/** The where. */
		private SLWhereByLinkType where;
		
		/** The order by. */
		private SLOrderByStatement orderBy;
		
		/** The select facade. */
		private SLSelectFacade selectFacade;
		
		/**
		 * Instantiates a new end impl.
		 * 
		 * @param selectFacade the select facade
		 * @param selectInfo the select info
		 */
		EndImpl(SLSelectFacade selectFacade, SLSelectByLinkTypeInfo selectInfo) {
			this.selectFacade = selectFacade;
			this.selectInfo = selectInfo;
			this.orderBy = new SLOrderByStatementImpl();
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.End#where()
		 */
		public SLWhereByLinkType where() {
			if (this.where == null) {
				SLWhereByLinkTypeInfo whereByLinkType = new SLWhereByLinkTypeInfo(selectInfo);
				selectInfo.setWhereByLinkTypeInfo(whereByLinkType);
				this.where = new SLWhereByLinkTypeImpl(orderBy, whereByLinkType);
			}
			return where;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.End#orderBy()
		 */
		public SLOrderByStatement orderBy() {
			return orderBy;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.End#keepResult()
		 */
		public End keepResult() {
			selectInfo.setKeepResult(true);
			return this;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.End#executeXTimes()
		 */
		public End executeXTimes() {
			selectInfo.setXTimes(0);
			return this;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.End#executeXTimes(int)
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
	}
	
	/**
	 * The Class ByLinkImpl.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static class ByLinkImpl implements ByLink {
		
		/** The select. */
		private SLSelectByLinkType select;
		
		/** The by link info. */
		private SLSelectByLinkInfo byLinkInfo;
		
		/**
		 * Instantiates a new by link impl.
		 * 
		 * @param select the select
		 * @param byLinkInfo the by link info
		 */
		public ByLinkImpl(SLSelectByLinkType select, SLSelectByLinkInfo byLinkInfo) {
			this.select = select;
			this.byLinkInfo = byLinkInfo;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.ByLink#a()
		 */
		public ByLink a() {
			byLinkInfo.setSide(A_SIDE);
			return this;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.ByLink#b()
		 */
		public ByLink b() {
			byLinkInfo.setSide(B_SIDE);
			return this;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.ByLink#any()
		 */
		public ByLink any() {
			byLinkInfo.setSide(ANY_SIDE);
			return this;
		}

		/**
		 * Both.
		 * 
		 * @return the by link
		 */
		public ByLink both() {
			byLinkInfo.setSide(BOTH_SIDES);
			return this;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.ByLink#comma()
		 */
		public SLSelectByLinkType comma() {
			byLinkInfo.setComma(true);
			return select;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectByLinkType.ByLink#selectEnd()
		 */
		public End selectEnd() {
			return select.end();
		}
	}
}
