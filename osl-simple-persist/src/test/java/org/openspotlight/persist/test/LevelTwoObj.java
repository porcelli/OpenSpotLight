/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA**********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA
 * E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */
package org.openspotlight.persist.test;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SetUniqueIdOnThisProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class LevelTwoObj implements SimpleNodeType {
    private String      uuid;

    private String      key;

    private String      property;
    private LevelOneObj parentObj;

    private PropertyObj propertyObj;

    @Override
    public boolean equals(final Object o) {
        if (o == this) { return true; }
        if (!(o instanceof LevelTwoObj)) { return false; }
        final LevelTwoObj that = (LevelTwoObj) o;
        return Equals.eachEquality(Arrays.of(parentObj, key), Arrays.andOf(that.parentObj, that.key));
    }

    @KeyProperty
    public String getKey() {
        return key;
    }

    @ParentProperty
    public LevelOneObj getLevelOneObj() {
        return parentObj;
    }

    public String getProperty() {
        return property;
    }

    public PropertyObj getPropertyObj() {
        return propertyObj;
    }

    @SetUniqueIdOnThisProperty
    public String getUuid() {
        return uuid;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public void setLevelOneObj(final LevelOneObj parentObj) {
        this.parentObj = parentObj;
    }

    public void setProperty(final String property) {
        this.property = property;
    }

    public void setPropertyObj(final PropertyObj propertyObj) {
        this.propertyObj = propertyObj;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

}
