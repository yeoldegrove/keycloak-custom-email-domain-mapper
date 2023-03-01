package de.b1_systems;

import org.junit.jupiter.api.Test;
import org.keycloak.protocol.saml.mappers.AttributeStatementHelper;
import org.keycloak.protocol.saml.mappers.UserAttributeStatementMapper;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SAMLCustomEmailDomainMapperTest {

    public static final String EMAIL_DOMAIN = "emailDomain";
    public static final String SAML_ATTRIBUTE_NAME = "SAMLAttributeStatement";
    public static final String SAML_ATTRIBUTE_NAMEFORMAT = "SAMLAttributeFormat";

    @Test
    public void shouldAttributeStatementMapperDisplayCategory() {
        final String attributeStatementMapperDisplayCategory = new UserAttributeStatementMapper().getDisplayCategory();
        assertThat(new SAMLCustomEmailDomainMapper().getDisplayCategory()).isEqualTo(attributeStatementMapperDisplayCategory);
    }

    @Test
    public void shouldHaveDisplayType() {
        assertThat(new SAMLCustomEmailDomainMapper().getDisplayType()).isNotBlank();
    }

    @Test
    public void shouldHaveHelpText() {
        assertThat(new SAMLCustomEmailDomainMapper().getHelpText()).isNotBlank();
    }

    @Test
    public void shouldHaveIdId() {
        assertThat(new SAMLCustomEmailDomainMapper().getId()).isNotBlank();
    }

    @Test
    public void shouldHaveProperties() {
        final List<String> configPropertyNames = new SAMLCustomEmailDomainMapper().getConfigProperties().stream()
                .map(ProviderConfigProperty::getName)
                .collect(Collectors.toList());
        assertThat(configPropertyNames).containsExactly(AttributeStatementHelper.SAML_ATTRIBUTE_NAME, AttributeStatementHelper.SAML_ATTRIBUTE_NAMEFORMAT, AttributeStatementHelper.URI_REFERENCE);
    }

    // TODO: add more tests

}