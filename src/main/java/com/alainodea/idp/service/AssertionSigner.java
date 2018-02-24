package com.alainodea.idp.service;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.impl.AssertionMarshaller;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureBuilder;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;

final class AssertionSigner {
    private final Credential signingCredential;

    static AssertionSigner createWithCredential(Credential signingCredential) {
        return new AssertionSigner(signingCredential);
    }

    private AssertionSigner(Credential signingCredential) {
        this.signingCredential = signingCredential;
    }

    Assertion signAssertion(Assertion assertion) throws MarshallingException, SignatureException {
        SignatureBuilder builder = new SignatureBuilder();
        Signature signature = builder.buildObject();

        signature.setSigningCredential(signingCredential);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

        assertion.setSignature(signature);

        addXmlSignatureInstanceToAssertion(assertion);
        Signer.signObject(signature);

        return assertion;
    }

    private void addXmlSignatureInstanceToAssertion(Assertion assertion) throws MarshallingException {
        AssertionMarshaller marshaller = new AssertionMarshaller();
        marshaller.marshall(assertion);
    }
}
