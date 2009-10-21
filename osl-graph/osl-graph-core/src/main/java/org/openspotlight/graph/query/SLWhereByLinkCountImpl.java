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
import org.openspotlight.graph.query.info.SLWhereByLinkCountInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkCountInfo.SLWhereTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkCountInfo.SLWhereTypeInfo.SLTypeStatementInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkCountInfo.SLWhereTypeInfo.SLTypeStatementInfo.SLConditionInfo;

/**
 * The Class SLWhereByLinkCountImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLWhereByLinkCountImpl implements SLWhereByLinkCount {

	/** The end. */
	private End end;
	
	/** The where by link count info. */
	private SLWhereByLinkCountInfo whereByLinkCountInfo;

	/**
	 * Instantiates a new sL where by link count impl.
	 * 
	 * @param selectFacade the select facade
	 * @param orderBy the order by
	 * @param whereByLinkCountInfo the where by link count info
	 */
	public SLWhereByLinkCountImpl(SLSelectFacade selectFacade, SLOrderByStatement orderBy, SLWhereByLinkCountInfo whereByLinkCountInfo) {
		this(new EndImpl(selectFacade, whereByLinkCountInfo, orderBy), whereByLinkCountInfo);
	}

	/**
	 * Instantiates a new sL where by link count impl.
	 * 
	 * @param end the end
	 * @param whereByLinkCountInfo the where by link count info
	 */
	public SLWhereByLinkCountImpl(End end, SLWhereByLinkCountInfo whereByLinkCountInfo) {
		this.end = end;
		this.whereByLinkCountInfo = whereByLinkCountInfo;
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLWhereByLinkCountInfoGetter#getWhereStatementInfo()
	 */
	/**
	 * Gets the where statement info.
	 * 
	 * @return the where statement info
	 */
	public SLWhereByLinkCountInfo getWhereStatementInfo() {
		return whereByLinkCountInfo;
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLWhereByLinkCount#type(java.lang.String)
	 */
	public Type type(String typeName) {
		SLWhereTypeInfo typeInfo = new SLWhereTypeInfo(typeName);
		whereByLinkCountInfo.getWhereTypeInfoList().add(typeInfo);
		return new TypeImpl(this, typeInfo);
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLWhereByLinkCount#whereEnd()
	 */
	public End whereEnd() {
		return end;
	}

	/**
	 * The Class EndImpl.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	
	public static class EndImpl implements End {
		
		/** The where by link count info. */
		private SLWhereByLinkCountInfo whereByLinkCountInfo;
		
		/** The order by statement. */
		private SLOrderByStatement orderByStatement;
		
		/** The select facade. */
		private SLSelectFacade selectFacade;
		
		/**
		 * Instantiates a new end impl.
		 * 
		 * @param selectFacade the select facade
		 * @param whereByLinkCountInfo the where by link count info
		 * @param orderByStatement the order by statement
		 */
		public EndImpl(SLSelectFacade selectFacade, SLWhereByLinkCountInfo whereByLinkCountInfo, SLOrderByStatement orderByStatement) {
			this.selectFacade = selectFacade;
			this.whereByLinkCountInfo = whereByLinkCountInfo;
			this.orderByStatement = orderByStatement;
		}
		
		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLWhereByLinkCount.End#orderBy()
		 */
		public SLOrderByStatement orderBy() {
			return orderByStatement;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLWhereByLinkCount.End#keepResult()
		 */
		public End keepResult() {
			whereByLinkCountInfo.getSelectByLinkCountInfo().setKeepResult(true);
			return this;
		}
		
        /**
         * {@inheritDoc}
         */
        public End limit( Integer limit ) {
            whereByLinkCountInfo.getSelectByLinkCountInfo().setLimit(limit);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End limit( Integer limit,
                          Integer offset ) {
            whereByLinkCountInfo.getSelectByLinkCountInfo().setLimit(limit);
            whereByLinkCountInfo.getSelectByLinkCountInfo().setOffset(offset);
            return this;
        }
		
		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLWhereByLinkCount.End#executeXTimes()
		 */
		public End executeXTimes() {
			whereByLinkCountInfo.getSelectByLinkCountInfo().setXTimes(0);
			return this;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLWhereByLinkCount.End#executeXTimes(int)
		 */
		public End executeXTimes(int x) {
			whereByLinkCountInfo.getSelectByLinkCountInfo().setXTimes(x);
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

		public SLSelectStatement select() throws SLGraphSessionException {
			return selectFacade.select();
		}
	}

	/**
	 * The Class TypeImpl.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static class TypeImpl implements Type {
		
		/** The where statement. */
		private SLWhereByLinkCount whereStatement;
		
		/** The type info. */
		private SLWhereTypeInfo typeInfo;
		
		/**
		 * Instantiates a new type impl.
		 * 
		 * @param whereStatement the where statement
		 * @param typeInfo the type info
		 */
		public TypeImpl(SLWhereByLinkCount whereStatement, SLWhereTypeInfo typeInfo) {
			this.whereStatement = whereStatement;
			this.typeInfo = typeInfo;
		}

		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type#subTypes()
		 */
		public SubTypes subTypes() {
			typeInfo.setSubTypes(true);
			return new SubTypesImpl(whereStatement, typeInfo);
		}
		
		/* (non-Javadoc)
		 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type#each()
		 */
		public Each each() {
			SLTypeStatementInfo whereStatementInfo = new SLTypeStatementInfo(typeInfo);
			typeInfo.setTypeStatementInfo(whereStatementInfo);
			SLConditionInfo conditionInfo = whereStatementInfo.addCondition();
			return new EachImpl(whereStatement, conditionInfo);
		}
		
		/**
		 * The Class SubTypesImpl.
		 * 
		 * @author Vitor Hugo Chagas
		 */
		public static class SubTypesImpl implements SubTypes {
			
			/** The where statement. */
			private SLWhereByLinkCount whereStatement;
			
			/** The type info. */
			private SLWhereTypeInfo typeInfo;
			
			/**
			 * Instantiates a new sub types impl.
			 * 
			 * @param whereStatement the where statement
			 * @param typeInfo the type info
			 */
			public SubTypesImpl(SLWhereByLinkCount whereStatement, SLWhereTypeInfo typeInfo) {
				this.whereStatement = whereStatement;
				this.typeInfo = typeInfo;
			}

			/* (non-Javadoc)
			 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.SubTypes#each()
			 */
			public Each each() {
				SLTypeStatementInfo whereStatementInfo = new SLTypeStatementInfo(typeInfo);
				typeInfo.setTypeStatementInfo(whereStatementInfo);
				SLConditionInfo conditionInfo = whereStatementInfo.addCondition();
				return new EachImpl(whereStatement, conditionInfo);
			}
		}
		
		/**
		 * The Class EachImpl.
		 * 
		 * @author Vitor Hugo Chagas
		 */
		public static class EachImpl implements Each {

			/** The where statement. */
			private SLWhereByLinkCount whereStatement;
			
			/** The condition info. */
			private SLConditionInfo conditionInfo;
			
			/** The outer each. */
			private Each outerEach;

			/**
			 * Instantiates a new each impl.
			 * 
			 * @param whereStatement the where statement
			 * @param conditionInfo the condition info
			 */
			public EachImpl(SLWhereByLinkCount whereStatement, SLConditionInfo conditionInfo) {
				this(whereStatement, conditionInfo, null);
			}
			
			/**
			 * Instantiates a new each impl.
			 * 
			 * @param whereStatement the where statement
			 * @param conditionInfo the condition info
			 * @param outerEach the outer each
			 */
			public EachImpl(SLWhereByLinkCount whereStatement, SLConditionInfo conditionInfo, Each outerEach) {
				this.whereStatement = whereStatement;
				this.conditionInfo = conditionInfo;
				this.outerEach = outerEach;
			}

			/* (non-Javadoc)
			 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each#property(java.lang.String)
			 */
			public Link link(String name) {
				conditionInfo.setLinkTypeName(name);
				return new LinkImpl(whereStatement, this, outerEach, conditionInfo);
			}
			
			/**
			 * The Class LinkImpl.
			 * 
			 * @author Vitor Hugo Chagas
			 */
			public static class LinkImpl implements Link {
				
				/** The where statement. */
				private SLWhereByLinkCount whereStatement;
				
				/** The each. */
				private Each each;
				
				/** The outer each. */
				private Each outerEach;
				
				/** The condition info. */
				private SLConditionInfo conditionInfo;
				
				/**
				 * Instantiates a new link impl.
				 * 
				 * @param whereStatement the where statement
				 * @param each the each
				 * @param outerEach the outer each
				 * @param conditionInfo the condition info
				 */
				public LinkImpl(SLWhereByLinkCount whereStatement, Each each, Each outerEach, SLConditionInfo conditionInfo) {
					this.whereStatement = whereStatement;
					this.each = each;
					this.outerEach = outerEach;
					this.conditionInfo = conditionInfo;
				}

				/* (non-Javadoc)
				 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Link#a()
				 */
				public Side a() {
					conditionInfo.setSide(SLSideType.A_SIDE);
					return new SideImpl(whereStatement, each, outerEach, conditionInfo);
				}
				
				/* (non-Javadoc)
				 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Link#b()
				 */
				public Side b() {
					conditionInfo.setSide(SLSideType.B_SIDE);
					return new SideImpl(whereStatement, each, outerEach, conditionInfo);
				}
				
				/**
				 * The Class SideImpl.
				 * 
				 * @author Vitor Hugo Chagas
				 */
				public static class SideImpl implements Side {

					/** The where statement. */
					private SLWhereByLinkCount whereStatement;
					
					/** The each. */
					private Each each;
					
					/** The outer each. */
					private Each outerEach;
					
					/** The condition info. */
					private SLConditionInfo conditionInfo;
					
					/**
					 * Instantiates a new side impl.
					 * 
					 * @param whereStatement the where statement
					 * @param each the each
					 * @param outerEach the outer each
					 * @param conditionInfo the condition info
					 */
					public SideImpl(SLWhereByLinkCount whereStatement, Each each, Each outerEach, SLConditionInfo conditionInfo) {
						this.whereStatement = whereStatement;
						this.each = each;
						this.outerEach = outerEach;
						this.conditionInfo = conditionInfo;
					}

					/* (non-Javadoc)
					 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Link.Side#count()
					 */
					public Count count() {
						return new CountImpl(whereStatement, each, outerEach, conditionInfo);
					}
					
					/**
					 * The Class CountImpl.
					 * 
					 * @author Vitor Hugo Chagas
					 */
					public static class CountImpl implements Count {
						
						/** The each. */
						private Each each;
						
						/** The outer each. */
						private Each outerEach;
						
						/** The where statement. */
						private SLWhereByLinkCount whereStatement;
						
						/** The condition info. */
						private SLConditionInfo conditionInfo;

						/**
						 * Instantiates a new count impl.
						 * 
						 * @param whereStatement the where statement
						 * @param each the each
						 * @param outerEach the outer each
						 * @param conditionInfo the condition info
						 */
						public CountImpl(SLWhereByLinkCount whereStatement, Each each, Each outerEach, SLConditionInfo conditionInfo) {
							this.each = each;
							this.whereStatement = whereStatement;
							this.conditionInfo = conditionInfo;
							this.outerEach = outerEach;
						}

						/* (non-Javadoc)
						 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property#not()
						 */
						public Count not() {
							conditionInfo.setRelationalNotOperator(true);
							return this;
						}

						/* (non-Javadoc)
						 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property#lesserThan()
						 */
						public Operator lesserThan() {
							conditionInfo.setRelationalOperator(SLRelationalOperatorType.LESSER_THAN);
							return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
						}
						
						/* (non-Javadoc)
						 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property#greaterThan()
						 */
						public Operator greaterThan() {
							conditionInfo.setRelationalOperator(SLRelationalOperatorType.GREATER_THAN);
							return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
						}
						
						/* (non-Javadoc)
						 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property#equalsTo()
						 */
						public Operator equalsTo() {
							conditionInfo.setRelationalOperator(SLRelationalOperatorType.EQUAL);
							return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
						}
						
						/* (non-Javadoc)
						 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property#lesserOrEqualThan()
						 */
						public Operator lesserOrEqualThan() {
							conditionInfo.setRelationalOperator(SLRelationalOperatorType.LESSER_OR_EQUAL_THAN);
							return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
						}
						
						/* (non-Javadoc)
						 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property#greaterOrEqualThan()
						 */
						public Operator greaterOrEqualThan() {
							conditionInfo.setRelationalOperator(SLRelationalOperatorType.GREATER_OR_EQUAL_THAN);
							return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
						}
						
						/* (non-Javadoc)
						 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property#contains()
						 */
						/**
						 * Contains.
						 * 
						 * @return the operator
						 */
						public Operator contains() {
							conditionInfo.setRelationalOperator(SLRelationalOperatorType.CONTAINS);
							return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
						}
						
						/* (non-Javadoc)
						 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property#startsWith()
						 */
						/**
						 * Starts with.
						 * 
						 * @return the operator
						 */
						public Operator startsWith() {
							conditionInfo.setRelationalOperator(SLRelationalOperatorType.STARTS_WITH);
							return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
						}
						
						/* (non-Javadoc)
						 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property#endsWith()
						 */
						/**
						 * Ends with.
						 * 
						 * @return the operator
						 */
						public Operator endsWith() {
							conditionInfo.setRelationalOperator(SLRelationalOperatorType.ENDS_WITH);
							return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
						}
						
						/**
						 * The Class OperatorImpl.
						 * 
						 * @author Vitor Hugo Chagas
						 */
						public static class OperatorImpl implements Operator {

							/** The each. */
							private Each each;
							
							/** The outer each. */
							private Each outerEach;
							
							/** The where statement. */
							private SLWhereByLinkCount whereStatement;
							
							/** The condition info. */
							private SLConditionInfo conditionInfo;
							
							/**
							 * Instantiates a new operator impl.
							 * 
							 * @param whereStatement the where statement
							 * @param each the each
							 * @param outerEach the outer each
							 * @param conditionInfo the condition info
							 */
							public OperatorImpl(SLWhereByLinkCount whereStatement, Each each, Each outerEach, SLConditionInfo conditionInfo) {
								this.each = each;
								this.outerEach = outerEach;
								this.whereStatement = whereStatement;
								this.conditionInfo = conditionInfo;
							}
							
							/* (non-Javadoc)
							 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator#value(java.lang.Integer)
							 */
							public Value value(Integer value) {
								conditionInfo.setValue(value);
								return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
							}
							
							/**
							 * The Class ValueImpl.
							 * 
							 * @author Vitor Hugo Chagas
							 */
							public static class ValueImpl implements Value {
						
								/** The each. */
								private Each each;
								
								/** The outer each. */
								private Each outerEach;
								
								/** The where statement. */
								private SLWhereByLinkCount whereStatement;
								
								/** The condition info. */
								private SLConditionInfo conditionInfo;

								/**
								 * Instantiates a new value impl.
								 * 
								 * @param whereStatement the where statement
								 * @param each the each
								 * @param outerEach the outer each
								 * @param conditionInfo the condition info
								 */
								public ValueImpl(SLWhereByLinkCount whereStatement, Each each, Each outerEach, SLConditionInfo conditionInfo) {
									this.each = each;
									this.outerEach = outerEach;
									this.whereStatement = whereStatement;
									this.conditionInfo = conditionInfo;
								}
								
								/* (non-Javadoc)
								 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator.Value#typeEnd()
								 */
								public SLWhereByLinkCount typeEnd() {
									return whereStatement;
								}
								
								/* (non-Javadoc)
								 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator.Value#or()
								 */
								public RelationalOperator or() {
									SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
									SLConditionInfo newConditionInfo = outerStatementInfo.addCondition(SLConditionalOperatorType.OR);
									Each newEach = new EachImpl(whereStatement, newConditionInfo, this.each);
									return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
								}
								
								/* (non-Javadoc)
								 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator.Value#and()
								 */
								public RelationalOperator and() {
									SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
									SLConditionInfo newConditionInfo = outerStatementInfo.addCondition(SLConditionalOperatorType.AND);
									Each newEach = new EachImpl(whereStatement, newConditionInfo, this.each);
									return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
								}
								
								/* (non-Javadoc)
								 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator.Value#closeBracket()
								 */
								public CloseBracket closeBracket() {
									return new CloseBracketImpl(whereStatement, outerEach, conditionInfo);
								}
								
								/**
								 * The Class RelationalOperatorImpl.
								 * 
								 * @author Vitor Hugo Chagas
								 */
								public static class RelationalOperatorImpl implements RelationalOperator {

									/** The each. */
									private Each each;
									
									/** The where statement. */
									private SLWhereByLinkCount whereStatement;
									
									/** The condition info. */
									private SLConditionInfo conditionInfo;

									/**
									 * Instantiates a new relational operator impl.
									 * 
									 * @param whereStatement the where statement
									 * @param each the each
									 * @param conditionInfo the condition info
									 */
									public RelationalOperatorImpl(SLWhereByLinkCount whereStatement, Each each, SLConditionInfo conditionInfo) {
										this.each = each;
										this.whereStatement = whereStatement;
										this.conditionInfo = conditionInfo;
									}
									
									/* (non-Javadoc)
									 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator.Value.RelationalOperator#not()
									 */
									public RelationalOperator not() {
										conditionInfo.setConditionalNotOperator(true);
										return this;
									}

									/* (non-Javadoc)
									 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator.Value.RelationalOperator#comma()
									 */
									public SLWhereByLinkCount comma() {
										return this.whereStatement;
									}
									
									/* (non-Javadoc)
									 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator.Value.RelationalOperator#each()
									 */
									public Each each() {
										return this.each;
									}

									/* (non-Javadoc)
									 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator.Value.RelationalOperator#openBracket()
									 */
									public OpenBracket openBracket() {
										SLTypeStatementInfo newStatementInfo = new SLTypeStatementInfo(conditionInfo.getTypeInfo());
										conditionInfo.setInnerStatementInfo(newStatementInfo);
										SLConditionInfo newConditionInfo = newStatementInfo.addCondition();
										Each each = new EachImpl(whereStatement, newConditionInfo, this.each);
										return new OpenBracketImpl(each);
									}

									/**
									 * The Class OpenBracketImpl.
									 * 
									 * @author Vitor Hugo Chagas
									 */
									public static class OpenBracketImpl implements OpenBracket {
										
										/** The each. */
										private Each each;
										
										/**
										 * Instantiates a new open bracket impl.
										 * 
										 * @param each the each
										 */
										public OpenBracketImpl(Each each) {
											this.each = each;
										}

										/* (non-Javadoc)
										 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator.Value.RelationalOperator.OpenBracket#each()
										 */
										public Each each() {
											return each;
										}
									}
								}
								
								/**
								 * The Class CloseBracketImpl.
								 * 
								 * @author Vitor Hugo Chagas
								 */
								public static class CloseBracketImpl implements CloseBracket {
									
									/** The where statement. */
									private SLWhereByLinkCount whereStatement;
									
									/** The outer each. */
									private Each outerEach;
									
									/** The condition info. */
									private SLConditionInfo conditionInfo;
									
									/**
									 * Instantiates a new close bracket impl.
									 * 
									 * @param whereStatement the where statement
									 * @param outerEach the outer each
									 * @param conditionInfo the condition info
									 */
									public CloseBracketImpl(SLWhereByLinkCount whereStatement, Each outerEach, SLConditionInfo conditionInfo) {
										this.whereStatement = whereStatement;
										this.outerEach = outerEach;
										this.conditionInfo = conditionInfo;
									}
									
									/* (non-Javadoc)
									 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator.Value.CloseBracket#or()
									 */
									public RelationalOperator or() {
										SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
										outerStatementInfo.addCondition(SLConditionalOperatorType.OR);
										Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
										return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
									}
									
									/* (non-Javadoc)
									 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator.Value.CloseBracket#and()
									 */
									public RelationalOperator and() {
										SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
										outerStatementInfo.addCondition(SLConditionalOperatorType.AND);
										Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
										return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
									}
									
									/* (non-Javadoc)
									 * @see org.openspotlight.graph.query.SLWhereByLinkCount.Type.Each.Property.Operator.Value.CloseBracket#typeEnd()
									 */
									public SLWhereByLinkCount typeEnd() {
										return whereStatement;
									}
								}
							}
						}
					}
				}
			}
		}
	}

}
