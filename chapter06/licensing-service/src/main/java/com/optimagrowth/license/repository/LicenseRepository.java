package com.optimagrowth.license.repository;

import com.optimagrowth.license.model.License;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseRepository extends JpaRepository<License, String> {

    License findByOrganizationIdAndLicenseId(String organizationId, String licenseId);
}