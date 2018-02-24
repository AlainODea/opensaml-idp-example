package com.alainodea.idp.attributes;

final class BooleanAttribute extends AbstractAttribute {
    final boolean value;

    BooleanAttribute(String name, boolean value) {
        super(name);
        this.value = value;
    }
}
