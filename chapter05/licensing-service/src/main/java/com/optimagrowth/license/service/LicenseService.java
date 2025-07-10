package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.UUID;

@Service
public class LicenseService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    ServiceConfig config;

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
}
