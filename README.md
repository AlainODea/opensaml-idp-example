# opensaml-idp-example
Example SAML 2.0 IdP written on OpenSAML to test an Okta ACS crash.

Fixed as of Okta 2018.11 in Okta Preview. Good work, Okta Engineering!

## Thanks
* [WhiteFang34](https://stackoverflow.com/users/653230/whitefang34):
  [How to create a valid SAML 2.0 Assertion with OpenSAML library in Java](
  https://stackoverflow.com/a/34579802/154527)
* [Prateek](https://stackoverflow.com/users/2929562/prateek):
  [XML Document to String](https://stackoverflow.com/a/5456836/154527)
* @chris-whittick for refactoring examples to work with OpenSAML 3.x 

## Motivation
It was built to send arbitrary attributes to an ACS in order to
identify which attributes cause problems.

In particular, it was built to debug a compatibility issue between
Okta and AD FS Device Registration. Okta will not allow assertions
with isRegisteredUser attributes due to a bug. It causes a crash and
forces AD FS users with this enabled to perform workarounds like using
private browsing mode to suppress Device Registration.

## Limitations
This is not suitable for use as an actual Identity Provider, but it has
elements you could remix into one.

## Quick Start

 1. Generate a throwaway PKCS#12 keystore:
  ```
  keytool -genkey -alias 1 -validity 365 -keyalg RSA -keysize 2048 \
          -keystore idp-signing.p12 -storetype pkcs12 \
          -storepass changeit \
          -dname 'CN=opensaml-example-idp Signing'
  ```
 2. Export the public key:
  ```
  keytool -exportcert -alias 1 \
          -keystore idp-signing.p12 -storetype pkcs12 \
          -storepass changeit > idp-signing.crt
  ```
 3. Create an Identity Provider in Okta Preview
    * Set **Name** to opensaml-example-idp
    * Leave **IdP Username** as is
    * Check **Only allow usernames that match
    defined RegEx Pattern** and set pattern to `^$`
      * Prevents succesful login with this IdP without preventing logs
    * Set **If no match is found** to Redirect to Okta sign-in page
    * Set **IdP Issuer URI** to
    `https://acme.example.com/saml2/identity-provider`
    * Set **IdP Single Sign-On URL** to
    `https://acme.example.com/saml2/service-provider`
    * Set **IdP Signature Certificate** by uploading **idp-signing.crt**
 4. Copy/paste **idp.properties.template** to **idp.properties**
 5. Modify **idp.properties**:
    * Set **issuer** to
    `https://acme.example.com/saml2/identity-provider`
    * Set **audienceRestriction** to be the Audience URI from the
    Identity Provider you created in Okta in step 3
    * Set **destinationUrl** to be the Assertion Consumer Service URL
    from the Identity Provider you created in Okta in step 3

Note: you can adapt the guide above. It's intended as a quick start.

## Regenerating IdP Signature Certificate

 1. Delete the expired keystore:
  ```
  rm idp-signing.p12
  ```
 2. Generate a new throwaway PKCS#12 keystore:
  ```
  keytool -genkey -alias 1 -validity 365 -keyalg RSA -keysize 2048 \
          -keystore idp-signing.p12 -storetype pkcs12 \
          -storepass changeit \
          -dname 'CN=opensaml-example-idp Signing'
  ```
 3. Export the public key:
  ```
  keytool -exportcert -alias 1 \
          -keystore idp-signing.p12 -storetype pkcs12 \
          -storepass changeit > idp-signing.crt
  ```
 4. Edit the opensaml-example-idp Identity Provider in Okta Preview
   * Set **IdP Signature Certificate** by uploading **idp-signing.crt**

## Running

### Run example that Okta's ACS can handle

```
./gradlew run -PappArgs="works-for-okta"
```

It should fail with GENERAL_NONSUCCESS if you configured Okta with a
Filter that rejects all usernames (as directed in the Quick Start).

Get the X-Okta-Request-Id from the program output.
In Okta, go to Reports > System Log and search for:

```
transaction.id eq "{PUT TRANSACTION ID HERE}"
```

You should see three log events:
 1. FAILURE : Unable to match transformed username
    * This is due to the Filter regex we deliberately set to block logins
 2. FAILURE : User Denied/Rejected
    * This seems to a be a roll-up of the over errors
 3. FAILURE : Unknown Profile Attribute
    * Expand Event > System > DebugContext > DebugData: you should see
      AttributeNames `[thisIsFine]` (that's an XSAny attribute
      resulting from Object attribute in **AttributeExamples.java**)
      
      Okta normally ignores bad attributes, which it does here.

### Run example that crashes Okta's ACS

```
./gradlew run -PappArgs="crash-okta"
```

Get the X-Okta-Request-Id and go to Reports > System Log and search
for:

```
transaction.id eq "{PUT TRANSACTION ID HERE}"
```

If the bug still exists, no events will match.

Ask Okta Support to find the crash log by providing them with the
X-Okta-Request-Id.

Arguably, Okta should ignore the `isRegisteredUser` attribute, but I
understand that it causes a **ClassCastException** in the ACS backend
and never gets logged to the System Log we can see as customers.

## Extending

You can add to **AttributeExamples.java** pretty easily:

 1. Copy/paste one of the example attribute methods
 2. Add a constant with a value you want to pass from the command-line
 3. Add that constant as a case inthe switch statement
