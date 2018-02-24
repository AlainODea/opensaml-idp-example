package com.alainodea.idp.service;

import com.alainodea.idp.attributes.AttributeConverter;
import com.alainodea.idp.model.AuthnRequest;
import net.shibboleth.utilities.java.support.security.RandomIdentifierGenerationStrategy;
import org.joda.time.DateTime;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.core.impl.*;
import org.opensaml.xmlsec.signature.support.SignatureException;

final class AssertionFactory {
    static Assertion buildAssertion(AuthnRequest input, DateTime authenticationTime) throws MarshallingException, SignatureException {
        Assertion assertion = new AssertionBuilder().buildObject();
        assertion.setID(new RandomIdentifierGenerationStrategy().generateIdentifier());
        assertion.setIssuer(buildIssuer(input));
        assertion.setIssueInstant(authenticationTime);
        assertion.setVersion(SAMLVersion.VERSION_20);
        assertion.getAuthnStatements().add(buildAuthnStatement(input, authenticationTime));
        assertion.getAttributeStatements().add(buildAttributeStatement(input));
        assertion.setConditions(buildConditions(input));
        assertion.setSubject(buildSubject(input, authenticationTime));
        AssertionSigner signingFactory = AssertionSigner.createWithCredential(input.signingCredential);
        return signingFactory.signAssertion(assertion);
    }

    private static Issuer buildIssuer(AuthnRequest input) {
        Issuer issuer = new IssuerBuilder().buildObject();
        issuer.setValue(input.issuer);
        return issuer;
    }

    private static AttributeStatement buildAttributeStatement(AuthnRequest input) {
        AttributeStatementBuilder attributeStatementBuilder =
                (AttributeStatementBuilder) XMLObjectSupport.getBuilder(AttributeStatement.DEFAULT_ELEMENT_NAME);
        AttributeStatement attrStatement = attributeStatementBuilder.buildObject();
        input.attributes.stream().map(AttributeConverter::convertAttribute).forEach(attrStatement.getAttributes()::add);
        return attrStatement;
    }

    private static AuthnStatement buildAuthnStatement(AuthnRequest input, DateTime authenticationTime) {
        AuthnStatement authnStatement = new AuthnStatementBuilder().buildObject();

        authnStatement.setAuthnInstant(authenticationTime);
        authnStatement.setSessionIndex(input.sessionId);
        authnStatement.setSessionNotOnOrAfter(authenticationTime.plus(input.maxSessionTimeoutInMinutes));

        AuthnContext authnContext = new AuthnContextBuilder().buildObject();

        AuthnContextClassRef authnContextClassRef = new AuthnContextClassRefBuilder().buildObject();
        authnContextClassRef.setAuthnContextClassRef(AuthnContext.PASSWORD_AUTHN_CTX);

        authnContext.setAuthnContextClassRef(authnContextClassRef);
        authnStatement.setAuthnContext(authnContext);
        return authnStatement;
    }

    private static Conditions buildConditions(AuthnRequest input) {
        Conditions conditions = new ConditionsBuilder().buildObject();
        Condition condition = new OneTimeUseBuilder().buildObject();
        conditions.getConditions().add(condition);

        AudienceRestriction audienceRestriction = new AudienceRestrictionBuilder().buildObject();

        Audience audience = new AudienceBuilder().buildObject();
        audience.setAudienceURI(input.audienceRestriction);

        audienceRestriction.getAudiences().add(audience);

        conditions.getAudienceRestrictions().add(audienceRestriction);
        return conditions;
    }

    private static Subject buildSubject(AuthnRequest input, DateTime authenticationTime) {
        SubjectConfirmationData confirmationData = new SubjectConfirmationDataBuilder().buildObject();
        confirmationData.setNotBefore(authenticationTime);
        confirmationData.setNotOnOrAfter(authenticationTime.plusMinutes(2));
        confirmationData.setRecipient(input.destinationUrl);

        SubjectConfirmation subjectConfirmation = new SubjectConfirmationBuilder().buildObject();
        subjectConfirmation.setSubjectConfirmationData(confirmationData);
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);

        Subject subject = new SubjectBuilder().buildObject();
        subject.setNameID(buildNameId(input));
        subject.getSubjectConfirmations().add(subjectConfirmation);
        return subject;
    }

    private static NameID buildNameId(AuthnRequest input) {
        NameID nameId = new NameIDBuilder().buildObject();
        nameId.setValue(input.nameId);
        return nameId;
    }
}
