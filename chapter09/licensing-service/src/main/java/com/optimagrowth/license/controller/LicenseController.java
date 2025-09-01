package com.optimagrowth.license.controller;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
import com.optimagrowth.license.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "v1/organization/{organizationId}/license")
public class LicenseController {

    private static Logger logger =
            LoggerFactory.getLogger(LicenseController.class);

    @Autowired
    private LicenseService licenseService;

    @GetMapping(value = "/{licenseId}")
    public ResponseEntity<License> getLicense(
            @PathVariable String organizationId,
            @PathVariable String licenseId) {

        License license = licenseService.getLicense(licenseId, organizationId);

        license.add(
                linkTo(methodOn(LicenseController.class).getLicense(organizationId, license.getLicenseId()))
                        .withSelfRel(),
                linkTo(methodOn(LicenseController.class).createLicense(organizationId, license, null))
                        .withRel("createLicense"),
                linkTo(methodOn(LicenseController.class).updateLicense(organizationId, license, null))
                        .withRel("updateLicense"),
                linkTo(methodOn(LicenseController.class).deleteLicense(organizationId, licenseId, null))
                        .withRel("deleteLicense")
        );

        return ResponseEntity.ok(license);
    }

    @GetMapping("/{licenseId}/{clientType}")
    public License getLicenseWithClient(@PathVariable String licenseId,
                                        @PathVariable String organizationId,
                                        @PathVariable String clientType) {

        return licenseService.getLicense(licenseId, organizationId, clientType);
    }

    @PostMapping
    public ResponseEntity<License> createLicense(
            @PathVariable String organizationId,
            @RequestBody License request,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale) {

        return ResponseEntity.ok(licenseService.createLicense(request));
    }

    @PutMapping
    public ResponseEntity<License> updateLicense(
            @PathVariable String organizationId,
            @RequestBody License request,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale) {

        return ResponseEntity.ok(licenseService.updateLicense(organizationId, request));
    }

    @DeleteMapping(value = "/{licenseId}")
    public ResponseEntity<License> deleteLicense(
            @PathVariable String organizationId,
            @PathVariable String licenseId,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale) {

        return ResponseEntity.ok(licenseService.deleteLicense(licenseId, organizationId, locale));
    }

    @GetMapping
    public List<License> getLicenses(@PathVariable("organizationId") String organizationId) throws TimeoutException {

        logger.debug(
                "LicenseServerController correlation-id: {}",
                UserContextHolder.getContext().getCorrelationId()
        );

        return licenseService.getLicensesByOrganization(organizationId);
    }
}
