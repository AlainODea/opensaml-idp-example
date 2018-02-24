package com.alainodea.idp.app;

import com.alainodea.idp.attributes.NamedAttribute;
import com.alainodea.idp.model.AuthnRequest;
import net.shibboleth.utilities.java.support.security.RandomIdentifierGenerationStrategy;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.BasicX509Credential;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

final class AuthnRequestBuilder {
    static AuthnRequest buildAuthRequest(String userName, List<NamedAttribute> attributes) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException {
        Properties properties = new Properties();
        try (Reader reader = new FileReader("idp.properties")) {
            properties.load(reader);
        }
        AuthnRequest input = new AuthnRequest();
        input.authenticationInstant = Instant.now();
        input.issuer = properties.getProperty("issuer");
        input.audienceRestriction = properties.getProperty("audienceRestriction");
        input.destinationUrl = properties.getProperty("destinationUrl");
        input.nameId = userName;
        input.sessionId = new RandomIdentifierGenerationStrategy().generateIdentifier();
        input.attributes = attributes;
        input.signingCredential = loadSigningCredential(
                properties.getProperty("signingKeystorePassword"),
                properties.getProperty("signingKeystore")
        );
        return input;
    }

    private static Credential loadSigningCredential(String signingKeystorePassword, String signingKeystore) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException {
        char[] password = signingKeystorePassword.toCharArray();

        KeyStore store = KeyStore.getInstance("PKCS12");
        try (FileInputStream stream = new FileInputStream(signingKeystore)) {
            store.load(stream, password);
        }

        KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(password);
        KeyStore.PrivateKeyEntry pkEntry =
                (KeyStore.PrivateKeyEntry) store.getEntry("1", protectionParameter);
        PrivateKey pk = pkEntry.getPrivateKey();

        X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();
        return new BasicX509Credential(certificate, pk);
    }
}
