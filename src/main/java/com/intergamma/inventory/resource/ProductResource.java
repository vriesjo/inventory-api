package com.intergamma.inventory.resource;

import com.intergamma.inventory.domain.Product;
import com.intergamma.inventory.exception.BadRequestException;
import com.intergamma.inventory.exception.GenericException;
import com.intergamma.inventory.repository.ProductRepository;
import com.intergamma.inventory.resource.util.HeaderUtil;
import com.intergamma.inventory.resource.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Transactional
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);

    private static final String ENTITY_NAME = "product";

    @Value("${intergamma.api.name}")
    private String applicationName;

    private final ProductRepository productRepository;

    public ProductResource(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
                    @ApiResponse(
                                    responseCode = "201",
                                    description = "Product created",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Product.class))
                                    }),
                    @ApiResponse(
                                    responseCode = "404",
                                    description = "Bad Request, if the product already has an ID.",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = GenericException.class))
                                    }
                    )
    })
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) throws URISyntaxException {
        log.debug("Saving Product : {}", product);
        if (product.getId() != null) {
            throw new BadRequestException("Product has already an ID", ENTITY_NAME, "mustbeempty");
        }
        Product productAlreadyExists = productRepository.findProductByCode(product.getCode());
        if (productAlreadyExists != null) {
            throw new BadRequestException("Product with unique code already exists", ENTITY_NAME, "alreadyexists");
        }

        Product result = productRepository.save(product);

        return ResponseEntity
                        .created(new URI("/api/products/" + result.getId()))
                        .headers(HeaderUtil.createdEntityCreatedHeaders(applicationName, ENTITY_NAME, result.getId().toString()))
                        .body(result);
    }

    @Operation(summary = "Update a product")
    @ApiResponses(value = {
                    @ApiResponse(
                                    responseCode = "200",
                                    description = "OK, product updated",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Product.class))
                                    }),
                    @ApiResponse(
                                    responseCode = "400",
                                    description = "Bad Request, if the product is not valid.",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = GenericException.class))
                                    }
                    ),
                    @ApiResponse(
                                    responseCode = "500",
                                    description = "Internal Server Error, if the product couldn't be updated",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = GenericException.class))
                                    }
                    )
    })
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(
                    @PathVariable(value = "id", required = false) final Long id,
                    @Valid @RequestBody Product product
    ) {
        log.debug("Updating Product : {}, {}", id, product);
        if (product.getId() == null || !Objects.equals(id, product.getId())) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "invalid");
        }

        if (!productRepository.existsById(id)) {
            throw new BadRequestException("Product not found", ENTITY_NAME, "notfound");
        }

        Product result = productRepository.save(product);

        return ResponseEntity
                        .ok()
                        .headers(HeaderUtil.createEntityUpdatedHeaders(applicationName, ENTITY_NAME, product.getId().toString()))
                        .body(result);
    }

    @Operation(summary = "Get all products")
    @ApiResponses(value = {
                    @ApiResponse(
                                    responseCode = "200",
                                    description = "OK, list of products",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Product.class))
                                    })
    })
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        log.debug("Getting all Products");
        return productRepository.findAll();
    }

    @Operation(summary = "Get the product by id")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200",
                                    description = "OK, with the product",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Product.class))
                                    }),
                    @ApiResponse(
                                    responseCode = "404",
                                    description = "Not found, if the product not exists.",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = GenericException.class))
                                    }
                    ),
    })
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        log.debug("Getting Product by id : {}", id);
        Optional<Product> product = productRepository.findById(id);

        return ResponseUtil.wrapOrNotFound(product);
    }

    @Operation(summary = "Delete product by id")
    @ApiResponses(value = {
                    @ApiResponse(
                                    responseCode = "204",
                                    description = "NO_CONTENT, successful deleted",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Product.class))
                                    })
    })
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("Deleting Product : {}", id);
        productRepository.deleteById(id);

        return ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletedHeaders(applicationName, ENTITY_NAME, id.toString()))
                        .build();
    }
}
