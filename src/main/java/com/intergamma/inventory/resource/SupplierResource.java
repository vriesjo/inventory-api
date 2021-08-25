package com.intergamma.inventory.resource;

import com.intergamma.inventory.domain.Supplier;
import com.intergamma.inventory.exception.BadRequestException;
import com.intergamma.inventory.exception.GenericException;
import com.intergamma.inventory.repository.SupplierRepository;
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
public class SupplierResource {

    private final Logger log = LoggerFactory.getLogger(SupplierResource.class);

    private static final String ENTITY_NAME = "supplier";

    @Value("${intergamma.api.name}")
    private String applicationName;

    private final SupplierRepository supplierRepository;

    public SupplierResource(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Operation(summary = "Create a new supplier")
    @ApiResponses(value = {
                    @ApiResponse(
                                    responseCode = "201",
                                    description = "Supplier created",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Supplier.class))
                                    }),
                    @ApiResponse(
                                    responseCode = "404",
                                    description = "Bad Request, if the supplier already has an ID.",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = GenericException.class))
                                    }
                    )
    })
    @PostMapping("/suppliers")
    public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody Supplier supplier) throws URISyntaxException {
        log.debug("Saving Supplier : {}", supplier);
        if (supplier.getId() != null) {
            throw new BadRequestException("Supplier has already an ID", ENTITY_NAME, "mustbeempty");
        }
        Supplier result = supplierRepository.save(supplier);

        return ResponseEntity
                        .created(new URI("/api/suppliers/" + result.getId()))
                        .headers(HeaderUtil.createdEntityCreatedHeaders(applicationName, ENTITY_NAME, result.getId().toString()))
                        .body(result);
    }

    @Operation(summary = "Update a supplier")
    @ApiResponses(value = {
                    @ApiResponse(
                                    responseCode = "200",
                                    description = "OK, supplier updated",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Supplier.class))
                                    }),
                    @ApiResponse(
                                    responseCode = "400",
                                    description = "Bad Request, if the supplier is not valid.",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = GenericException.class))
                                    }
                    ),
                    @ApiResponse(
                                    responseCode = "500",
                                    description = "Internal Server Error, if the supplier couldn't be updated",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = GenericException.class))
                                    }
                    )
    })
    @PutMapping("/suppliers/{id}")
    public ResponseEntity<Supplier> updateSupplier(
                    @PathVariable(value = "id", required = false) final Long id,
                    @Valid @RequestBody Supplier supplier
    ) {
        log.debug("Updating Supplier : {}, {}", id, supplier);
        if (supplier.getId() == null || !Objects.equals(id, supplier.getId())) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "invalid");
        }

        if (!supplierRepository.existsById(id)) {
            throw new BadRequestException("Supplier not found", ENTITY_NAME, "notfound");
        }

        Supplier result = supplierRepository.save(supplier);

        return ResponseEntity
                        .ok()
                        .headers(HeaderUtil.createEntityUpdatedHeaders(applicationName, ENTITY_NAME, supplier.getId().toString()))
                        .body(result);
    }

    @Operation(summary = "Get all suppliers")
    @ApiResponses(value = {
                    @ApiResponse(
                                    responseCode = "200",
                                    description = "OK, list of suppliers",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Supplier.class))
                                    })
    })
    @GetMapping("/suppliers")
    public List<Supplier> getAllSuppliers() {
        log.debug("Getting all Suppliers");
        return supplierRepository.findAll();
    }

    @Operation(summary = "Get the supplier by id")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200",
                                    description = "OK, with the supplier",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Supplier.class))
                                    }),
                    @ApiResponse(
                                    responseCode = "404",
                                    description = "Not found, if the supplier not exists.",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = GenericException.class))
                                    }
                    ),
    })
    @GetMapping("/suppliers/{id}")
    public ResponseEntity<Supplier> getSupplier(@PathVariable Long id) {
        log.debug("Getting Supplier : {}", id);
        Optional<Supplier> supplier = supplierRepository.findById(id);

        return ResponseUtil.wrapOrNotFound(supplier);
    }

    @Operation(summary = "Delete supplier by id")
    @ApiResponses(value = {
                    @ApiResponse(
                                    responseCode = "204",
                                    description = "NO_CONTENT, successful deleted",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Supplier.class))
                                    })
    })
    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        log.debug("Deleting Supplier : {}", id);
        supplierRepository.deleteById(id);

        return ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletedHeaders(applicationName, ENTITY_NAME, id.toString()))
                        .build();
    }
}
