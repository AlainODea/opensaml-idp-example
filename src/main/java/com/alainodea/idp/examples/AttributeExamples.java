package com.alainodea.idp.examples;

import com.alainodea.idp.attributes.NamedAttribute;

import java.util.Arrays;
import java.util.List;

public interface AttributeExamples {
    String CRASH_OKTA = "crash-okta";
    String WORKS_FOR_OKTA = "works-for-okta";

    static List<NamedAttribute> attributesByExampleName(String exampleName, String userName) {
        switch (exampleName) {
            case CRASH_OKTA: return attributesThatCrashOkta(userName);
            case WORKS_FOR_OKTA: return attributesThatWorkForOkta(userName);
            default: throw new IllegalArgumentException("Unknown example name '" + exampleName + "'");
        }
    }

    static List<NamedAttribute> attributesThatCrashOkta(String userName) {
        return Arrays.asList(
                NamedAttribute.create("firstName", "Crash"),
                NamedAttribute.create("lastName", "Example"),
                NamedAttribute.create("email", userName),
                NamedAttribute.create("thisMachineKillsOkta", true)
        );
    }

    static List<NamedAttribute> attributesThatWorkForOkta(String userName) {
        return Arrays.asList(
                NamedAttribute.create("firstName", "Success"),
                NamedAttribute.create("lastName", "Example"),
                NamedAttribute.create("email", userName),
                NamedAttribute.create("thisIsFine", new Object())
        );
    }
}
