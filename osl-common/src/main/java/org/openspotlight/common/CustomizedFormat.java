package org.openspotlight.common;

public class CustomizedFormat {

    private final String[] items;
    public final int sizeOfParameters;

    public static CustomizedFormat cf(String itemsAsStrings){
        return new CustomizedFormat(itemsAsStrings);
    }

    public CustomizedFormat(String itemsAsString) {
        items = itemsAsString.split("[ ]");
        sizeOfParameters = items.length - 1;
    }

    public String format(String s) {
        if (sizeOfParameters != 1)
            throw new IllegalArgumentException("Expected " + sizeOfParameters + " parameters");
        StringBuilder b = new StringBuilder();
        b.append(items[0]);
        b.append(s);
        b.append(items[1]);
        return b.toString();
    }

    public String format(String s1, String s2) {
        if (sizeOfParameters != 2)
            throw new IllegalArgumentException("Expected " + sizeOfParameters + " parameters");
        StringBuilder b = new StringBuilder();
        b.append(items[0]);
        b.append(s1);
        b.append(items[1]);
        b.append(s2);
        b.append(items[2]);
        return b.toString();
    }

}
