package org.openspotlight.bundle.db.tool.template;

import java.io.File;
import java.io.FileWriter;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.junit.Test;
import org.openspotlight.bundle.db.metamodel.node.Catalog;
import org.openspotlight.bundle.db.metamodel.node.Column;
import org.openspotlight.bundle.db.metamodel.node.DataType;
import org.openspotlight.bundle.db.metamodel.node.Database;
import org.openspotlight.bundle.db.metamodel.node.DatabaseConstraintForeignKey;
import org.openspotlight.bundle.db.metamodel.node.DatabaseConstraintPrimaryKey;
import org.openspotlight.bundle.db.metamodel.node.Schema;
import org.openspotlight.bundle.db.metamodel.node.Server;
import org.openspotlight.bundle.db.metamodel.node.TableView;
import org.openspotlight.bundle.db.metamodel.node.TableViewTable;
import org.openspotlight.bundle.db.metamodel.node.TableViewView;
import org.openspotlight.federation.domain.DatabaseType;

import dynamo.string.StringTool;

public class NodeInheritanceTemplateGeneration {

	Class<?>[] databaseNodeTypes = new Class<?>[] { Catalog.class,
			Column.class, DataType.class, Database.class,
			DatabaseConstraintForeignKey.class,
			DatabaseConstraintPrimaryKey.class, Schema.class, Server.class,
			TableView.class, TableViewTable.class, TableViewView.class };

	@Test
	public void shouldCreateNodeInheritanceFiles() throws Exception {
		final String dir = "target/test-data/NodeInheritanceTemplateGeneration/generated";
		new File(dir).mkdirs();

		final StringTemplateGroup group = new StringTemplateGroup("myGroup",
				"src/test/resources/template/sourcecode",
				DefaultTemplateLexer.class);

		final StringTool t = new StringTool();
		for (final DatabaseType dbType : DatabaseType.values()) {
			for (final Class<?> nodeType : databaseNodeTypes) {
				final String prefix = t.camelCase(dbType.name().toLowerCase());
				final StringTemplate template = group
						.getInstanceOf("DatabaseNode");
				template.setAttribute("dbName", prefix);
				final String nodeTypeName = nodeType.getSimpleName();
				template.setAttribute("nodeName", nodeTypeName);
				final FileWriter writer = new FileWriter(dir + "/" + prefix
						+ nodeTypeName + ".java");
				writer.write(template.toString());
				writer.flush();
				writer.close();
			}
		}
	}

}
