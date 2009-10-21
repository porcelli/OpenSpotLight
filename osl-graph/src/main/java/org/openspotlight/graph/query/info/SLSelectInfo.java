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
package org.openspotlight.graph.query.info;

import org.openspotlight.common.util.StringBuilderUtil;

/**
 * The Class SLSelectInfo.
 * 
 * @author Vitor Hugo Chagas
 */
public abstract class SLSelectInfo {

    /** The Constant INDIFINITE. */
    public static final int INDEFINITE = 0;

    /** The keep result. */
    private boolean         keepResult;

    /** The x times. */
    private Integer         xTimes     = null;

    /** The limit. */
    private Integer         limit      = null;

    /** The offset. */
    private Integer         offset     = null;

    /**
     * Checks if is keep result.
     * 
     * @return true, if is keep result
     */
    public boolean isKeepResult() {
        return keepResult;
    }

    /**
     * Sets the keep result.
     * 
     * @param keepResult the new keep result
     */
    public void setKeepResult( boolean keepResult ) {
        this.keepResult = keepResult;
    }

    /**
     * Gets the x times.
     * 
     * @return the x times
     */
    public Integer getXTimes() {
        return xTimes;
    }

    /**
     * Sets the x times.
     * 
     * @param times the new x times
     */
    public void setXTimes( Integer times ) {
        xTimes = times;
    }

    /**
     * Gets the limit.
     * 
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     * 
     * @param limit the new limit
     */
    public void setLimit( Integer limit ) {
        this.limit = limit;
    }

    /**
     * Gets the offset.
     * 
     * @return the offset
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * Sets the offset.
     * 
     * @param offset the new offset
     */
    public void setOffset( Integer offset ) {
        this.offset = offset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (isKeepResult()) {
            StringBuilderUtil.append(buffer, "\nKEEP RESULT");
        }
        Integer xTimes = getXTimes();
        if (xTimes != null) {
            StringBuilderUtil.append(buffer, "\nEXECUTING ", (xTimes == INDEFINITE ? "INDEFINITE" : "" + xTimes), " TIMES");
        }

        Integer limit = getLimit();
        if (limit != null) {
            StringBuilderUtil.append(buffer, "\nLIMIT ", limit);
            Integer offset = getOffset();
            if (offset != null) {
                StringBuilderUtil.append(buffer, " OFFSET ", offset);
            }
        }

        return buffer.toString();
    }
}
