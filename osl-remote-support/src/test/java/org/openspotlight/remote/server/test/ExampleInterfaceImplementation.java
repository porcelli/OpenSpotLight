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

package org.openspotlight.remote.server.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ExampleInterfaceImplementation implements ExampleInterface {

    private final AnotherNonSerializableClass     remoteResult = new AnotherNonSerializableClass("damn cool stuff!");

    private Collection<NonSerializableInterface>  list         = new ArrayList<NonSerializableInterface>();

    private Map<String, NonSerializableInterface> map          = new HashMap<String, NonSerializableInterface>();

    public ExampleInterfaceImplementation() {
        map.put("1", new AnotherNonSerializableClass("1"));
        map.put("2", new AnotherNonSerializableClass("2"));
        map.put("3", new AnotherNonSerializableClass("3"));
        list.add(new AnotherNonSerializableClass("1"));
        list.add(new AnotherNonSerializableClass("2"));
        list.add(new AnotherNonSerializableClass("3"));
    }

    @Override
    public NonSerializableInterface doSomethingWith(final NonSerializableInterface remoteParameter) {
        remoteParameter.setStuff("AA" + remoteParameter.getStuff());
        return remoteParameter;
    }

    @Override
    public NonSerializableInterface doSomethingWithCollection(final Collection<NonSerializableInterface> collection) {
        list = collection;
        return list.iterator().next();
    }

    @Override
    public NonSerializableInterface doSomethingWithMap(final Map<String, NonSerializableInterface> map) {
        this.map = map;
        return this.map.entrySet().iterator().next().getValue();
    }

    @Override
    public boolean expensiveMethodWithoutParameter() {
        try {
            Thread.sleep(100);
        } catch (final InterruptedException e) {
            // nothing to see here... move along sir.
        }
        return true;

    }

    @Override
    public String expensiveMethodWithParameter(final String id,
                                                final String anotherStr,
                                                final boolean throwsException)
        throws Exception {
        try {
            Thread.sleep(100);
        } catch (final InterruptedException e) {
            // nothing to see here... move along sir.
        }
        if (throwsException) { throw new Exception(id + anotherStr); }
        return id + anotherStr;

    }

    @Override
    public Collection<NonSerializableInterface> getList() {

        return list;
    }

    @Override
    public Map<String, NonSerializableInterface> getMap() {
        return map;
    }

    @Override
    public AnotherNonSerializableClass getRemoteResult() {
        return remoteResult;
    }

    @Override
    public Integer returns6Times(final Integer another) {
        return another * 6;
    }

    public boolean returnsTrue() {
        return true;
    }

    @Override
    public void throwAnException()
        throws EnumConstantNotPresentException {
        throw new EnumConstantNotPresentException(Enum.class, "stuff");
    }

    @Override
    public boolean unsupportedMethod() {
        return false;
    }

}
