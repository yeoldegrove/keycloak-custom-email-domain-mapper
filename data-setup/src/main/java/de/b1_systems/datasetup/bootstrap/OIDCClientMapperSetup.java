package de.b1_systems.datasetup.bootstrap;
import de.b1_systems.OIDCCustomEmailDomainMapper;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.protocol.oidc.mappers.GroupMembershipMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;

import java.util.HashMap;
import java.util.Map;

public class OIDCClientMapperSetup {

    public static final String PROTOCOL = "openid-connect";
    private final Keycloak keycloak;

    public OIDCClientMapperSetup(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void execute() {
        final ClientResource client = this.keycloak.realm(RealmSetup.REALM).clients().get(RealmSetup.CLIENT_OIDC);
        client.getProtocolMappers().createMapper(createGroupMapper());
        client.getProtocolMappers().createMapper(createCustomEmailDomainMapper());
    }

    private ProtocolMapperRepresentation createGroupMapper() {
        ProtocolMapperRepresentation protocolMapperRepresentation = new ProtocolMapperRepresentation();
        protocolMapperRepresentation.setProtocolMapper(GroupMembershipMapper.PROVIDER_ID);
        protocolMapperRepresentation.setProtocol(PROTOCOL);
        protocolMapperRepresentation.setName("Group mapper");
        Map<String, String> config = new HashMap<>();
        putAccessTokenClaim(config);
        // the name of the property we got from the class GroupMembershipMapper
        config.put("full.path", "true");
        config.put(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME, "groups");
        protocolMapperRepresentation.setConfig(config);
        return protocolMapperRepresentation;
    }

    private ProtocolMapperRepresentation createCustomEmailDomainMapper() {
        ProtocolMapperRepresentation protocolMapperRepresentation = new ProtocolMapperRepresentation();
        protocolMapperRepresentation.setProtocolMapper(OIDCCustomEmailDomainMapper.PROVIDER_ID);
        protocolMapperRepresentation.setProtocol(PROTOCOL);
        protocolMapperRepresentation.setName("Custom Email Domain Mapper");
        Map<String, String> config = new HashMap<>();
        putAccessTokenClaim(config);
        config.put(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME, "email");
        protocolMapperRepresentation.setConfig(config);
        return protocolMapperRepresentation;
    }

    static void putAccessTokenClaim(Map<String, String> config) {
        config.put("access.token.claim", "true");
    }
}
