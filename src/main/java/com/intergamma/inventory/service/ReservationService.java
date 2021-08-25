package com.intergamma.inventory.service;

import com.intergamma.inventory.domain.Reservation;
import com.intergamma.inventory.exception.ServiceException;
import com.intergamma.inventory.repository.ProductRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private RedisTemplate<String, Object> redisTemplate;
    private ProductRepository productRepository;

    public ReservationService(
                    final RedisTemplate<String, Object> redisTemplate,
                    final ProductRepository productRepository) {
        this.redisTemplate = redisTemplate;
        this.productRepository = productRepository;
    }

    public Boolean isQuantityAllowed(Reservation reservation) {
        Integer productStockQuantity = productRepository.findById(reservation.getProductId())
                        .orElseThrow(() -> new ServiceException("Product not found"))
                        .getQuantity();
        Integer productQuantityAlreadyReserved = getReservationsBySupplierAndProductFromCache(reservation.getSupplierId().toString(), reservation.getProductId().toString())
                        .stream()
                        .map(r -> r.getQuantity())
                        .reduce((q1, q2) -> q1 + q2)
                        .orElse(0);
        if (productQuantityAlreadyReserved + reservation.getQuantity() > productStockQuantity) {
            return false;
        }
        return true;
    }

    public List<Reservation> getAllReservationsFromCache() {
        Set<String> keys = redisTemplate.keys("*");
        return keys.stream()
                        .map(s -> redisTemplate.boundValueOps(s))
                        .map(value -> (Reservation) value.get())
                        .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "reservationCache", key = "'key_'.concat(#sessionId).concat('_').concat(#supplierId).concat('_').concat(#productId)")
    public Reservation getReservationsBySessionAndSupplierAndProductFromCache(String sessionId, String supplierId, String productId) {
        return null;
    }


    @CachePut(cacheNames = "reservationCache", key = "'key_'.concat(#reservation.sessionId).concat('_').concat(#reservation.supplierId).concat('_').concat(#reservation.productId)")
    public Reservation addToCache(final Reservation reservation) {
        return reservation;
    }

    public List<Reservation> getReservationsBySupplierAndProductFromCache(String supplierId, String productId) {
        Set<String> keys = redisTemplate.keys("*_".concat(supplierId).concat("_").concat(productId));
        return keys.stream()
                        .map(s -> redisTemplate.boundValueOps(s))
                        .map(value -> (Reservation) value.get())
                        .collect(Collectors.toList());
    }
}
