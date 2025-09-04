package com.optimagrowth.license.service.client;

import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.OrganizationRedisRepository;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class OrganizationRestTemplateClient {

    private static final Logger logger =
            LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    @Autowired
    OrganizationRedisRepository organizationRedisRepository;

    @Autowired
    KeycloakRestTemplate keycloakRestTemplate;


    public Organization getOrganization(String organizationId) {

        logger.debug(
                "Checking if organization info exists in the Redis cache"
        );

        Organization organization = checkRedisCache(organizationId);
        if (organization == null)
            logger.debug(
                    "Organization info with id {} does not exist in the Redis cache. " +
                            "It will be retrieved from the Organization Service",
                    organizationId
            );
        else
            logger.debug(
                    "Organization info with id {} was found in the Redis cache",
                    organizationId
            );

        ResponseEntity<Organization> responseEntity;
        try {
            responseEntity =
                    keycloakRestTemplate.getForEntity(
                            "http://gateway:8072/organization-service/v1/organization/{organizationId}",
                            Organization.class,
                            Map.of("organizationId", organizationId)
                    );
        } catch (RestClientException e) {
            logger.error("Get request has failed", e);
            return null;
        }

        organization = responseEntity.getBody();
        if (responseEntity.getBody() != null)
            cacheOrganizationObject(organization);

        return responseEntity.getBody();
    }

    private void cacheOrganizationObject(Organization organization) {
        try {
            organizationRedisRepository.save(organization);
        } catch (Exception e) {
            logger.error("Saving to Redis cache has failed");
        }
    }

    private Organization checkRedisCache(String organizationId) {
        try {
            return organizationRedisRepository.findById(organizationId).orElse(null);
        } catch (Exception e) {
            logger.error("Checking Redis cache has failed");
            return null;
        }
    }
}