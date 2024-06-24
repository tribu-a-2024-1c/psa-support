package com.edu.uba.support.service;

import com.edu.uba.support.dto.CreateProductDto;
import com.edu.uba.support.dto.CreateProductVersionDto;
import com.edu.uba.support.model.Client;
import com.edu.uba.support.model.Product;
import com.edu.uba.support.model.ProductVersion;
import com.edu.uba.support.repository.ClientRepository;
import com.edu.uba.support.repository.ProductRepository;
import com.edu.uba.support.repository.ProductVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	private final ProductRepository productRepository;
	private final ProductVersionRepository productVersionRepository;
	private final ClientRepository clientRepository;

	@Autowired
	public ProductService(ProductRepository productRepository, ProductVersionRepository productVersionRepository, ClientRepository clientRepository) {
		this.productRepository = productRepository;
		this.productVersionRepository = productVersionRepository;
		this.clientRepository = clientRepository;
	}

	@Transactional
	public Product createProduct(CreateProductDto createProductDto) {
		logger.info("📦 Creating product with name: {}", createProductDto.getName());
		Product product = new Product();
		product.setName(createProductDto.getName());
		Product savedProduct = productRepository.save(product);
		logger.info("✅ Product created successfully with id: {}", savedProduct.getId());
		return savedProduct;
	}

	@Transactional
	public ProductVersion createProductVersion(Long productId, CreateProductVersionDto createProductVersionDto) {
		logger.info("📦 Creating product version for product with id: {}", productId);
		Optional<Product> productOptional = productRepository.findById(productId);
		if (productOptional.isEmpty()) {
			logger.error("❌ Product with id '{}' not found", productId);
			throw new IllegalStateException("Product not found");
		}
		ProductVersion productVersion = new ProductVersion();
		productVersion.setVersion(createProductVersionDto.getVersion());
		productVersion.setProduct(productOptional.get());
		ProductVersion savedProductVersion = productVersionRepository.save(productVersion);
		logger.info("✅ Product version '{}' created successfully for product id: {}", savedProductVersion.getVersion(), productId);
		return savedProductVersion;
	}

	@Transactional
	public Product assignClientToProduct(Long productId, Long clientId) {
		logger.info("🔗 Assigning client with id '{}' to product with id '{}'", clientId, productId);
		Optional<Product> productOptional = productRepository.findById(productId);
		if (productOptional.isEmpty()) {
			logger.error("❌ Product with id '{}' not found", productId);
			throw new IllegalStateException("Product not found");
		}
		Optional<Client> clientOptional = clientRepository.findById(clientId);
		if (clientOptional.isEmpty()) {
			logger.error("❌ Client with id '{}' not found", clientId);
			throw new IllegalStateException("Client not found");
		}

		Product product = productOptional.get();
		Client client = clientOptional.get();
		product.getClients().add(client);
		client.getProducts().add(product);

		productRepository.save(product);
		clientRepository.save(client);

		logger.info("✅ Client with id '{}' assigned to product with id '{}'", clientId, productId);
		return product;
	}

	public List<Product> getProducts() {
		logger.info("🔍 Fetching all products");
		List<Product> products = productRepository.findAll();
		logger.info("✅ Found {} products", products.size());
		return products;
	}

	public List<ProductVersion> getProductVersions(Long productId) {
		logger.info("🔍 Fetching all versions for product with id: {}", productId);
		Optional<Product> productOptional = productRepository.findById(productId);
		if (productOptional.isEmpty()) {
			logger.error("❌ Product with id '{}' not found", productId);
			throw new IllegalStateException("Product not found");
		}
		List<ProductVersion> productVersions = productVersionRepository.findByProduct(productOptional.get());
		logger.info("✅ Found {} versions for product id: {}", productVersions.size(), productId);
		return productVersions;
	}
}
