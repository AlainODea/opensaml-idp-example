package com.alainodea.idp.attributes;

final class StringAttribute extends AbstractAttribute {
    final String value;

    StringAttribute(String name, String value) {
        super(name);
        this.value = value;
    }
}
