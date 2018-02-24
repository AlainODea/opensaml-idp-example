package com.alainodea.idp.model;

import com.alainodea.idp.attributes.NamedAttribute;
import org.opensaml.security.credential.Credential;

import java.time.Instant;
import java.util.List;

public final class AuthnRequest {
    public Instant authenticationInstant;
    public String issuer;
    public String audienceRestriction;
    public String destinationUrl;
    public String nameId;
    public String sessionId;
    public int maxSessionTimeoutInMinutes = 15;
    public List<NamedAttribute> attributes;
    public Credential signingCredential;
}
