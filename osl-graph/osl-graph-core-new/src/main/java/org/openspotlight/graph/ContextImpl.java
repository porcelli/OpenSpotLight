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
package org.openspotlight.graph;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.Pair;
import org.openspotlight.common.util.Conversion;

import com.google.common.collect.ImmutableSet;

public class ContextImpl extends Context {

	public static final int DEFAULT_CONTEXT_WEIGTH = 1;

	public static final String WEIGTH_PROPERTY="context_weigth";
	
	public ContextImpl(String id, Map<String, Serializable> properties,
			String caption, int weight) {
		super();
		this.id = id;
		this.properties = properties;
		this.weightValue = weight;
		this.caption = caption;
		removedProperties = new HashSet<String>();
	}

	private boolean dirty = false;

	private String caption;

	private final String id;

	private final int initialWeightValue = DEFAULT_CONTEXT_WEIGTH;

	private int weightValue;

	private final Map<String, Serializable> properties;

	private final Set<String> removedProperties;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		dirty = true;
		this.caption = caption;
	}

	public void resetDirty() {
		this.dirty = false;
		removedProperties.clear();
	}

	public int getWeightValue() {
		return weightValue;
	}

	public void setWeightValue(int weightValue) {
		this.weightValue = weightValue;
	}

	public String getId() {
		return id;
	}

	public int getInitialWeightValue() {
		return initialWeightValue;
	}

	@Override
	public Set<Pair<String, Serializable>> getProperties() {
		ImmutableSet.Builder<Pair<String, Serializable>> builder = ImmutableSet
				.builder();
		for (Map.Entry<String, Serializable> e : properties.entrySet()) {
			builder.add(new Pair<String, Serializable>(e.getKey(),
					e.getValue(), Pair.PairEqualsMode.K1));
		}
		return builder.build();
	}

	@Override
	public Iterable<String> getPropertyKeys() {
		return ImmutableSet.copyOf(properties.keySet());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V extends Serializable> V getPropertyValue(String key) {
		return (V) properties.get(key);
	}

	@Override
	public <V extends Serializable> V getPropertyValue(String key,
			V defaultValue) {
		V value = getPropertyValue(key);
		return value != null ? value : defaultValue;
	}

	@Override
	public String getPropertyValueAsString(String key) {
		Serializable value = getPropertyValue(key);
		if (value == null)
			return null;
		return Conversion.convert(value, String.class);
	}

	@Override
	public boolean hasProperty(String key) throws IllegalArgumentException {
		return properties.containsKey(key);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void removeProperty(String key) {
		dirty = true;
		properties.remove(key);
		removedProperties.add(key);
	}

	@Override
	public <V extends Serializable> void setProperty(String key, V value) {
		dirty = true;
		properties.put(key, value);
	}

}
