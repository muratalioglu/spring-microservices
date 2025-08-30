package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.client.OrganizationDiscoveryClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
public class LicenseService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    ServiceConfig config;

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;

    public License getLicense(String licenseId, String organizationId) {

        License license = getLicenseByOrganization(organizationId, licenseId);

        return license.withComment(config.getProperty());
    }

    public License createLicense(License license) {

        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    public License updateLicense(String organizationId, License license) {

        String licenseId = license.getLicenseId();

        License existingLicense = getLicenseByOrganization(organizationId, licenseId);

        existingLicense.setLicenseType(license.getLicenseType());
        existingLicense.setDescription(license.getDescription());
        existingLicense.setProductName(license.getProductName());
        existingLicense.setComment(license.getComment());

        licenseRepository.save(existingLicense);
        return license.withComment(config.getProperty());
    }

    public License deleteLicense(String licenseId, String organizationId, Locale locale) {

        License license = getLicenseByOrganization(organizationId, licenseId);

        licenseRepository.delete(license);

        return license.withComment(config.getProperty());
    }

    private License getLicenseByOrganization(String organizationId, String licenseId) {
        License license;
        license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);

        if (license == null)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                            messageSource.getMessage(
                                    "license.search.error.message", null, null
                            ),
                            licenseId, organizationId
                    )
            );
        return license;
    }

    public License getLicense(String licenseId, String organizationId, String clientType) {

        License license = getLicenseByOrganization(organizationId, licenseId);

        Organization organization = retrieveOrganization(organizationId, clientType);
        if (organization != null) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }

        return license.withComment(config.getProperty());
    }

    @CircuitBreaker(name = "licenseService", fallbackMethod = "getLicensesFallback")
//    @Bulkhead(name = "bulkheadLicenseService", fallbackMethod = "getLicenseFallback", type = Bulkhead.Type.SEMAPHORE)
    public List<License> getLicensesByOrganization(String organizationId) throws TimeoutException {
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    private void randomlyRunLong() throws TimeoutException {
        try {
            Thread.sleep(2000);
            throw new TimeoutException();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    private List<License> getLicensesFallback(String organizationId, Throwable throwable) {
        License license = new License();
        license.setOrganizationId(organizationId);
        license.setLicenseId("0000000-00-00000");
        license.setProductName("-No licensing information currently available-");
        return List.of(license);
    }

    private Organization retrieveOrganization(String organizationId, String clientType) {

        Organization organization = null;

        switch (clientType) {
            case "discovery":
                System.out.println("Using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
        }

        return organization;
    }
}
