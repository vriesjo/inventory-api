package com.intergamma.inventory.resource;

import com.intergamma.inventory.domain.Category;
import com.intergamma.inventory.exception.BadRequestException;
import com.intergamma.inventory.exception.GenericException;
import com.intergamma.inventory.repository.CategoryRepository;
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
public class CategoryResource {

    private final Logger log = LoggerFactory.getLogger(CategoryResource.class);

    private static final String ENTITY_NAME = "category";

    @Value("${intergamma.api.name}")
    private String applicationName;

    private final CategoryRepository categoryRepository;

    public CategoryResource(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
                    @ApiResponse(
                                    responseCode = "201",
                                    description = "Category created",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Category.class))
                                    }),
                    @ApiResponse(
                                    responseCode = "404",
                                    description = "Bad Request, if the category already has an ID.",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = GenericException.class))
                                    }
                    )
    })
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) throws URISyntaxException {
        log.debug("Saving Category : {}", category);
        if (category.getId() != null) {
            throw new BadRequestException("Category has already an ID", ENTITY_NAME, "mustbeempty");
        }
        Category result = categoryRepository.save(category);

        return ResponseEntity
                        .created(new URI("/api/categories/" + result.getId()))
                        .headers(HeaderUtil.createdEntityCreatedHeaders(applicationName, ENTITY_NAME, result.getId().toString()))
                        .body(result);
    }

    @Operation(summary = "Update a category")
    @ApiResponses(value = {
                    @ApiResponse(
                                    responseCode = "200",
                                    description = "OK, category updated",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Category.class))
                                    }),
                    @ApiResponse(
                                    responseCode = "400",
                                    description = "Bad Request, if the category is not valid.",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = GenericException.class))
                                    }
                    ),
                    @ApiResponse(
                                    responseCode = "500",
                                    description = "Internal Server Error, if the category couldn't be updated",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = GenericException.class))
                                    }
                    )
    })
    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(
                    @PathVariable(value = "id", required = false) final Long id,
                    @Valid @RequestBody Category category
    ) {
        log.debug("Updating Category : {}, {}", id, category);
        if (category.getId() == null || !Objects.equals(id, category.getId())) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "invalid");
        }

        if (!categoryRepository.existsById(id)) {
            throw new BadRequestException("Category not found", ENTITY_NAME, "notfound");
        }

        Category result = categoryRepository.save(category);

        return ResponseEntity
                        .ok()
                        .headers(HeaderUtil.createEntityUpdatedHeaders(applicationName, ENTITY_NAME, category.getId().toString()))
                        .body(result);
    }

    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
                    @ApiResponse(
                                    responseCode = "200",
                                    description = "OK, list of categories",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Category.class))
                                    })
    })
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        log.debug("Getting all Categories");
        return categoryRepository.findAll();
    }

    @Operation(summary = "Get the category by id")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200",
                                    description = "OK, with the category",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Category.class))
                                    }),
                    @ApiResponse(
                                    responseCode = "404",
                                    description = "Not found, if the category not exists.",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = GenericException.class))
                                    }
                    ),
    })
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        log.debug("Getting Category by id : {}", id);
        Optional<Category> category = categoryRepository.findById(id);

        return ResponseUtil.wrapOrNotFound(category);
    }

    @Operation(summary = "Delete category by id")
    @ApiResponses(value = {
                    @ApiResponse(
                                    responseCode = "204",
                                    description = "NO_CONTENT, successful deleted",
                                    content = {
                                                    @Content(
                                                                    mediaType = "application/json",
                                                                    schema = @Schema(implementation = Category.class))
                                    })
    })
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.debug("Deleting Category by id : {}", id);
        categoryRepository.deleteById(id);

        return ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletedHeaders(applicationName, ENTITY_NAME, id.toString()))
                        .build();
    }
}
