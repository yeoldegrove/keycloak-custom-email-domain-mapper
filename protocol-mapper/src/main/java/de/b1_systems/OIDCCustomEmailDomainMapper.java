// purpose: A keycloak OIDC protocol mapper that
// returns an email address whose domain part is replaced with a custom domain.
// You can choose the OIDC Token Claim Name.
// copyright: B1 Systems GmbH <info@b1-systems.de>, 2023.
// license: Apacje License, Version 2.0, https://www.apache.org/licenses/LICENSE-2.0
// author: Eike Waldt <waldt@b1-systems.de>, 2023.
// kudos to:
//   - https://github.com/thomasdarimont/keycloak-extension-playground
//   - https://github.com/mschwartau/keycloak-custom-protocol-mapper-example

package de.b1_systems;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.models.UserModel;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.List;

/*
 * Our own example protocol mapper.
 */
public class OIDCCustomEmailDomainMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    /*
     * A config which keycloak uses to display a generic dialog to configure the token.
     */
    private static final List<ProviderConfigProperty> configProperties;

    /*
     * The ID of the token mapper. Is public, because we need this id in our data-setup project to
     * configure the protocol mapper in keycloak.
     */
    public static final String PROVIDER_ID = "oidc-custom-email-domain-mapper";

    public static final String EMAIL_DOMAIN = "emailDomain";
    public static final String DEFAULT_EMAIL_DOMAIN = "example.com";

    static {

        configProperties = ProviderConfigurationBuilder.create()
                .property()
                .name(EMAIL_DOMAIN)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Email Domain")
                .helpText("Return an email address where the domain part is replaced with this custom domain.")
                .defaultValue(DEFAULT_EMAIL_DOMAIN)
                .add()
                .build();

        // The builtin protocol mapper let the user define under which claim name (key)
        // the protocol mapper writes its value. To display this option in the generic dialog
        // in keycloak, execute the following method.
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        // The builtin protocol mapper let the user define for which tokens the protocol mapper
        // is executed (access token, id token, user info). To add the config options for the different types
        // to the dialog execute the following method. Note that the following method uses the interfaces
        // this token mapper implements to decide which options to add to the config. So if this token
        // mapper should never be available for some sort of options, e.g. like the id token, just don't
        // implement the corresponding interface.
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, OIDCCustomEmailDomainMapper.class);
    }

    @Override
    public String getDisplayCategory() {
        return "Token mapper";
    }

    @Override
    public String getDisplayType() {
        return "Custom Email Domain Mapper";
    }

    @Override
    public String getHelpText() {
        return "Returns an email address whose domain part is replaced with a custom domain.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    protected void setClaim(final IDToken token,
                            final ProtocolMapperModel mappingModel,
                            final UserSessionModel userSession,
                            final KeycloakSession keycloakSession,
                            final ClientSessionContext clientSessionCtx) {
        // Get User
        UserModel user = userSession.getUser();
        // Get Email
        String userEmail = user.getEmail();
        // Check if Email is empty and throw exception
        if (userEmail == null) {
            throw new RuntimeException("User email is null");
        }
        // Split Email
        String emailAddrSpec = userEmail.split("@")[0];
        // Define custome Email Domain or if empty a sane default
        String emailDomain = mappingModel.getConfig().getOrDefault(EMAIL_DOMAIN, DEFAULT_EMAIL_DOMAIN);
        // Concat Email AddressSpec with custom domain
        String claimValue = emailAddrSpec + "@" + emailDomain;
        // Finally, do the mapping with our custom claim.
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, claimValue);
    }

}