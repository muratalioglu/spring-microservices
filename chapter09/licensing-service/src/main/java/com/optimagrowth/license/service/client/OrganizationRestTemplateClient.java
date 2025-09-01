package com.optimagrowth.license.service.client;

import com.optimagrowth.license.model.Organization;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Component
public class OrganizationRestTemplateClient {

    private static final Logger logger =
            LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    @Autowired
    KeycloakRestTemplate keycloakRestTemplate;

    public Organization getOrganization(String organizationId) {

        ResponseEntity<Organization> responseEntity;
        try {
            responseEntity =
                    keycloakRestTemplate.getForEntity(
                            "http://localhost:8072/organization-service/v1/organization/{organizationId}",
                            Organization.class,
                            Map.of("organizationId", organizationId)
                    );
        } catch (RestClientException e) {
            logger.error("Get request failed");
            return null;
        }

        return responseEntity.getBody();
    }
}