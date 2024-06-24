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
			product.getVersions().add(productVersion);
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
		for (Client client : clients) {
			client.getProducts().add(product);
		}
		product = productRepository.save(product);

		// Map to ProductDto
		Set<ProductDto.ClientDto> clientDtos = clients.stream().map(client ->
				new ProductDto.ClientDto(client.getId(), client.getCompanyName(), client.getCuit())).collect(Collectors.toSet());
		ProductDto productDto = new ProductDto(product.getId(), product.getName(), version, clientDtos);

		logger.info("✅ Product created successfully with id: {}", product.getId());
		return productDto;
	}


	public ProductVersion createProductVersion(Long productId, CreateProductVersionDto createProductVersionDto) {
		logger.info("📦 Creating product version for product with id: {}", productId);
		Optional<Product> productOptional = productRepository.findById(productId);
		if (productOptional.isEmpty()) {
			logger.error("❌ Product with id '{}' not found", productId);
			throw new IllegalStateException("Product not found");
		}
		Product product = productOptional.get();

		// Check if the version already exists for the product
		boolean versionExists = product.getVersions().stream()
				.anyMatch(v -> v.getVersion().equals(createProductVersionDto.getVersion()));
		if (versionExists) {
			throw new IllegalStateException("The version already exists for this product");
		}

		ProductVersion productVersion = new ProductVersion();
		productVersion.setVersion(createProductVersionDto.getVersion());
		productVersion.setProduct(product);
		product.getVersions().add(productVersion);
		logger.info("✅ Product version '{}' created successfully for product id: {}", productVersion.getVersion(), productId);
		return productVersionRepository.save(productVersion);
	}


	@Transactional
	public ProductDto assignClientToProduct(Long productId, ProductDto.ClientDto clientDto) {
		logger.info("🔗 Assigning client to product with id '{}'", productId);
		Optional<Product> productOptional = productRepository.findById(productId);
		if (productOptional.isEmpty()) {
			logger.error("❌ Product with id '{}' not found", productId);
			throw new IllegalStateException("Product not found");
		}

		Product product = productOptional.get();
		Client client = clientRepository.findById(clientDto.getId()).orElseGet(() -> {
			Client newClient = new Client();
			newClient.setId(clientDto.getId());
			newClient.setCompanyName(clientDto.getRazonSocial());
			newClient.setCuit(clientDto.getCuit());
			return clientRepository.save(newClient);
		});

		product.getClients().add(client);
		client.getProducts().add(product);

		productRepository.save(product);
		clientRepository.save(client);

		// Map to ProductDto
		String version = product.getVersions().isEmpty() ? null : product.getVersions().iterator().next().getVersion();
		Set<ProductDto.ClientDto> clientDtos = product.getClients().stream().map(c ->
				new ProductDto.ClientDto(c.getId(), c.getCompanyName(), c.getCuit())).collect(Collectors.toSet());
		ProductDto productDto = new ProductDto(product.getId(), product.getName(), version, clientDtos);

		logger.info("✅ Client assigned to product with id '{}'", productId);
		return productDto;
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
