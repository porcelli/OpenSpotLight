package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.STStorageSession;

/**
 * Created by IntelliJ IDEA.
 * User: feuteston
 * Date: 28/03/2010
 * Time: 10:27:26
 * To change this template use File | Settings | File Templates.
 */
public interface STProperty {

    STNodeEntry getParent();

    <T> void setValue(STStorageSession session, T value);

    <T> T getValueAs(STStorageSession session, Class<T> type);

    <T> T getValue(STStorageSession session);

    String getPropertyName();

    public STPropertyInternalMethods getInternalMethods();

    interface STPropertyInternalMethods {
        <T> void setValueOnLoad(T value);

        void removeTransientValueIfExpensive();


        <T> Class<T> getPropertyType();

        <T> Class<T> getFirstParameterizedType();

        <T> Class<T> getSecondParameterizedType();

        boolean hasParameterizedTypes();

        boolean isSerialized();

        boolean isDifficultToLoad();

        boolean isKey();

        <T> T getTransientValue();

        STPropertyDescription getDescription();
    }

    enum STPropertyDescription {
        KEY(STSerializedType.NOT_SERIALIZED, STLoadWeight.EASY,true),
        SIMPLE(STSerializedType.NOT_SERIALIZED, STLoadWeight.EASY,false),
        SIMPLE_INDEXED(STSerializedType.NOT_SERIALIZED, STLoadWeight.EASY,true),
        SERIALIZED_LIST(STSerializedType.SERIALIZED, STLoadWeight.DIFFICULT, 1,false),
        SERIALIZED_SET(STSerializedType.SERIALIZED, STLoadWeight.DIFFICULT, 1,false),
        SERIALIZED_MAP(STSerializedType.SERIALIZED, STLoadWeight.DIFFICULT, 2,false),
        SERIALIZED_POJO(STSerializedType.SERIALIZED, STLoadWeight.DIFFICULT,false),
        INPUT_STREAM(STSerializedType.SERIALIZED, STLoadWeight.DIFFICULT,false);

        private final STSerializedType serialized;
        private final STLoadWeight loadWeight;
        private final int parameterizedLevel;
        private final boolean indexed;

        STPropertyDescription(STSerializedType serialized, STLoadWeight loadWeight, boolean indexed) {
            this.serialized = serialized;
            this.loadWeight = loadWeight;
            this.parameterizedLevel = 0;
            this.indexed = indexed;
        }

        STPropertyDescription(STSerializedType serialized, STLoadWeight loadWeight, int parameterizedLevel, boolean indexed) {
            this.serialized = serialized;
            this.loadWeight = loadWeight;
            this.parameterizedLevel = parameterizedLevel;
            this.indexed = indexed;
        }

        public STSerializedType getSerialized() {
            return serialized;
        }

        public STLoadWeight getLoadWeight() {
            return loadWeight;
        }

        public enum STSerializedType {
            SERIALIZED, NOT_SERIALIZED
        }

        public enum STLoadWeight {
            EASY, DIFFICULT
        }

        public boolean hasFirstParameterizedLevel() {
            return parameterizedLevel > 0;
        }

        public boolean hasSecondParameterizedLevel() {
            return parameterizedLevel > 1;
        }

        public boolean isIndexed() {
            return indexed;
        }
    }


}
