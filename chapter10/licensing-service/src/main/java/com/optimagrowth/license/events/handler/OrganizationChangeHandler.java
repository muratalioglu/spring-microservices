package com.optimagrowth.license.events.handler;

import com.optimagrowth.license.events.model.OrganizationChangeModel;
import com.optimagrowth.license.repository.OrganizationRedisRepository;
import com.optimagrowth.license.service.client.CustomChannels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(CustomChannels.class)
public class OrganizationChangeHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(OrganizationChangeHandler.class);

    private OrganizationRedisRepository organizationRedisRepository;

    @StreamListener("inboundOrgChanges")
    public void loggerSink(OrganizationChangeModel organizationChangeModel) {

        logger.debug(
                "Received a message of type: {}",
                organizationChangeModel.getType()
        );

        logger.debug(
                "Received a message with an event {} from the Organization service for the Organization id {}",
                organizationChangeModel.getType(),
                organizationChangeModel.getOrganizationId()
        );
    }
}