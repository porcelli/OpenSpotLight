package org.openspotlight.graph.query;

import static org.openspotlight.common.util.StringBuilderUtil.append;
import static org.openspotlight.common.util.StringBuilderUtil.appendIfNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.graph.query.SLXPathStatementBuilder.Statement.Condition;

public class SLXPathStatementBuilder {
	
	private String path;
	private String orderBy;
	private Statement rootStatement;
	
	public SLXPathStatementBuilder() {}
	
	public SLXPathStatementBuilder(String path) {
		this.path = path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	public Statement getRootStatement() {
		if (rootStatement == null) {
			rootStatement = new Statement(null);
		}
		return rootStatement;
	}
	
	public String getXPath() {
		StringBuilder buffer = new StringBuilder();
		StringBuilder statementBuffer = new StringBuilder();
		printStatement(statementBuffer, rootStatement, 0);
		append(buffer, path, "\n[\n", statementBuffer, "]");
		appendIfNotNull(buffer, orderBy, "\norder by @", orderBy);
		return buffer.toString();
	}
	
	public String getXPathString() {
		String xpath = getXPath();
		xpath = StringUtils.replace(xpath, "\t", "");
		xpath = StringUtils.replace(xpath, "\n", " ");
		return xpath; 
	}
	
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
				String expression = condition.relationalOperator.xPathExpression(condition.leftOperandValue, condition.rightOperandValue, condition.relationalOperatorApplyNot);
				append(statementBuffer, expression, '\n');
				if (condition.conditionalOperator != null && condition.conditionalOperatorApplyNot) {
					append(statementBuffer, tabs0, ")\n");
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
	
	public static class Statement {
		
		private Condition parent;
		private List<Condition> conditions = new ArrayList<Condition>();
		
		private Statement(Condition parent) {
			this.parent = parent;
		}
		
		public int getConditionCount() {
			return conditions.size();
		}
		
		public Condition condition() {
			Condition condition = new Condition(this);
			conditions.add(condition);
			return condition;
		}

		public ConditionalOperator operator(SLConditionalOperatorType conditionalOperator) {
			return operator(conditionalOperator, false);
		}

		public ConditionalOperator operator(SLConditionalOperatorType conditionalOperator, boolean applyNot) {
			return new ConditionalOperator(this, conditionalOperator, applyNot);
		}
		
		public Statement openBracket() {
			Condition condition = new Condition(this);
			this.conditions.add(condition);
			Statement innerStatement = new Statement(condition);
			condition.innerStatement = innerStatement;
			return innerStatement;
		}
		
		public Statement closeBracket() {
			parent.closed = true;
			return parent.outerStatement;
		}
		
		public static class ConditionalOperator {
			
			private Statement statement;
			private SLConditionalOperatorType conditionalOperator;
			private boolean applyNot;
			
			private ConditionalOperator(Statement statement, SLConditionalOperatorType conditionalOperator, boolean applyNot) {
				this.statement = statement;
				this.conditionalOperator = conditionalOperator;
				this.applyNot = applyNot;
			}
			
			public Condition condition() {
				Condition condition = new Condition(statement);
				condition.conditionalOperator = conditionalOperator;
				condition.conditionalOperatorApplyNot = applyNot;
				condition.outerStatement = statement;
				statement.conditions.add(condition);
				return condition;
			}
			
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
		
		public static class Condition {
			
			private boolean closed = false;
			private Statement outerStatement;
			private Statement innerStatement;
			private Object leftOperandValue;
			private Object rightOperandValue;
			private SLRelationalOperatorType relationalOperator;
			private SLConditionalOperatorType conditionalOperator;
			private boolean relationalOperatorApplyNot;
			private boolean conditionalOperatorApplyNot;
			private StackTraceElement[] stackTrace; 

			private Condition(Statement outerStatement) {
				stackTrace = Thread.currentThread().getStackTrace();
				this.outerStatement = outerStatement;
			}

			public LeftOperand leftOperand(Object value) {
				this.leftOperandValue = value;
				return new LeftOperand(this);
			}
			
			public static class LeftOperand {
				
				private Condition condition; 
				
				private LeftOperand(Condition condition) {
					this.condition = condition;
				}
				
				public RelationalOperator operator(SLRelationalOperatorType relationalOperator) {
					return operator(relationalOperator, false);
				}
				
				public RelationalOperator operator(SLRelationalOperatorType relationalOperator, boolean applyNot) {
					condition.relationalOperator = relationalOperator;
					condition.relationalOperatorApplyNot = applyNot;
					return new RelationalOperator(condition);
				}
				
				public static class RelationalOperator {
					
					private Condition condition;
					
					private RelationalOperator(Condition condition) {
						this.condition = condition;
					}
					
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
