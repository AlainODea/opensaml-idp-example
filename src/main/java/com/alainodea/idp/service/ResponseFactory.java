package com.alainodea.idp.service;

import com.alainodea.idp.model.AuthnRequest;
import net.shibboleth.utilities.java.support.security.RandomIdentifierGenerationStrategy;
import org.joda.time.DateTime;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml.saml2.core.impl.ResponseBuilder;
import org.opensaml.saml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml.saml2.core.impl.StatusCodeBuilder;

import java.util.concurrent.TimeUnit;

final class ResponseFactory {
    static Response buildResponse(AuthnRequest input) throws Exception {
        long instant = TimeUnit.SECONDS.toMillis(input.authenticationInstant.getEpochSecond());
        DateTime authenticationTime = new DateTime(instant);

        Response response = new ResponseBuilder().buildObject();
        response.setID(new RandomIdentifierGenerationStrategy().generateIdentifier());
        response.setIssueInstant(authenticationTime);
        response.setVersion(SAMLVersion.VERSION_20);
        response.setIssuer(buildIssuer(input));
        response.setDestination(input.destinationUrl);
        response.setStatus(buildStatus());
        response.getAssertions().add(AssertionFactory.buildAssertion(input, authenticationTime));

        return response;
    }

    private static Issuer buildIssuer(AuthnRequest input) {
        Issuer issuer = new IssuerBuilder().buildObject();
        issuer.setValue(input.issuer);
        return issuer;
    }

    private static Status buildStatus() {
        StatusCode statusCode = new StatusCodeBuilder().buildObject();
        statusCode.setValue(StatusCode.SUCCESS);
        Status status = new StatusBuilder().buildObject();
        status.setStatusCode(statusCode);
        return status;
    }
}
