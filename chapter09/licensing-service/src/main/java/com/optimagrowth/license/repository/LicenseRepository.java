package com.optimagrowth.license.repository;

import com.optimagrowth.license.model.License;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LicenseRepository extends JpaRepository<License, String> {

    License findByOrganizationIdAndLicenseId(String organizationId, String licenseId);

    List<License> findByOrganizationId(String organizationId);
}