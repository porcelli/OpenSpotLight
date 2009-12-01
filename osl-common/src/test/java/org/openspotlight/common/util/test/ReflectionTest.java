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

package org.openspotlight.common.util.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Reflection.searchInheritanceType;
import static org.openspotlight.common.util.Reflection.searchType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.Reflection.InheritanceType;
import org.openspotlight.common.util.Reflection.UnwrappedCollectionTypeFromMethodReturn;
import org.openspotlight.common.util.Reflection.UnwrappedMapTypeFromMethodReturn;

/**
 * Test for the class {@link Reflection}
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SuppressWarnings( "all" )
public class ReflectionTest {

    private static class ClassUnderTest {

        public Collection<? super String> collectionString() {
            return null;
        }

        public List<String> listString() {
            return null;
        }

        public Map<? extends String, ? extends Integer> mapExtendsStringInteger() {
            return null;
        }

        public Map<String, Integer> mapStringInteger() {
            return null;
        }

        public Map<? super String, ? super Integer> mapSuperStringInteger() {
            return null;
        }

        public Set<? extends String> setString() {
            return null;
        }

    }

    @Test
    public void shouldFindInheritanceTypeOnInheritedClasses() throws Exception {
        final InheritanceType type = searchInheritanceType(String.class, Integer.class, Serializable.class);
        assertThat(type, is(InheritanceType.INHERITED_CLASS));
    }

    @Test
    public void shouldFindInheritanceTypeOnTheSameClasses() throws Exception {
        final InheritanceType type = searchInheritanceType(String.class, Integer.class, String.class);
        assertThat(type, is(InheritanceType.SAME_CLASS));
    }

    @Test
    public void shouldFindOnInheritedClasses() throws Exception {
        final Class<?> type = searchType(String.class, Integer.class, Serializable.class);
        assertThat(type, is(Serializable.class));
    }

    @Test
    public void shouldFindOnTheSameClasses() throws Exception {
        final Class<?> type = searchType(String.class, Integer.class, String.class);
        assertThat(String.class.equals(type), is(true));
    }

    @Test
    public void shouldFindTypeUnderCollection() throws Exception {
        final UnwrappedCollectionTypeFromMethodReturn<?> result = Reflection.unwrapCollectionFromMethodReturn(ClassUnderTest.class.getMethod("collectionString"));
        assertThat(result.getCollectionType().equals(Collection.class), is(true));
        assertThat(result.getItemType().equals(String.class), is(true));
    }

    @Test
    public void shouldFindTypeUnderList() throws Exception {
        final UnwrappedCollectionTypeFromMethodReturn<?> result = Reflection.unwrapCollectionFromMethodReturn(ClassUnderTest.class.getMethod("listString"));
        assertThat(result.getCollectionType().equals(List.class), is(true));
        assertThat(result.getItemType().equals(String.class), is(true));
    }

    @Test
    public void shouldFindTypeUnderMap() throws Exception {
        final UnwrappedMapTypeFromMethodReturn<?, ?> result = Reflection.unwrapMapFromMethodReturn(ClassUnderTest.class.getMethod("mapStringInteger"));
        assertThat(result.getItemType().getK1().equals(String.class), is(true));
        assertThat(result.getItemType().getK2().equals(Integer.class), is(true));
    }

    @Test
    public void shouldFindTypeUnderMapExtends() throws Exception {
        final UnwrappedMapTypeFromMethodReturn<?, ?> result = Reflection.unwrapMapFromMethodReturn(ClassUnderTest.class.getMethod("mapExtendsStringInteger"));
        assertThat(result.getItemType().getK1().equals(String.class), is(true));
        assertThat(result.getItemType().getK2().equals(Integer.class), is(true));
    }

    @Test
    public void shouldFindTypeUnderMapSuper() throws Exception {
        final UnwrappedMapTypeFromMethodReturn<?, ?> result = Reflection.unwrapMapFromMethodReturn(ClassUnderTest.class.getMethod("mapSuperStringInteger"));
        assertThat(result.getItemType().getK1().equals(String.class), is(true));
        assertThat(result.getItemType().getK2().equals(Integer.class), is(true));
    }

    @Test
    public void shouldFindTypeUnderSet() throws Exception {
        final UnwrappedCollectionTypeFromMethodReturn<?> result = Reflection.unwrapCollectionFromMethodReturn(ClassUnderTest.class.getMethod("setString"));
        assertThat(result.getCollectionType().equals(Set.class), is(true));
        assertThat(result.getItemType().equals(String.class), is(true));
    }

    @Test
    public void shouldNotFindInheritanceTypeOnStrangeClasses() throws Exception {
        final InheritanceType type = searchInheritanceType(String.class, Integer.class, Double.class, BigDecimal.class);
        assertThat(type, is(InheritanceType.NO_INHERITANCE));
    }

    @Test
    public void shouldNotFindOnStrangeClasses() throws Exception {
        final Class<?> type = searchType(String.class, Integer.class, Double.class, BigDecimal.class);
        assertThat(type, is(nullValue()));
    }
}
