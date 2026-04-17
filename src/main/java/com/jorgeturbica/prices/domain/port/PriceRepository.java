package com.jorgeturbica.prices.domain.port;

import com.jorgeturbica.prices.domain.model.Price;

import java.time.LocalDateTime;
import java.util.Optional;


public interface PriceRepository {

    Optional<Price> findApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate);
}
