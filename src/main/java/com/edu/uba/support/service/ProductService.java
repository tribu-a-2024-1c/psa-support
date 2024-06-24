package com.edu.uba.support.service;

import com.edu.uba.support.dto.CreateProductDto;
import com.edu.uba.support.dto.CreateProductVersionDto;
import com.edu.uba.support.dto.ProductDto;
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
import java.util.Set;
import java.util.stream.Collectors;

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
	public ProductDto createProduct(CreateProductDto createProductDto) {
		logger.info("📦 Creating product with name: {}", createProductDto.getName());

		// Create and save product
		Product product = new Product();
		product.setName(createProductDto.getName());
		product = productRepository.save(product);

		// Create and save product version
		String version = createProductDto.getVersion();
		if (version != null) {
			ProductVersion productVersion = new ProductVersion();
			productVersion.setVersion(version);
			productVersion.setProduct(product);
			productVersionRepository.save(productVersion);
			logger.info("🔗 Created version '{}' for product '{}'", version, product.getName());
		}

		// Assign clients to product
		Set<Client> clients = createProductDto.getClients().stream().map(clientDto ->
				clientRepository.findById(clientDto.getId()).orElseGet(() -> {
					Client newClient = new Client();
					newClient.setId(clientDto.getId());
					newClient.setCompanyName(clientDto.getRazonSocial());
					newClient.setCuit(clientDto.getCuit());
					return clientRepository.save(newClient);
				})
		).collect(Collectors.toSet());

		product.setClients(clients);
		product = productRepository.save(product);

		// Map to ProductDto
		Set<ProductDto.ClientDto> clientDtos = clients.stream().map(client ->
				new ProductDto.ClientDto(client.getId(), client.getCompanyName(), client.getCuit())).collect(Collectors.toSet());
		ProductDto productDto = new ProductDto(product.getId(), product.getName(), version, clientDtos);

		logger.info("✅ Product created successfully with id: {}", product.getId());
		return productDto;
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

	@Transactional
	public List<ProductDto> getAllProducts() {
		logger.info("📦 Fetching all products");
		List<Product> products = productRepository.findAll();

		List<ProductDto> productDtos = products.stream().map(product -> {
			String version = product.getVersions().isEmpty() ? null : product.getVersions().iterator().next().getVersion(); // Assuming there's only one version
			Set<ProductDto.ClientDto> clientDtos = product.getClients().stream().map(client ->
					new ProductDto.ClientDto(client.getId(), client.getCompanyName(), client.getCuit())).collect(Collectors.toSet());
			return new ProductDto(product.getId(), product.getName(), version, clientDtos);
		}).collect(Collectors.toList());

		logger.info("✅ Fetched {} products", productDtos.size());
		return productDtos;
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
