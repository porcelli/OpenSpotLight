/**
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
 * The Interface SLSelectStatement.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SelectStatement extends Select {

    /**
     * The Interface AllTypes.
     * 
     * @author Vitor Hugo Chagas
     */
    public static interface AllTypes {

        /**
         * By link.
         * 
         * @param name the name
         * @return the by link
         */
        public ByLink byLink(String name);

        /**
         * On where.
         * 
         * @return the all types
         */
        public AllTypes onWhere();

        /**
         * Select end.
         * 
         * @return the end
         */
        public End selectEnd();
    }

    /**
     * The Interface ByLink.
     * 
     * @author Vitor Hugo Chagas
     */
    public static interface ByLink {

        /**
         * A.
         * 
         * @return the by link
         */
        public ByLink a();

        /**
         * Any.
         * 
         * @return the by link
         */
        public ByLink any();

        /**
         * B.
         * 
         * @return the by link
         */
        public ByLink b();

        /**
         * Comma.
         * 
         * @return the sL select statement
         */
        public SelectStatement comma();

        /**
         * Select end.
         * 
         * @return the end
         */
        public End selectEnd();
    }

    /**
     * The Interface End.
     * 
     * @author Vitor Hugo Chagas
     */
    public static interface End extends SelectFacade {

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
         * @return the end
         */
        public End executeXTimes(Integer x);

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
        public End limit(Integer size);

        /**
         * Limit.
         * 
         * @param size the size
         * @param offset the offset
         * @return the end
         */
        public End limit(Integer size,
                          Integer offset);

        /**
         * Order by.
         * 
         * @return the sL order by statement
         */
        public OrderByStatement orderBy();

        /**
         * Where.
         * 
         * @return the sL where statement
         */
        public WhereStatement where();
    }

    /**
     * The Interface Type.
     * 
     * @author Vitor Hugo Chagas
     */
    public static interface Type {

        /**
         * By link.
         * 
         * @param name the name
         * @return the by link
         */
        public ByLink byLink(String name);

        /**
         * Comma.
         * 
         * @return the sL select statement
         */
        public SelectStatement comma();

        /**
         * Select end.
         * 
         * @return the end
         */
        public End selectEnd();

        /**
         * Sub types.
         * 
         * @return the type
         */
        public Type subTypes();
    }

    /**
     * All types.
     * 
     * @return the all types
     */
    public AllTypes allTypes();

    /**
     * By link.
     * 
     * @param typeName the type name
     * @return the by link
     */
    public ByLink byLink(String typeName);

    /**
     * End.
     * 
     * @return the end
     */
    public End end();

    /**
     * Type.
     * 
     * @param typeName the type name
     * @return the type
     */
    public Type type(String typeName);
}
