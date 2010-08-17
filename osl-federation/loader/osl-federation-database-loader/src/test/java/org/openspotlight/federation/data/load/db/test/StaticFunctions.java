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

package org.openspotlight.federation.data.load.db.test;

/**
 * This class is used to store static functions to be used by java functions and procedures.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class StaticFunctions {
    /**
     * static variable to mark is the procedure was called. H2 doesn't support out parameters.
     */
    public static boolean procedureCalled = false;

    /**
     * This dummy method just set a flag to true. H2 doesn't support out parameters.
     * 
     * @param i1
     * @param i2
     */
    public static void anotherFlagProcedure( final int i1,
                                             final int i2 ) {
        procedureCalled = true;
    }

    /**
     * This dummy method just set a flag to true. H2 doesn't support out parameters.
     * 
     * @param i1
     * @param i2
     */
    public static void flagProcedure( final int i1,
                                      final int i2 ) {
        procedureCalled = true;
    }

    /**
     * This method increments its parameter.
     * 
     * @param i
     * @return the parameter plus one
     */
    public static int increment( final int i ) {
        return i + 1;
    }

    /**
     * This method is used on derby tests. It sum the two first parameters and store its results on 3rd parameter.
     * 
     * @param i1
     * @param i2
     * @param result
     */
    public static void sum( final int i1,
                            final int i2,
                            final int[] result ) {
        result[0] = i1 + i2;
    }

}
