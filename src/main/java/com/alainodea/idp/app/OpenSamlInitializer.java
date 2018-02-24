package com.alainodea.idp.app;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.xmlsec.config.JavaCryptoValidationInitializer;

final class OpenSamlInitializer {
    static void initialize() {
        try {
            new JavaCryptoValidationInitializer().init();
            InitializationService.initialize();
        } catch (InitializationException e) {
            throw new IllegalStateException("Could no initialize OpenSAML", e);
        }
    }
}
