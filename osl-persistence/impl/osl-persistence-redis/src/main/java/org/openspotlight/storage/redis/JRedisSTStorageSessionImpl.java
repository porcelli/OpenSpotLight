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
 * ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 * *
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

package org.openspotlight.storage.redis;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.jredis.JRedis;
import org.jredis.RedisException;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.storage.AbstractSTStorageSession;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.key.STKeyEntry;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STProperty;
import org.openspotlight.storage.domain.node.STPropertyImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Class.forName;
import static java.text.MessageFormat.format;
import static org.jredis.ri.alphazero.support.DefaultCodec.toStr;
import static org.openspotlight.common.util.Conversion.convert;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 4:46:25 PM
 */
public class JRedisSTStorageSessionImpl extends AbstractSTStorageSession {

    private final String SET_WITH_ALL_KEYS = "all-unique-keys";
    private final String SET_WITH_ALL_LOCAL_KEYS = "local-keys:{0}:unique-keys";
    private final String SET_WITH_NODE_KEYS_NAMES = "node-unique-key:{0}:key-names";
    private final String SET_WITH_NODE_CHILDREN_KEYS = "node-unique-key:{0}:children-unique-keys";
    private final String SET_WITH_NODE_CHILDREN_NAMED_KEYS = "node-unique-key:{0}:node-name:{1}:children-unique-keys";
    private final String SET_WITH_NODE_PROPERTY_NAMES = "node-unique-key:{0}:property-names";
    private final String SET_WITH_PROPERTY_NODE_IDS = "property-name:{0}:property-type:{1}:property-value:{2}:node-unique-keys";
    private final String KEY_WITH_PROPERTY_VALUE = "node-unique-key:{0}:property-name:{1}:value";
    private final String KEY_WITH_PROPERTY_DESCRIPTION = "node-unique-key:{0}:property-name:{1}:description";
    private final String KEY_WITH_PROPERTY_PARAMETERIZED_1 = "node-unique-key:{0}:property-name:{1}:parameterized-1";
    private final String KEY_WITH_PROPERTY_PARAMETERIZED_2 = "node-unique-key:{0}:property-name:{1}:parameterized-2";
    private final String KEY_WITH_PROPERTY_TYPE = "node-unique-key:{0}:property-name:{1}:type";
    private final String KEY_WITH_PARENT_UNIQUE_ID = "node-unique-key:{0}:parent-unique-id";
    private final String KEY_WITH_NODE_ENTRY_NAME = "node-unique-key:{0}:node-entry-name";

    private final JRedis jRedis;


    public JRedisSTStorageSessionImpl(JRedis jredis, STFlushMode flushMode, STPartition partition) {
        super(flushMode, partition);
        this.jRedis = jredis;
    }

    @Override
    protected Set<STNodeEntry> internalFindByCriteria(STCriteria criteria) throws Exception {
        List<String> inter = Lists.newLinkedList();
        for (STCriteriaItem c : criteria.getCriteriaItems()) {
            if (c instanceof STPropertyCriteriaItem) {
                STPropertyCriteriaItem p = (STPropertyCriteriaItem) c;
                inter.add(format(SET_WITH_PROPERTY_NODE_IDS, p.getPropertyName(), p.getType().getName(), convert(p.getValue(), String.class)).replaceAll(" ", ""));
            }
        }
        List<String> ids;
        if (inter.size() > 1) {
            ids = listBytesToListString(jRedis.sinter(inter.get(0), inter.subList(1, inter.size()).toArray(new String[0])));
        } else if (inter.size() == 1) {
            ids = listBytesToListString(jRedis.sinter(inter.get(0)));

        } else {
            ids = Collections.emptyList();
        }
        Set<STNodeEntry> nodeEntries = newHashSet();
        for (String id : ids) {
            String parentKey = id;
            STNodeEntry nodeEntry = loadNode(parentKey);
            nodeEntries.add(nodeEntry);
        }

        return ImmutableSortedSet.copyOf(nodeEntries);
    }

    private STNodeEntry loadNode(String parentKey) throws Exception {
        STUniqueKeyBuilder keyBuilder = null;
        do {
            List<String> keyPropertyNames = listBytesToListString(jRedis.smembers(format(SET_WITH_NODE_KEYS_NAMES, parentKey)));
            String nodeEntryName = toStr(jRedis.get(format(KEY_WITH_NODE_ENTRY_NAME, parentKey)));
            keyBuilder = keyBuilder == null ? createKey(nodeEntryName) : keyBuilder.withParent(nodeEntryName);

            for (String keyName : keyPropertyNames) {
                String typeName = toStr(jRedis.get(format(KEY_WITH_PROPERTY_TYPE, parentKey, keyName)));
                String typeValueAsString = toStr(jRedis.get(format(KEY_WITH_PROPERTY_VALUE, parentKey, keyName)));
                Class<? extends Serializable> type = (Class<? extends Serializable>) forName(typeName);
                Serializable value = convert(typeValueAsString, type);
                keyBuilder.withEntry(keyName, type, value);
            }
            parentKey = toStr(jRedis.get(format(KEY_WITH_PARENT_UNIQUE_ID, parentKey)));

        } while (parentKey != null);
        STUniqueKey uniqueKey = keyBuilder.andCreate();
        STNodeEntry nodeEntry = createEntryWithKey(uniqueKey);
        return nodeEntry;
    }

    private List<String> listBytesToListString(List<byte[]> ids) {
        List<String> idsAsString = Lists.newLinkedList();
        if (ids != null) {
            for (byte[] b : ids) {
                String s = toStr(b);
                idsAsString.add(s);
            }
        }
        return idsAsString;
    }

    @Override
    protected STStorageSession createNewInstance(STFlushMode flushMode, STPartition partition) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void flushNewItem(STNodeEntry entry) throws Exception {

        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(entry.getUniqueKey());
        jRedis.sadd(SET_WITH_ALL_KEYS, uniqueKey);
        jRedis.set(format(KEY_WITH_NODE_ENTRY_NAME, uniqueKey), entry.getNodeEntryName());
        STUniqueKey parentKey = entry.getUniqueKey().getParentKey();
        if (parentKey != null) {
            jRedis.set(format(KEY_WITH_PARENT_UNIQUE_ID, uniqueKey), supportMethods.getUniqueKeyAsStringHash(parentKey));
        }
        String localKey = supportMethods.getLocalKeyAsStringHash(entry.getLocalKey());
        jRedis.sadd(format(SET_WITH_ALL_LOCAL_KEYS, localKey), uniqueKey);

        for (STKeyEntry<?> k : entry.getLocalKey().getEntries()) {
            jRedis.sadd(format(SET_WITH_NODE_KEYS_NAMES, uniqueKey), k.getPropertyName());
            jRedis.sadd(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey), k.getPropertyName());
            jRedis.sadd(format(SET_WITH_PROPERTY_NODE_IDS, k.getPropertyName(), k.getType().getName(),
                    convert(k.getValue(), String.class)).replaceAll(" ", ""), uniqueKey);
            jRedis.set(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, k.getPropertyName()), k.getType().getName());
            jRedis.set(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, k.getPropertyName()), convert(k.getValue(), String.class));
            jRedis.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, k.getPropertyName()), convert(STProperty.STPropertyDescription.KEY, String.class));
        }


    }

    @Override
    protected void flushRemovedItem(STNodeEntry entry) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(entry.getUniqueKey());
        jRedis.srem(SET_WITH_ALL_KEYS, uniqueKey);

        String localKey = supportMethods.getLocalKeyAsStringHash(entry.getLocalKey());
        jRedis.srem(format(SET_WITH_ALL_LOCAL_KEYS, localKey), uniqueKey);

        for (STKeyEntry<?> k : entry.getLocalKey().getEntries()) {
            jRedis.del(format(SET_WITH_NODE_KEYS_NAMES, uniqueKey));
            jRedis.del(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey));
            jRedis.srem(format(SET_WITH_PROPERTY_NODE_IDS, k.getPropertyName(), k.getType().getName(),
                    convert(k.getValue(), String.class)).replaceAll(" ", ""), uniqueKey);
            jRedis.del(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, k.getPropertyName()));
            jRedis.del(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, k.getPropertyName()));
        }
    }

    @Override
    protected boolean internalHasSavedProperty(STProperty stProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(stProperty.getParent().getUniqueKey());

        return jRedis.exists(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, stProperty.getPropertyName()));

    }

    @Override
    protected Class<?> internalPropertyDiscoverType(STProperty stProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(stProperty.getParent().getUniqueKey());

        String typeName = toStr(jRedis.get(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, stProperty.getPropertyName())));

        Class<?> type = forName(typeName);
        return type;


    }

    @Override
    protected Set<STNodeEntry> internalNodeEntryGetChildren(STNodeEntry stNodeEntry) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected STNodeEntry internalNodeEntryGetParent(STNodeEntry stNodeEntry) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected <T> T internalPropertyGetKeyPropertyAs(STProperty stProperty, Class<T> type) throws Exception {
        return this.<T>internalPropertyGetSimplePropertyAs(stProperty, type);
    }

    @Override
    protected InputStream internalPropertyGetInputStreamProperty(STProperty stProperty)throws Exception{
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(stProperty.getParent().getUniqueKey());

        byte[] fromStorage = jRedis.get(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, stProperty.getPropertyName()));
        
        return new ByteArrayInputStream(fromStorage);
    }

    @Override
    protected <T> T internalPropertyGetSerializedPojoPropertyAs(STProperty stProperty, Class<T> type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected <T> T internalPropertyGetSerializedMapPropertyAs(STProperty stProperty, Class<T> type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected <T> T internalPropertyGetSerializedSetPropertyAs(STProperty stProperty, Class<T> type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected <T> T internalPropertyGetSerializedListPropertyAs(STProperty stProperty, Class<T> type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected <T> T internalPropertyGetMapPropertyAs(STProperty stProperty, Class<T> type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected <T> T internalPropertyGetSetPropertyAs(STProperty stProperty, Class<T> type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected <T> T internalPropertyGetListPropertyAs(STProperty stProperty, Class<T> type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected <T> T internalPropertyGetSimplePropertyAs(STProperty stProperty, Class<T> type) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(stProperty.getParent().getUniqueKey());

        String typeValueAsString = toStr(jRedis.get(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, stProperty.getPropertyName())));
        T value = convert(typeValueAsString, type);
        return value;
    }

    @Override
    protected Set<STProperty> internalNodeEntryLoadProperties(STNodeEntry stNodeEntry) throws Exception {

        String parentKey = supportMethods.getUniqueKeyAsStringHash(stNodeEntry.getUniqueKey());
        List<String> propertyNames = listBytesToListString(jRedis.smembers(format(SET_WITH_NODE_PROPERTY_NAMES, parentKey)));
        Set<STProperty> result = newHashSet();
        for (String propertyName : propertyNames) {
            STProperty property = loadProperty(stNodeEntry, parentKey, propertyName);

            result.add(property);

        }

        return result;
    }

    private STProperty loadProperty(STNodeEntry stNodeEntry, String parentKey, String propertyName) throws RedisException, ClassNotFoundException, SLException {
        String typeName = toStr(jRedis.get(format(KEY_WITH_PROPERTY_TYPE, parentKey, propertyName)));
        String descriptionAsString = toStr(jRedis.get(format(KEY_WITH_PROPERTY_DESCRIPTION, parentKey, propertyName)));
        STProperty.STPropertyDescription description = STProperty.STPropertyDescription.valueOf(descriptionAsString);
        Class<? extends Serializable> type = (Class<? extends Serializable>) forName(typeName);

        Class<?> parameterized1 = null;
        Class<?> parameterized2 = null;
        if (jRedis.exists(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, parentKey, propertyName))) {
            String typeName1 = toStr(jRedis.get(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, parentKey, propertyName)));
            parameterized1 = forName(typeName1);

        }
        if (jRedis.exists(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, parentKey, propertyName))) {
            String typeName2 = toStr(jRedis.get(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, parentKey, propertyName)));
            parameterized2 = forName(typeName2);

        }

        STProperty property = new STPropertyImpl(stNodeEntry, propertyName, description, type, parameterized1, parameterized2);
        if (description.getLoadWeight().equals(STProperty.STPropertyDescription.STLoadWeight.EASY)) {
            String typeValueAsString = toStr(jRedis.get(format(KEY_WITH_PROPERTY_VALUE, parentKey, propertyName)));
            Serializable value = convert(typeValueAsString, type);
            property.getInternalMethods().setValueOnLoad(value);
        }
        return property;
    }

    @Override
    protected void internalFlushInputStreamProperty(STProperty dirtyProperty) throws Exception{
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());

        InputStream valueAsStream = dirtyProperty.getInternalMethods().<InputStream>getTransientValue();
        if(valueAsStream.markSupported()){
            valueAsStream.reset();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(valueAsStream,outputStream);

        jRedis.sadd(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey), dirtyProperty.getPropertyName());

        jRedis.set(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getPropertyType().getName());
        jRedis.set(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, dirtyProperty.getPropertyName()), outputStream.toByteArray() );
        jRedis.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.INPUT_STREAM, String.class));
    }

    @Override
    protected void internalFlushSerializedPojoProperty(STProperty dirtyProperty) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void internalFlushSerializedMapProperty(STProperty dirtyProperty) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void internalFlushSerializedSetProperty(STProperty dirtyProperty) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void internalFlushSerializedListProperty(STProperty dirtyProperty) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void internalFlushMapProperty(STProperty dirtyProperty) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void internalFlushSetProperty(STProperty dirtyProperty) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void internalFlushListProperty(STProperty dirtyProperty) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void internalFlushSimpleProperty(STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());

        String transientValueAsString = convert(dirtyProperty.getInternalMethods().<Object>getTransientValue(), String.class);

        jRedis.sadd(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey), dirtyProperty.getPropertyName());
        jRedis.sadd(format(SET_WITH_PROPERTY_NODE_IDS, dirtyProperty.getPropertyName(), dirtyProperty.getInternalMethods().<Object>getPropertyType().getName(),
                convert(dirtyProperty.getValue(this), String.class)).replaceAll(" ", ""), uniqueKey);

        jRedis.set(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getPropertyType().getName());
        jRedis.set(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, dirtyProperty.getPropertyName()), transientValueAsString );
        jRedis.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.SIMPLE, String.class));

    }
}
