package com.alainodea.idp.attributes;

abstract class AbstractAttribute implements NamedAttribute {
    private final String name;

    AbstractAttribute(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }
}
