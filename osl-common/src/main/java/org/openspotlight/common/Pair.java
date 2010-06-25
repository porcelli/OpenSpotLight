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

package org.openspotlight.common;

/**
 * This is a simple class to store a pair of objects.
 *
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * @param <K1>
 * @param <K2>
 */
public class Pair<K1, K2> {

    public static enum PairEqualsMode {
        K1, K2, BOTH
    }


    /**
     * static factory method
     *
     * @param <K1>
     * @param <K2>
     * @param k1
     * @param k2
     * @return
     */
    public static <K1, K2> Pair<K1, K2> newPair(K1 k1,
                                                K2 k2) {
        return new Pair<K1, K2>(k1, k2, PairEqualsMode.BOTH);
    }

    /**
     * static factory method
     *
     * @param <K1>
     * @param <K2>
     * @param k1
     * @param k2
     * @return
     */
    public static <K1, K2> Pair<K1, K2> newPair(K1 k1,
                                                K2 k2, PairEqualsMode equalsMode) {
        return new Pair<K1, K2>(k1, k2, equalsMode);
    }

    /**
     * First item.
     */
    private final K1 k1;
    /**
     * Second item.
     */
    private final K2 k2;

    private final PairEqualsMode equalsMode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (PairEqualsMode.BOTH.equals(equalsMode) || PairEqualsMode.K1.equals(equalsMode))
            if (k1 != null ? !k1.equals(pair.k1) : pair.k1 != null) return false;
        if (PairEqualsMode.BOTH.equals(equalsMode) || PairEqualsMode.K2.equals(equalsMode))
            if (k2 != null ? !k2.equals(pair.k2) : pair.k2 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (PairEqualsMode.BOTH.equals(equalsMode) || PairEqualsMode.K1.equals(equalsMode))
            result = k1 != null ? k1.hashCode() : 0;
        if (PairEqualsMode.BOTH.equals(equalsMode) || PairEqualsMode.K2.equals(equalsMode))
            result = 31 * result + (k2 != null ? k2.hashCode() : 0);
        return result;
    }

    /**
     * Creates a new pair using the two keys provided.
     *
     * @param k1
     * @param k2
     */
    public Pair(
            final K1 k1, final K2 k2, final PairEqualsMode equalsMode) {
        this.k1 = k1;
        this.k2 = k2;
        this.equalsMode = equalsMode;
    }
    /**
     * Creates a new pair using the two keys provided.
     *
     * @param k1
     * @param k2
     */
    public Pair(
            final K1 k1, final K2 k2) {
        this.k1 = k1;
        this.k2 = k2;
        this.equalsMode = PairEqualsMode.BOTH;
    }

    /**
     * @return the first key
     */
    public K1 getK1() {
        return this.k1;
    }

    /**
     * @return the second key
     */
    public K2 getK2() {
        return this.k2;
    }

}
