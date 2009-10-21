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

import static org.openspotlight.common.util.StringBuilderUtil.append;
import static org.openspotlight.common.util.StringBuilderUtil.appendIfNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.graph.query.SLXPathStatementBuilder.Statement.Condition;

/**
 * The Class SLXPathStatementBuilder.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLXPathStatementBuilder {
	
	/** The path. */
	private String path;
	
	/** The order by. */
	private String orderBy;
	
	/** The root statement. */
	private Statement rootStatement;
	
	/**
	 * Instantiates a new sLX path statement builder.
	 */
	public SLXPathStatementBuilder() {}
	
	/**
	 * Instantiates a new sLX path statement builder.
	 * 
	 * @param path the path
	 */
	public SLXPathStatementBuilder(String path) {
		this.path = path;
	}
	
	/**
	 * Sets the path.
	 * 
	 * @param path the new path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Sets the order by.
	 * 
	 * @param orderBy the new order by
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	/**
	 * Gets the root statement.
	 * 
	 * @return the root statement
	 */
	public Statement getRootStatement() {
		if (rootStatement == null) {
			rootStatement = new Statement(null);
		}
		return rootStatement;
	}
	
	/**
	 * Gets the x path.
	 * 
	 * @return the x path
	 */
	public String getXPath() {
		StringBuilder buffer = new StringBuilder();
		StringBuilder statementBuffer = new StringBuilder();
		printStatement(statementBuffer, rootStatement, 0);
		append(buffer, path, "\n[\n", statementBuffer, "]");
		appendIfNotNull(buffer, orderBy, "\norder by @", orderBy);
		return buffer.toString();
	}
	
	/**
	 * Gets the x path string.
	 * 
	 * @return the x path string
	 */
	public String getXPathString() {
		String xpath = getXPath();
		xpath = StringUtils.replace(xpath, "\t", "");
		xpath = StringUtils.replace(xpath, "\n", " ");
		return xpath; 
	}
	
	/**
	 * Prints the statement.
	 * 
	 * @param buffer the buffer
	 * @param statement the statement
	 * @param identLevel the ident level
	 */
	private void printStatement(StringBuilder buffer, Statement statement, int identLevel) {
		validate(rootStatement);
		StringBuilder statementBuffer = new StringBuilder();
		String tabs0 = StringUtils.repeat("\t", identLevel);
		String tabs1 = StringUtils.repeat("\t", identLevel + 1);
		for (Condition condition : statement.conditions) {
			append(statementBuffer, tabs0);
			if (condition.innerStatement == null) {
				if (condition.conditionalOperator != null) {
					if (condition.conditionalOperator != null) {
						append(statementBuffer, condition.conditionalOperator.symbol().toLowerCase(), ' ');	
					}
					if (condition.conditionalOperatorApplyNot) {
						append(statementBuffer, "not(\n", tabs1);
					}
				}
				if (condition.inexistent) {
					append(statementBuffer, "not(@", condition.leftOperandValue, ")\n");
				}
				else {
					String expression = condition.relationalOperator.xPathExpression(condition.leftOperandValue, condition.rightOperandValue, condition.relationalOperatorApplyNot);
					append(statementBuffer, expression, '\n');
					if (condition.conditionalOperator != null && condition.conditionalOperatorApplyNot) {
						append(statementBuffer, tabs0, ")\n");
					}
				}
			}
			else {
				if (condition.conditionalOperator != null) {
					append(statementBuffer, condition.conditionalOperator.symbol().toLowerCase(), ' ');	
				}
				if (condition.conditionalOperatorApplyNot) {
					append(statementBuffer, "not(\n");
				}
				else {
					append(statementBuffer, "(\n");
				}
				printStatement(statementBuffer, condition.innerStatement, identLevel + 1);
				append(statementBuffer, tabs0, ")\n");
			}
		}
		append(buffer, statementBuffer);
	}
	
	/**
	 * Validate.
	 * 
	 * @param statement the statement
	 */
	private void validate(Statement statement) {
		if (statement != null) {
			for (Condition condition : statement.conditions) {
				if (!condition.closed) {
					RuntimeException e = new SLRuntimeException("All conditions must be closed.");
					e.setStackTrace(condition.stackTrace);
					throw e;
				}
				validate(condition.innerStatement);
			}
		}
	}
	
	/**
	 * The Class Statement.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	public static class Statement {
		
		/** The parent. */
		private Condition parent;
		
		/** The conditions. */
		private List<Condition> conditions = new ArrayList<Condition>();
		
		/**
		 * Instantiates a new statement.
		 * 
		 * @param parent the parent
		 */
		private Statement(Condition parent) {
			this.parent = parent;
		}
		
		/**
		 * Gets the condition count.
		 * 
		 * @return the condition count
		 */
		public int getConditionCount() {
			return conditions.size();
		}
		
		/**
		 * Condition.
		 * 
		 * @return the condition
		 */
		public Condition condition() {
			Condition condition = new Condition(this);
			conditions.add(condition);
			return condition;
		}

		/**
		 * Operator.
		 * 
		 * @param conditionalOperator the conditional operator
		 * 
		 * @return the conditional operator
		 */
		public ConditionalOperator operator(SLConditionalOperatorType conditionalOperator) {
			return operator(conditionalOperator, false);
		}

		/**
		 * Operator.
		 * 
		 * @param conditionalOperator the conditional operator
		 * @param applyNot the apply not
		 * 
		 * @return the conditional operator
		 */
		public ConditionalOperator operator(SLConditionalOperatorType conditionalOperator, boolean applyNot) {
			return new ConditionalOperator(this, conditionalOperator, applyNot);
		}
		
		/**
		 * Open bracket.
		 * 
		 * @return the statement
		 */
		public Statement openBracket() {
			Condition condition = new Condition(this);
			this.conditions.add(condition);
			Statement innerStatement = new Statement(condition);
			condition.innerStatement = innerStatement;
			return innerStatement;
		}
		
		/**
		 * Close bracket.
		 * 
		 * @return the statement
		 */
		public Statement closeBracket() {
			parent.closed = true;
			return parent.outerStatement;
		}
		
		/**
		 * The Class ConditionalOperator.
		 * 
		 * @author Vitor Hugo Chagas
		 */
		public static class ConditionalOperator {
			
			/** The statement. */
			private Statement statement;
			
			/** The conditional operator. */
			private SLConditionalOperatorType conditionalOperator;
			
			/** The apply not. */
			private boolean applyNot;
			
			/**
			 * Instantiates a new conditional operator.
			 * 
			 * @param statement the statement
			 * @param conditionalOperator the conditional operator
			 * @param applyNot the apply not
			 */
			private ConditionalOperator(Statement statement, SLConditionalOperatorType conditionalOperator, boolean applyNot) {
				this.statement = statement;
				this.conditionalOperator = conditionalOperator;
				this.applyNot = applyNot;
			}
			
			/**
			 * Condition.
			 * 
			 * @return the condition
			 */
			public Condition condition() {
				Condition condition = new Condition(statement);
				condition.conditionalOperator = conditionalOperator;
				condition.conditionalOperatorApplyNot = applyNot;
				condition.outerStatement = statement;
				statement.conditions.add(condition);
				return condition;
			}
			
			/**
			 * Open bracket.
			 * 
			 * @return the statement
			 */
			public Statement openBracket() {
				Condition condition = new Condition(statement);
				condition.conditionalOperator = conditionalOperator;
				condition.conditionalOperatorApplyNot = applyNot;
				statement.conditions.add(condition);
				Statement innerStatement = new Statement(condition);
				condition.innerStatement = innerStatement;
				return innerStatement;
			}
		}
		
		/**
		 * The Class Condition.
		 * 
		 * @author Vitor Hugo Chagas
		 */
		public static class Condition {
			
			/** The closed. */
			private boolean closed = false;
			
			/** The inexistent. */
			private boolean inexistent = false;
			
			/** The outer statement. */
			private Statement outerStatement;
			
			/** The inner statement. */
			private Statement innerStatement;
			
			/** The left operand value. */
			private Object leftOperandValue;
			
			/** The right operand value. */
			private Object rightOperandValue;
			
			/** The relational operator. */
			private SLRelationalOperatorType relationalOperator;
			
			/** The conditional operator. */
			private SLConditionalOperatorType conditionalOperator;
			
			/** The relational operator apply not. */
			private boolean relationalOperatorApplyNot;
			
			/** The conditional operator apply not. */
			private boolean conditionalOperatorApplyNot;
			
			/** The stack trace. */
			private StackTraceElement[] stackTrace; 

			/**
			 * Instantiates a new condition.
			 * 
			 * @param outerStatement the outer statement
			 */
			private Condition(Statement outerStatement) {
				stackTrace = Thread.currentThread().getStackTrace();
				this.outerStatement = outerStatement;
			}

			/**
			 * Left operand.
			 * 
			 * @param value the value
			 * 
			 * @return the left operand
			 */
			public LeftOperand leftOperand(Object value) {
				this.leftOperandValue = value;
				return new LeftOperand(this);
			}
			
			/**
			 * The Class LeftOperand.
			 * 
			 * @author Vitor Hugo Chagas
			 */
			public static class LeftOperand {
				
				/** The condition. */
				private Condition condition; 
				
				/**
				 * Instantiates a new left operand.
				 * 
				 * @param condition the condition
				 */
				private LeftOperand(Condition condition) {
					this.condition = condition;
				}
				
				/**
				 * Inexistent.
				 * 
				 * @return the statement
				 */
				public Statement inexistent() {
					condition.closed = true;
					condition.inexistent = true;
					return condition.outerStatement;
				}
				
				/**
				 * Operator.
				 * 
				 * @param relationalOperator the relational operator
				 * 
				 * @return the relational operator
				 */
				public RelationalOperator operator(SLRelationalOperatorType relationalOperator) {
					return operator(relationalOperator, false);
				}
				
				/**
				 * Operator.
				 * 
				 * @param relationalOperator the relational operator
				 * @param applyNot the apply not
				 * 
				 * @return the relational operator
				 */
				public RelationalOperator operator(SLRelationalOperatorType relationalOperator, boolean applyNot) {
					condition.relationalOperator = relationalOperator;
					condition.relationalOperatorApplyNot = applyNot;
					return new RelationalOperator(condition);
				}
				
				/**
				 * The Class RelationalOperator.
				 * 
				 * @author Vitor Hugo Chagas
				 */
				public static class RelationalOperator {
					
					/** The condition. */
					private Condition condition;
					
					/**
					 * Instantiates a new relational operator.
					 * 
					 * @param condition the condition
					 */
					private RelationalOperator(Condition condition) {
						this.condition = condition;
					}
					
					/**
					 * Right operand.
					 * 
					 * @param value the value
					 * 
					 * @return the statement
					 */
					public Statement rightOperand(Object value) {
						condition.rightOperandValue = value;
						condition.closed = true;
						return condition.outerStatement;
					}
				}
			}
		}
	}
}
