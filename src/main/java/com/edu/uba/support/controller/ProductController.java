package com.edu.uba.support.controller;

import com.edu.uba.support.dto.CreateProductDto;
import com.edu.uba.support.dto.CreateProductVersionDto;
import com.edu.uba.support.model.Product;
import com.edu.uba.support.model.ProductVersion;
import com.edu.uba.support.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;

	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping()
	@Operation(summary = "Create new product", description = "This endpoint allows creating a new product")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "The product was created successfully"),
			@ApiResponse(responseCode = "400", description = "The product could not be created"),
	})
	public ResponseEntity<Product> createProduct(@RequestBody CreateProductDto createProductDto) {
		try {
			Product product = productService.createProduct(createProductDto);
			return ResponseEntity.status(HttpStatus.CREATED).body(product);
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@PostMapping("/{productId}/version")
	@Operation(summary = "Create new product version", description = "This endpoint allows creating a new version for a product")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "The product version was created successfully"),
			@ApiResponse(responseCode = "400", description = "The product version could not be created"),
	})
	public ResponseEntity<ProductVersion> createProductVersion(@PathVariable Long productId, @RequestBody CreateProductVersionDto createProductVersionDto) {
		try {
			ProductVersion productVersion = productService.createProductVersion(productId, createProductVersionDto);
			return ResponseEntity.status(HttpStatus.CREATED).body(productVersion);
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@PostMapping("/{productId}/client/{clientId}")
	@Operation(summary = "Assign client to product", description = "This endpoint allows assigning a client to a product")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "The client was assigned to the product successfully"),
			@ApiResponse(responseCode = "404", description = "Product or client not found"),
	})
	public ResponseEntity<Product> assignClientToProduct(@PathVariable Long productId, @PathVariable Long clientId) {
		try {
			Product product = productService.assignClientToProduct(productId, clientId);
			return ResponseEntity.ok(product);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@GetMapping()
	@Operation(summary = "Get all products", description = "This endpoint allows getting all products")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "The products were retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "The products could not be retrieved"),
	})
	public ResponseEntity<List<Product>> getProducts() {
		try {
			Set<Product> products = productService.getProducts();
			List<Product> productList = new ArrayList<>(products);
			return ResponseEntity.status(HttpStatus.OK).body(productList);
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@GetMapping("/{productId}/versions")
	@Operation(summary = "Get all versions of a product", description = "This endpoint allows getting all versions of a product")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "The product versions were retrieved successfully"),
			@ApiResponse(responseCode = "404", description = "Product not found"),
			@ApiResponse(responseCode = "400", description = "The product versions could not be retrieved"),
	})
	public ResponseEntity<List<ProductVersion>> getProductVersions(@PathVariable Long productId) {
		try {
			List<ProductVersion> productVersions = productService.getProductVersions(productId);
			return ResponseEntity.status(HttpStatus.OK).body(productVersions);
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
}
