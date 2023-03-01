// purpose: A keycloak SAML protocol mapper that
// returns an email address whose domain part is replaced with a custom domain.
// You can choose the SAML AttributeStatement name.
// copyright: B1 Systems GmbH <info@b1-systems.de>, 2023.
// license: Apacje License, Version 2.0, https://www.apache.org/licenses/LICENSE-2.0
// author: Eike Waldt <waldt@b1-systems.de>, 2023.
// kudos to:
//   - https://github.com/thomasdarimont/keycloak-extension-playground
//   - https://github.com/mschwartau/keycloak-custom-protocol-mapper-example

package de.b1_systems;

import org.keycloak.dom.saml.v2.assertion.AttributeStatementType;
import org.keycloak.dom.saml.v2.assertion.AttributeType;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.models.UserModel;
import org.keycloak.protocol.saml.mappers.AbstractSAMLProtocolMapper;
import org.keycloak.protocol.saml.mappers.AttributeStatementHelper;
import org.keycloak.protocol.saml.mappers.SAMLAttributeStatementMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.List;

/*
 * Our own example protocol mapper.
 */
public class SAMLCustomEmailDomainMapper extends AbstractSAMLProtocolMapper implements SAMLAttributeStatementMapper {

    /*
     * A config which keycloak uses to display a generic dialog to configure the token.
     */
    private static final List<ProviderConfigProperty> configProperties;

    /*
     * The ID of the token mapper. Is public, because we need this id in our data-setup project to
     * configure the protocol mapper in keycloak.
     */
    public static final String PROVIDER_ID = "saml-custom-email-domain-mapper";

    public static final String EMAIL_DOMAIN = "emailDomain";
    public static final String DEFAULT_EMAIL_DOMAIN = "example.com";
    public static final String SAML_ATTRIBUTE_NAME = "SAMLAttributeStatement";
    public static final String DEFAULT_SAML_ATTRIBUTE_NAME = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress";
    public static final String SAML_ATTRIBUTE_NAMEFORMAT = "SAMLAttributeFormat";

    static {

        configProperties = ProviderConfigurationBuilder.create()
                .property()
                .name(EMAIL_DOMAIN)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Email Domain")
                .helpText("Return an email address where the domain part is replaced with this custom domain.")
                .defaultValue(DEFAULT_EMAIL_DOMAIN)
                .add()
                .property()
                .name(SAML_ATTRIBUTE_NAME)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("SAML Attribute Statement")
                .helpText("Set name of SAML Attribute Statement to add")
                .defaultValue(DEFAULT_SAML_ATTRIBUTE_NAME)
                .add()
                .property()
                .name(SAML_ATTRIBUTE_NAMEFORMAT)
                .type(ProviderConfigProperty.LIST_TYPE)
                .label("SAML Attribute Format")
                .helpText("Set format of SAML Attribute Statement to add")
                .options(
                    "urn:oasis:names:tc:SAML:2.0:attrname-format:uri",
                    "urn:oasis:names:tc:SAML:2.0:attrname-format:basic",
                    "urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified"
                )
                .add()
                .build();
    }

    @Override
    public String getDisplayCategory() {
        return AttributeStatementHelper.ATTRIBUTE_STATEMENT_CATEGORY;        
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
    public void transformAttributeStatement(AttributeStatementType attributeStatement,
                                            ProtocolMapperModel mappingModel,
                                            KeycloakSession session,
                                            UserSessionModel userSession,
                                            AuthenticatedClientSessionModel clientSession) {
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
        // Set attribute name
        String attributeName = mappingModel.getConfig().getOrDefault(SAML_ATTRIBUTE_NAME, DEFAULT_SAML_ATTRIBUTE_NAME);
        // Set attribute format
        String attributeFormat = mappingModel.getConfig().get(SAML_ATTRIBUTE_NAMEFORMAT);
        // Concat Email AddressSpec with custom domain
        String attributeValue = emailAddrSpec + "@" + emailDomain;
        // Finally, do the mapping with our custom claim.
        AttributeType attribute = new AttributeType("attribute");
        attribute.setFriendlyName("Email from Custom Email Domain Mapper");
        attribute.setNameFormat(attributeFormat);
        attribute.setName(attributeName);
        attribute.addAttributeValue(attributeValue);
        attributeStatement.addAttribute(new AttributeStatementType.ASTChoiceType(attribute));
    }

}