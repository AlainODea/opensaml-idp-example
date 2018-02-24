package com.alainodea.idp.service;

import com.alainodea.idp.model.AuthnRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public final class IdentityProviderClient {
    public static void authenticate(AuthnRequest authRequest) throws Exception {
        Response response = ResponseFactory.buildResponse(authRequest);
        IdentityProviderClient.authenticate(response);
    }

    private static void authenticate(Response response) throws MarshallingException, IOException {
        ResponseMarshaller responseMarshaller = new ResponseMarshaller();
        Element el = responseMarshaller.marshall(response);

        String originalAssertionString = XMLHelper.nodeToString(el);
        System.out.format("%n***** Assertion XML ******%n%n");
        System.out.println(originalAssertionString);

        String samlResponse = Base64.getEncoder().encodeToString(originalAssertionString.getBytes(StandardCharsets.UTF_8));
        HttpPost httpPost = new HttpPost(response.getDestination());

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("SAMLResponse", samlResponse));
        params.add(new BasicNameValuePair("RelayState", ""));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        System.out.format("%n***** Sending request to Okta ******%n%n");
        try (CloseableHttpClient httpClient = HttpClients.createSystem();
             CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
            System.out.println(httpPost);
            System.out.println(httpResponse.getStatusLine());
            Arrays.stream(httpResponse.getAllHeaders()).forEach(System.out::println);
            System.out.println(EntityUtils.toString(httpResponse.getEntity()));
        }
    }
}
