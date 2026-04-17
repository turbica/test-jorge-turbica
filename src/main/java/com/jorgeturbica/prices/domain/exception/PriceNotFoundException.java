package com.jorgeturbica.prices.domain.exception;

import java.time.LocalDateTime;

public class PriceNotFoundException extends RuntimeException {

    public PriceNotFoundException(Long productId, Long brandId, LocalDateTime applicationDate) {
        super("No applicable price found for productId=%d, brandId=%d at %s".formatted(productId, brandId, applicationDate));
    }
}
