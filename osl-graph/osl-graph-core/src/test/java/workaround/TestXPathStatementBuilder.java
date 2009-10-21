package workaround;

import static org.openspotlight.graph.query.SLConditionalOperatorType.AND;
import static org.openspotlight.graph.query.SLConditionalOperatorType.OR;
import static org.openspotlight.graph.query.SLRelationalOperatorType.CONTAINS;
import static org.openspotlight.graph.query.SLRelationalOperatorType.EQUAL;
import static org.openspotlight.graph.query.SLRelationalOperatorType.GREATER_THAN;
import static org.openspotlight.graph.query.SLRelationalOperatorType.LESSER_OR_EQUAL_THAN;
import static org.openspotlight.graph.query.SLRelationalOperatorType.STARTS_WITH;

import org.openspotlight.graph.query.SLXPathStatementBuilder;
import org.openspotlight.graph.query.SLXPathStatementBuilder.Statement;

public class TestXPathStatementBuilder {
	
	public static void main(String[] args) {
		
		SLXPathStatementBuilder builder = new SLXPathStatementBuilder();
		Statement statement = builder.getRootStatement();
		
		statement
			.openBracket()
				.condition()
					.leftOperand("caption").operator(EQUAL, true).rightOperand("java.lang")
					
			.closeBracket()
			.operator(OR)
			.openBracket()
				.operator(OR).condition()
					.leftOperand("caption").operator(EQUAL, true).rightOperand("java.lang")
				.operator(OR).condition()
					.leftOperand("caption").operator(STARTS_WITH).rightOperand("java.util")
				.operator(OR)
					.openBracket()
						.condition()
							.leftOperand("caption").operator(CONTAINS).rightOperand("String")
						.operator(OR).condition()
							.leftOperand("caption").operator(EQUAL).rightOperand("Date")
						.operator(AND, true)
							.openBracket()
								.condition()
									.leftOperand("tag").operator(GREATER_THAN).rightOperand(10)
								.operator(OR).condition()
									.leftOperand("tag").operator(LESSER_OR_EQUAL_THAN).rightOperand(8)
							.closeBracket()
					.closeBracket()
			.closeBracket();
		
		String formattedString = builder.getXPath();
		System.out.println(formattedString);
		System.out.println();
		System.out.println(builder.getXPathString());
	}
}
