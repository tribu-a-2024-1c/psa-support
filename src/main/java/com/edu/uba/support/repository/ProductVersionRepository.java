package com.edu.uba.support.repository;

import com.edu.uba.support.model.ProductVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVersionRepository extends JpaRepository<ProductVersion, Long> {
}
