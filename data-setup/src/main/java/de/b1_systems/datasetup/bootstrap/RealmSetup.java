package de.b1_systems.datasetup.bootstrap;

import org.keycloak.admin.client.Keycloak;
//import org.keycloak.client.registration.ClientRegistration;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.saml.SamlProtocol;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class RealmSetup {

    static final String URL = "http://localhost:8080/auth";
    static final String REALM = "example-realm";
    static final String CLIENT = "example-realm-client";
    static final String CLIENT_OIDC = CLIENT + "-oidc";
    static final String CLIENT_SAML = CLIENT + "-saml";

    private final Keycloak keycloak;

    public RealmSetup(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void execute() {
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setDisplayName(REALM);
        realmRepresentation.setId(REALM);
        realmRepresentation.setClients(new ArrayList<>());
        realmRepresentation.getClients().add(createClientOIDC(CLIENT_OIDC));
        // TODO: add SAML client creation
        //realmRepresentation.getClients().add(createClientSAML(CLIENT_SAML));
        realmRepresentation.setLoginWithEmailAllowed(true);
        realmRepresentation.setEnabled(true);
        realmRepresentation.setRealm(REALM);
        this.keycloak.realms().create(realmRepresentation);
    }

    //private List<ClientRepresentation> createClientOIDC(String clientId) {
    private ClientRepresentation createClientOIDC(String clientId) {
        ClientRepresentation client_oidc = new ClientRepresentation();
        client_oidc.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        client_oidc.setEnabled(true);
        // normally you wouldn't do this, but we use the direct grant to be able
        // to fetch the token for demo purposes per curl ;-)
        client_oidc.setDirectAccessGrantsEnabled(true);
        client_oidc.setId(clientId);
        client_oidc.setName(clientId);
        client_oidc.setPublicClient(Boolean.TRUE);
        
        return client_oidc;
    }

    // TODO: add SAML client creation
    // private ClientRepresentation createClientSAML(String clientId) {
    //     String token = "testToken";

    //     ClientRepresentation client_saml = new ClientRepresentation();
    //     client_saml.setProtocol(SamlProtocol.LOGIN_PROTOCOL);
    //     client_saml.setEnabled(true);
    //     client_saml.setId(clientId);
    //     client_saml.setName(clientId);
    //     client_saml.setPublicClient(Boolean.TRUE);
    //     client_saml.setStandardFlowEnabled(true);

    //     ClientRegistration reg = ClientRegistration.create()
    //     .url(URL, REALM)
    //     .build(); 
    //     reg.auth(Auth.token(token));
    //     client_saml = reg.create(client_saml);
    //     String registrationAccessToken = client_saml.getRegistrationAccessTplen();
    //     
    //     return client_saml;
    // }
}
