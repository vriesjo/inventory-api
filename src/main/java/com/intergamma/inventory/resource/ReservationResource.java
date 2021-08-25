package com.intergamma.inventory.resource;

import com.intergamma.inventory.domain.Reservation;
import com.intergamma.inventory.exception.BadRequestException;
import com.intergamma.inventory.exception.ServiceException;
import com.intergamma.inventory.repository.ProductRepository;
import com.intergamma.inventory.resource.util.HeaderUtil;
import com.intergamma.inventory.resource.util.ResponseUtil;
import com.intergamma.inventory.service.ReservationService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Transactional
public class ReservationResource {

    private final Logger log = LoggerFactory.getLogger(ReservationResource.class);

    private static final String ENTITY_NAME = "reservation";

    @Value("${intergamma.api.name}")
    private String applicationName;

    private final ReservationService reserveProductService;
    private ProductRepository productRepository;

    public ReservationResource(
                    final ReservationService reserveProductService,
                    final ProductRepository productRepository
    ) {
        this.reserveProductService = reserveProductService;
        this.productRepository = productRepository;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody Reservation reservation) throws URISyntaxException {
        Boolean isQuantityAllowed = null;
        try {
            isQuantityAllowed = reserveProductService.isQuantityAllowed(reservation);
        }
        catch (ServiceException e) {
            throw new BadRequestException("Product does not exist", ENTITY_NAME, "notexist");
        }

        if (!isQuantityAllowed) {
            throw new BadRequestException("Reserved quantity not allowed", ENTITY_NAME, "notallowed");
        }

        Reservation result = reserveProductService.addToCache(reservation);
        return ResponseEntity
                        .created(new URI("/api/reservations/" + result.getProductId()))
                        .headers(HeaderUtil.createdEntityCreatedHeaders(applicationName, ENTITY_NAME, result.getProductId().toString()))
                        .body(result);
    }

    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {
        return reserveProductService.getAllReservationsFromCache();
    }

    @GetMapping("/reservations/{sessionId}/{supplierId}/{productId}")
    public ResponseEntity<Reservation> getReservation(
                    @PathVariable String sessionId,
                    @PathVariable String supplierId,
                    @PathVariable String productId) {
        Optional<Reservation> result = Optional.ofNullable(reserveProductService.getReservationsBySessionAndSupplierAndProductFromCache(sessionId, supplierId, productId));
        return ResponseUtil.wrapOrNotFound(result);
    }

    @GetMapping("/reservations/{supplierId}/{productId}")
    public List<Reservation> getReservationsBySupplierAndProduct(
                    @PathVariable String supplierId,
                    @PathVariable String productId) {
        return reserveProductService.getReservationsBySupplierAndProductFromCache(supplierId, productId);
    }

}