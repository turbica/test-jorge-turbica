package com.jorgeturbica.prices.application.port;

import com.jorgeturbica.prices.domain.model.Price;

import java.time.LocalDateTime;


public interface PriceService {

    Price getApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate);
}
