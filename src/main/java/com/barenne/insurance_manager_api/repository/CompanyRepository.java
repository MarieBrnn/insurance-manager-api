package com.barenne.insurance_manager_api.repository;

import com.barenne.insurance_manager_api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByCompanyIdentifier(String companyIdentifier);

    Optional<Company> findByCompanyIdentifier(String companyIdentifier);

}
