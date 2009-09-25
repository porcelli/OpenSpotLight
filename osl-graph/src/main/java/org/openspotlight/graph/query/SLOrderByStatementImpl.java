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

import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.query.info.SLOrderByStatementInfo;
import org.openspotlight.graph.query.info.SLOrderByTypeInfo;
import org.openspotlight.graph.query.info.SLSelectStatementInfo;
import org.openspotlight.graph.query.info.SLOrderByTypeInfo.OrderType;

/**
 * The Class SLOrderByStatementImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLOrderByStatementImpl implements SLOrderByStatement {
	
	/** The select facade. */
	private SLSelectFacade selectFacade;
	
	/** The order by statement info. */
	private SLOrderByStatementInfo orderByStatementInfo;
	
	/**
	 * Instantiates a new sL order by statement impl.
	 * 
	 * @param selectFacade the select facade
	 * @param orderByStatementInfo the order by statement info
	 */
	public SLOrderByStatementImpl(SLSelectFacade selectFacade, SLOrderByStatementInfo orderByStatementInfo) {
		this.selectFacade = selectFacade;
		this.orderByStatementInfo = orderByStatementInfo;
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLOrderByStatement#type(java.lang.String)
	 */
	public Type type(String typeName) {
		SLOrderByTypeInfo typeInfo = new SLOrderByTypeInfo();
		typeInfo.setOrderByStatementInfo(orderByStatementInfo);
		typeInfo.setTypeName(typeName);
		orderByStatementInfo.getOrderByTypeInfoList().add(typeInfo);
		return new TypeImpl(this, selectFacade, typeInfo);
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLOrderByStatement#orderByEnd()
	 */
	public End orderByEnd() {
		return new EndImpl(selectFacade, orderByStatementInfo.getSelectStatementInfo());
	}

	/**
	 * The Class TypeImpl.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static class TypeImpl implements Type {
		
		/** The order by statement. */
		private SLOrderByStatement orderByStatement;
		
		/** The select facade. */
		private SLSelectFacade selectFacade;
		
		/** The type info. */
		private SLOrderByTypeInfo typeInfo;
		
		/**
		 * Instantiates a new type impl.
		 * 
		 * @param orderByStatement the order by statement
		 * @param selectFacade the select facade
		 * @param typeInfo the type info
		 */
		public TypeImpl(SLOrderByStatement orderByStatement, SLSelectFacade selectFacade, SLOrderByTypeInfo typeInfo) {
			this.orderByStatement = orderByStatement;
			this.selectFacade = selectFacade;
			this.typeInfo = typeInfo;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLOrderByStatement.Type#subTypes()
		 */
		public Type subTypes() {
			typeInfo.setSubTypes(true);
			return this;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLOrderByStatement.Type#property(java.lang.String)
		 */
		public Property property(String name) {
			typeInfo.setPropertyName(name);
			return new PropertyImpl(orderByStatement, selectFacade, typeInfo);
		}
		
		/**
		 * The Class PropertyImpl.
		 * 
		 * @author Vitor Hugo Chagas
		 */
		public static class PropertyImpl implements Property {
			
			/** The order by statement. */
			SLOrderByStatement orderByStatement;
			
			/** The select facade. */
			private SLSelectFacade selectFacade;
			
			/** The type info. */
			private SLOrderByTypeInfo typeInfo;

			/**
			 * Instantiates a new property impl.
			 * 
			 * @param orderByStatement the order by statement
			 * @param selectFacade the select facade
			 * @param typeInfo the type info
			 */
			public PropertyImpl(SLOrderByStatement orderByStatement, SLSelectFacade selectFacade, SLOrderByTypeInfo typeInfo) {
				this.selectFacade = selectFacade;
				this.typeInfo = typeInfo;
				this.orderByStatement = orderByStatement;
			}

			/* (non-Javadoc)
			 * @see org.openspotlight.graph.query.SLOrderByStatement.Type.Property#ascending()
			 */
			public SLOrderByStatement ascending() {
				typeInfo.setOrderType(OrderType.ASCENDING);
				return orderByStatement;
			}

			/* (non-Javadoc)
			 * @see org.openspotlight.graph.query.SLOrderByStatement.Type.Property#descending()
			 */
			public SLOrderByStatement descending() {
				typeInfo.setOrderType(OrderType.DESCENDING);
				return orderByStatement;
			}

			/* (non-Javadoc)
			 * @see org.openspotlight.graph.query.SLOrderByStatement.Type.Property#orderByEnd()
			 */
			public End orderByEnd() {
				return new EndImpl(selectFacade, typeInfo.getOrderByStatementInfo().getSelectStatementInfo());
			}
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
		
		/** The select statement info. */
		private SLSelectStatementInfo selectStatementInfo;

		/**
		 * Instantiates a new end impl.
		 * 
		 * @param selectFacade the select facade
		 * @param selectStatementInfo the select statement info
		 */
		public EndImpl(SLSelectFacade selectFacade, SLSelectStatementInfo selectStatementInfo) {
			this.selectFacade = selectFacade;
			this.selectStatementInfo = selectStatementInfo;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLOrderByStatement.Type.End#collator(int)
		 */
		public End collator(int strength) {
			selectStatementInfo.setCollatorStrength(strength);
			return this;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLOrderByStatement.Type.End#executeXTimes()
		 */
		public End executeXTimes() {
			selectStatementInfo.setXTimes(0);
			return this;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLOrderByStatement.Type.End#executeXTimes(int)
		 */
		public End executeXTimes(int x) {
			selectStatementInfo.setXTimes(x);
			return null;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLOrderByStatement.Type.End#keepResult()
		 */
		public End keepResult() {
			selectStatementInfo.setKeepResult(true);
			return this;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectFacade#select()
		 */
		public SLSelectStatement select() throws SLGraphSessionException {
			return selectFacade.select();
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLSelectFacade#selectByLinkCount()
		 */
		public SLSelectByLinkCount selectByLinkCount() throws SLGraphSessionException {
			return selectFacade.selectByLinkCount();
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

}
