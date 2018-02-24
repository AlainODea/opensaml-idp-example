package com.alainodea.idp.attributes;

public interface NamedAttribute {
    String name();

    static NamedAttribute create(String name, String value) {
        return new StringAttribute(name, value);
    }

    static NamedAttribute create(String name, boolean value) {
        return new BooleanAttribute(name, value);
    }

    static NamedAttribute create(String name, Object value) {
        return new AnyAttribute(name, value);
    }
}

