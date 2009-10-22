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
 * OpenSpotLight - Plataforma de Governan�a de TI de C�digo Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribui��o de direito autoral declarada e atribu�da pelo autor.
 * Todas as contribui��es de terceiros est�o distribu�das sob licen�a da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo sob os 
 * termos da Licen�a P�blica Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU ADEQUA��O A UMA
 * FINALIDADE ESPEC�FICA. Consulte a Licen�a P�blica Geral Menor do GNU para mais detalhes.  
 * 
 * Voc� deve ter recebido uma c�pia da Licen�a P�blica Geral Menor do GNU junto com este
 * programa; se n�o, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.graph.query;



/**
 * The Interface SLWhereByNodeType.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SLWhereByNodeType {
	
	/**
	 * Type.
	 * 
	 * @param typeName the type name
	 * 
	 * @return the type
	 */
	public Type type(String typeName);
	
	/**
	 * Where end.
	 * 
	 * @return the end
	 */
	public End whereEnd();
	
	/**
	 * The Interface End.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static interface End extends SLSelectFacade {
		
		/**
		 * Order by.
		 * 
		 * @return the sL order by statement
		 */
		public SLOrderByStatement orderBy();
		
		/**
		 * Keep result.
		 * 
		 * @return the end
		 */
		public End keepResult();
		
        /**
         * Limit.
         * 
         * @param size the size
         * @return the end
         */
        public End limit( Integer size );

        /**
         * Limit.
         * 
         * @param size the size
         * @param offset the offset
         * @return the end
         */
        public End limit( Integer size,
                          Integer offset );

		/**
		 * Execute x times.
		 * 
		 * @return the end
		 */
		public End executeXTimes();
		
		/**
		 * Execute x times.
		 * 
		 * @param x the x
		 * 
		 * @return the end
		 */
		public End executeXTimes(int x);
	}
	
	/**
	 * The Interface Type.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static interface Type {
		
		/**
		 * Sub types.
		 * 
		 * @return the sub types
		 */
		public SubTypes subTypes();
		
		/**
		 * Each.
		 * 
		 * @return the each
		 */
		public Each each();
		
		/**
		 * The Interface SubTypes.
		 * 
		 * @author Vitor Hugo Chagas
		 */
		public static interface SubTypes {
			
			/**
			 * Each.
			 * 
			 * @return the each
			 */
			public Each each();
		}
		
		/**
		 * The Interface Each.
		 * 
		 * @author Vitor Hugo Chagas
		 */
		public static interface Each {
			
			/**
			 * Property.
			 * 
			 * @param name the name
			 * 
			 * @return the property
			 */
			public Property property(String name);
			
			/**
			 * Link.
			 * 
			 * @param name the name
			 * 
			 * @return the link
			 */
			public Link link(String name);
			
			/**
			 * The Interface Link.
			 * 
			 * @author Vitor Hugo Chagas
			 */
			public static interface Link {
				
				/**
				 * A.
				 * 
				 * @return the side
				 */
				public Side a();
				
				/**
				 * B.
				 * 
				 * @return the side
				 */
				public Side b(); 
				
				/**
				 * The Interface Side.
				 * 
				 * @author Vitor Hugo Chagas
				 */
				public static interface Side {
					
					/**
					 * Count.
					 * 
					 * @return the count
					 */
					public Count count();
					
					/**
					 * The Interface Count.
					 * 
					 * @author Vitor Hugo Chagas
					 */
					public static interface Count {
						
						/**
						 * Not.
						 * 
						 * @return the count
						 */
						public Count not();
						
						/**
						 * Lesser than.
						 * 
						 * @return the operator
						 */
						public Operator lesserThan();
						
						/**
						 * Greater than.
						 * 
						 * @return the operator
						 */
						public Operator greaterThan();
						
						/**
						 * Equals to.
						 * 
						 * @return the operator
						 */
						public Operator equalsTo();
						
						/**
						 * Lesser or equal than.
						 * 
						 * @return the operator
						 */
						public Operator lesserOrEqualThan();
						
						/**
						 * Greater or equal than.
						 * 
						 * @return the operator
						 */
						public Operator greaterOrEqualThan();
						
						/**
						 * The Interface Operator.
						 * 
						 * @author Vitor Hugo Chagas
						 */
						public static interface Operator {
							
							/**
							 * Value.
							 * 
							 * @param value the value
							 * 
							 * @return the value
							 */
							public Value value(Integer value);
							
							/**
							 * The Interface Value.
							 * 
							 * @author Vitor Hugo Chagas
							 */
							public static interface Value {
								
								/**
								 * Type end.
								 * 
								 * @return the sL where by link count
								 */
								public SLWhereByNodeType typeEnd();
								
								/**
								 * Or.
								 * 
								 * @return the relational operator
								 */
								public RelationalOperator or();
								
								/**
								 * And.
								 * 
								 * @return the relational operator
								 */
								public RelationalOperator and();
								
								/**
								 * Close bracket.
								 * 
								 * @return the close bracket
								 */
								public CloseBracket closeBracket();
								
								/**
								 * The Interface RelationalOperator.
								 * 
								 * @author Vitor Hugo Chagas
								 */
								public static interface RelationalOperator {
									
									/**
									 * Not.
									 * 
									 * @return the relational operator
									 */
									public RelationalOperator not();
									
									/**
									 * Comma.
									 * 
									 * @return the sL where by link count
									 */
									public SLWhereByNodeType comma();
									
									/**
									 * Each.
									 * 
									 * @return the each
									 */
									public Each each();
									
									/**
									 * Open bracket.
									 * 
									 * @return the open bracket
									 */
									public OpenBracket openBracket();
									
									/**
									 * The Interface OpenBracket.
									 * 
									 * @author Vitor Hugo Chagas
									 */
									public static interface OpenBracket {
										
										/**
										 * Each.
										 * 
										 * @return the each
										 */
										public Each each();
									}
								}
								
								/**
								 * The Interface OpenBracket.
								 * 
								 * @author Vitor Hugo Chagas
								 */
								public static interface OpenBracket {
									
									/**
									 * Each.
									 * 
									 * @return the each
									 */
									public Each each();
									
									/**
									 * Close bracket.
									 * 
									 * @return the close bracket
									 */
									public CloseBracket closeBracket();
								}
								
								/**
								 * The Interface CloseBracket.
								 * 
								 * @author Vitor Hugo Chagas
								 */
								public static interface CloseBracket {
									
									/**
									 * Or.
									 * 
									 * @return the relational operator
									 */
									public RelationalOperator or();
									
									/**
									 * And.
									 * 
									 * @return the relational operator
									 */
									public RelationalOperator and();
									
									/**
									 * Type end.
									 * 
									 * @return the sL where by link count
									 */
									public SLWhereByNodeType typeEnd();
								}
							}
						}
					}
				}
			}
			
			/**
			 * The Interface Property.
			 * 
			 * @author Vitor Hugo Chagas
			 */
			public static interface Property {
				
				/**
				 * Not.
				 * 
				 * @return the property
				 */
				public Property not();
				
				/**
				 * Lesser than.
				 * 
				 * @return the operator
				 */
				public Operator lesserThan();
				
				/**
				 * Greater than.
				 * 
				 * @return the operator
				 */
				public Operator greaterThan();
				
				/**
				 * Equals to.
				 * 
				 * @return the operator
				 */
				public Operator equalsTo();
				
				/**
				 * Lesser or equal than.
				 * 
				 * @return the operator
				 */
				public Operator lesserOrEqualThan();
				
				/**
				 * Greater or equal than.
				 * 
				 * @return the operator
				 */
				public Operator greaterOrEqualThan();
				
				/**
				 * Contains.
				 * 
				 * @return the operator
				 */
				public Operator contains();
				
				/**
				 * Starts with.
				 * 
				 * @return the operator
				 */
				public Operator startsWith();
				
				/**
				 * Ends with.
				 * 
				 * @return the operator
				 */
				public Operator endsWith();
				
				/**
				 * The Interface Operator.
				 * 
				 * @author Vitor Hugo Chagas
				 */
				public static interface Operator {
					
					/**
					 * Value.
					 * 
					 * @param value the value
					 * 
					 * @return the value
					 */
					public Value value(String value);
					
					/**
					 * Value.
					 * 
					 * @param value the value
					 * 
					 * @return the value
					 */
					public Value value(Integer value);
					
					/**
					 * Value.
					 * 
					 * @param value the value
					 * 
					 * @return the value
					 */
					public Value value(Long value);
					
					/**
					 * Value.
					 * 
					 * @param value the value
					 * 
					 * @return the value
					 */
					public Value value(Float value);
					
					/**
					 * Value.
					 * 
					 * @param value the value
					 * 
					 * @return the value
					 */
					public Value value(Double value);
					
					/**
					 * Value.
					 * 
					 * @param value the value
					 * 
					 * @return the value
					 */
					public Value value(Boolean value);

					/**
					 * The Interface Value.
					 * 
					 * @author Vitor Hugo Chagas
					 */
					public static interface Value {
						
						/**
						 * Type end.
						 * 
						 * @return the sL where by node type
						 */
						public SLWhereByNodeType typeEnd();
						
						/**
						 * Or.
						 * 
						 * @return the relational operator
						 */
						public RelationalOperator or();
						
						/**
						 * And.
						 * 
						 * @return the relational operator
						 */
						public RelationalOperator and();
						
						/**
						 * Close bracket.
						 * 
						 * @return the close bracket
						 */
						public CloseBracket closeBracket();
						
						/**
						 * The Interface RelationalOperator.
						 * 
						 * @author Vitor Hugo Chagas
						 */
						public static interface RelationalOperator {
							
							/**
							 * Not.
							 * 
							 * @return the relational operator
							 */
							public RelationalOperator not();
							
							/**
							 * Comma.
							 * 
							 * @return the sL where by node type
							 */
							public SLWhereByNodeType comma();
							
							/**
							 * Each.
							 * 
							 * @return the each
							 */
							public Each each();
							
							/**
							 * Open bracket.
							 * 
							 * @return the open bracket
							 */
							public OpenBracket openBracket();
							
							/**
							 * The Interface OpenBracket.
							 * 
							 * @author Vitor Hugo Chagas
							 */
							public static interface OpenBracket {
								
								/**
								 * Each.
								 * 
								 * @return the each
								 */
								public Each each();
							}
						}
						
						/**
						 * The Interface OpenBracket.
						 * 
						 * @author Vitor Hugo Chagas
						 */
						public static interface OpenBracket {
							
							/**
							 * Each.
							 * 
							 * @return the each
							 */
							public Each each();
							
							/**
							 * Close bracket.
							 * 
							 * @return the close bracket
							 */
							public CloseBracket closeBracket();
						}
						
						/**
						 * The Interface CloseBracket.
						 * 
						 * @author Vitor Hugo Chagas
						 */
						public static interface CloseBracket {
							
							/**
							 * Or.
							 * 
							 * @return the relational operator
							 */
							public RelationalOperator or();
							
							/**
							 * And.
							 * 
							 * @return the relational operator
							 */
							public RelationalOperator and();
							
							/**
							 * Type end.
							 * 
							 * @return the sL where by node type
							 */
							public SLWhereByNodeType typeEnd();
						}
					}
				}
			}
			
		}
		
	}
	
}