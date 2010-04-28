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
import org.openspotlight.federation.domain.artifact.db.DatabaseType;

import dynamo.string.StringTool;

public class NodeInheritanceTemplateGeneration {

    Class<?>[] databaseNodeTypes = new Class<?>[] {Catalog.class, Column.class, DataType.class, Database.class,
        DatabaseConstraintForeignKey.class, DatabaseConstraintPrimaryKey.class, Schema.class, Server.class, TableView.class,
        TableViewTable.class, TableViewView.class};

    @Test
    public void shouldCreateNodeInheritanceFiles() throws Exception {
        final String dir = "target/test-data/NodeInheritanceTemplateGeneration/generated";
        new File(dir).mkdirs();
        new File(dir + "/wrapped").mkdirs();

        final StringTemplateGroup group = new StringTemplateGroup("myGroup", "src/test/resources/template/sourcecode",
                                                                  DefaultTemplateLexer.class);
        final StringTool t = new StringTool();
        for (final DatabaseType dbType : DatabaseType.values()) {
            final String prefix = t.camelCase(dbType.name().toLowerCase());

            final StringTemplate wrapTemplate = group.getInstanceOf("WrappedType");
            wrapTemplate.setAttribute("dbName", prefix);
            final FileWriter wrapWriter = new FileWriter(dir + "/wrapped/" + prefix + "WrappedType.java");
            wrapWriter.write(wrapTemplate.toString());
            wrapWriter.flush();
            wrapWriter.close();

            for (final Class<?> nodeType : databaseNodeTypes) {
                final StringTemplate template = group.getInstanceOf("DatabaseNode");
                template.setAttribute("dbName", prefix);
                final String nodeTypeName = nodeType.getSimpleName();
                template.setAttribute("nodeName", nodeTypeName);
                final FileWriter writer = new FileWriter(dir + "/" + prefix + nodeTypeName + ".java");
                writer.write(template.toString());
                writer.flush();
                writer.close();
            }
        }
    }

}
