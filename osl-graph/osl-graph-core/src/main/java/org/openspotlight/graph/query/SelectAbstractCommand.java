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

import org.openspotlight.graph.query.info.SelectByLinkCountInfo;
import org.openspotlight.graph.query.info.SelectByLinkTypeInfo;
import org.openspotlight.graph.query.info.SelectByNodeTypeInfo;
import org.openspotlight.graph.query.info.SelectInfo;
import org.openspotlight.graph.query.info.SelectStatementInfo;

/**
 * The Class SLSelectAbstractCommand.
 * 
 * @author Vitor Hugo Chagas
 */
public abstract class SelectAbstractCommand {

    /**
     * Execute.
     */
    public abstract void execute();

    /**
     * Gets the execute command.
     * 
     * @param select the select
     * @param selectInfo the select info
     * @param commandDO the command do
     * @return the execute command
     */
    public static SelectAbstractCommand getCommand( Select select,
                                                      SelectInfo selectInfo,
                                                      SelectCommandDO commandDO ) {
        SelectAbstractCommand command = null;
        if (select instanceof SelectByNodeType) {
            SelectByNodeTypeInfo selectByNodeTypeInfo = SelectByNodeTypeInfo.class.cast(selectInfo);
            command = new SelectByNodeTypeExecuteCommand(selectByNodeTypeInfo, commandDO);
        } else if (select instanceof SelectByLinkType) {
            SelectByLinkTypeInfo selectByLinkTypeInfo = SelectByLinkTypeInfo.class.cast(selectInfo);
            command = new SelectByLinkTypeExecuteCommand(selectByLinkTypeInfo, commandDO);
        } else if (select instanceof SelectByLinkCount) {
            SelectByLinkCountInfo selectByLinkCountInfo = SelectByLinkCountInfo.class.cast(selectInfo);
            command = new SelectByLinkCountExecuteCommand(selectByLinkCountInfo, commandDO);
        } else if (select instanceof SelectStatement) {
            SelectStatementInfo selectStatementInfo = SelectStatementInfo.class.cast(selectInfo);
            if (selectStatementInfo.getByLinkInfoList().isEmpty()) {
                command = new SelectByNodeTypeCommand(selectStatementInfo, commandDO);
            } else {
                command = new SelectByLinkTypeCommand(selectStatementInfo, commandDO);
            }
        }
        return command;
    }
}
