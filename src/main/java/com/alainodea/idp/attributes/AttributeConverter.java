package com.alainodea.idp.attributes;

import org.opensaml.saml.saml2.core.Attribute;

public interface AttributeConverter {
    static Attribute convertAttribute(NamedAttribute attr) {
        if (attr instanceof StringAttribute) {
            return AttributeUtils.buildAttribute(attr.name(), ((StringAttribute) attr).value);
        } else if (attr instanceof BooleanAttribute) {
            return AttributeUtils.buildAttribute(attr.name(), ((BooleanAttribute) attr).value);
        } else if (attr instanceof AnyAttribute) {
            return AttributeUtils.buildAttribute(attr.name(), ((AnyAttribute) attr).value);
        } else {
            throw new IllegalArgumentException("Unsupported attribute type " + attr.getClass());
        }
    }
}
