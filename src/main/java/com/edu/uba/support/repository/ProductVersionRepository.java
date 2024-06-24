package com.edu.uba.support.repository;

import com.edu.uba.support.model.ProductVersion;
import com.edu.uba.support.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVersionRepository extends JpaRepository<ProductVersion, Long> {
	List<ProductVersion> findByProduct(Product product);
}
