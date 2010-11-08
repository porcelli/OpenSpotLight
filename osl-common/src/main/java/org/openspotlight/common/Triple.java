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

package org.openspotlight.common;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;

/**
 * This is a simple class to store three objects.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * @param <K1>
 * @param <K2>
 * @param <K3>
 */
public class Triple<K1, K2, K3> {
    private volatile int hashCode;
    /**
     * First item.
     */
    private final K1     k1;
    /**
     * Second item.
     */
    private final K2     k2;

    /**
     * third item.
     */
    private final K3     k3;

    /**
     * Creates a new pair using the two keys provided.
     * 
     * @param k1
     * @param k2
     * @param k3
     */
    public Triple(
                   final K1 k1, final K2 k2, final K3 k3) {
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
    }

    public static <K1, K2, K3> Triple<K1, K2, K3> newTriple(final K1 k1, final K2 k2, final K3 k3) {
        return new Triple<K1, K2, K3>(k1, k2, k3);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) { return true; }
        if (!(o instanceof Triple<?, ?, ?>)) { return false; }
        final Triple<?, ?, ?> that = (Triple<?, ?, ?>) o;
        return Equals.eachEquality(Arrays.of(this.k1, this.k2, this.k3), Arrays.andOf(that.k1, that.k2, that.k3));
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

    /**
     * @return the third key
     */
    public K3 getK3() {
        return this.k3;
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(this.k1, this.k2, this.k3);
            this.hashCode = result;
        }
        return result;

    }

}
