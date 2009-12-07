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


/**
 * The Interface SLWhereByLinkType.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SLWhereByLinkType {

	/**
	 * Link type.
	 * 
	 * @param typeName the type name
	 * 
	 * @return the link type
	 */
	public LinkType linkType(String typeName);
	
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
	public static interface End {
		
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
	 * The Interface LinkType.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static interface LinkType {
		
		/**
		 * Each.
		 * 
		 * @return the each
		 */
		public Each each();
		
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
						 * Link type end.
						 * 
						 * @return the sL where by link type
						 */
						public SLWhereByLinkType linkTypeEnd();
						
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
							 * Comma.
							 * 
							 * @return the sL where by link type
							 */
							public SLWhereByLinkType comma();
							
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
							 * @return the sL where by link type
							 */
							public SLWhereByLinkType typeEnd();
						}
					}
				}
			}
		}
	}
}
