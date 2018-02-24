package com.alainodea.idp.attributes;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSAnyBuilder;
import org.opensaml.core.xml.schema.impl.XSBooleanBuilder;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.impl.AttributeBuilder;

import javax.xml.namespace.QName;
import java.util.function.Function;

final class AttributeUtils {
    static Attribute buildAttribute(String name, boolean value) {
        return buildAttribute(name, value, AttributeUtils::buildBooleanAttributeValue);
    }

    static Attribute buildAttribute(String name, String value) {
        return buildAttribute(name, value, AttributeUtils::buildStringAttributeValue);
    }

    static Attribute buildAttribute(String name, Object value) {
        return buildAttribute(name, value, AttributeUtils::buildAnyAttributeValue);
    }

    private static XSBoolean buildBooleanAttributeValue(boolean value) {
        XSBoolean attributeValue = buildAttributeValue(new XSBooleanBuilder(), XSBoolean.TYPE_NAME);
        attributeValue.setValue(new XSBooleanValue(value, false));
        return attributeValue;
    }

    private static XSString buildStringAttributeValue(String value) {
        XSString attributeValue = buildAttributeValue(new XSStringBuilder(), XSString.TYPE_NAME);
        attributeValue.setValue(value);
        return attributeValue;
    }

    private static XSAny buildAnyAttributeValue(Object value) {
        XSAny attributeValue = buildAttributeValue(new XSAnyBuilder(), XSAny.TYPE_NAME);
        attributeValue.setTextContent(String.valueOf(value));
        return attributeValue;
    }

    private static <T extends XMLObject> T buildAttributeValue(XMLObjectBuilder<T> builder, QName typeName) {
        return builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, typeName);
    }

    private static <T> Attribute buildAttribute(String name, T value, Function<T, ? extends XMLObject> valueBuilder) {
        Attribute attribute = buildAttribute(name);
        XMLObject attributeValue = valueBuilder.apply(value);
        attribute.getAttributeValues().add(attributeValue);
        return attribute;
    }

    private static Attribute buildAttribute(String attributeName) {
        Attribute attribute = new AttributeBuilder().buildObject();
        attribute.setName(attributeName);
        return attribute;
    }
}
