package com.jorgeturbica.prices.infrastructure.rest.controller;

import com.jorgeturbica.prices.application.port.PriceService;
import com.jorgeturbica.prices.domain.model.Price;
import com.jorgeturbica.prices.infrastructure.rest.generated.api.PricesApi;
import com.jorgeturbica.prices.infrastructure.rest.generated.model.PriceResponse;
import com.jorgeturbica.prices.infrastructure.rest.mapper.PriceResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class PriceController implements PricesApi {

    private final PriceService priceService;
    private final PriceResponseMapper priceResponseMapper;

    @Override
    public ResponseEntity<PriceResponse> getApplicablePrice(LocalDateTime applicationDate, Long productId, Long brandId) {
        Price price = priceService.getApplicablePrice(productId, brandId, applicationDate);
        return ResponseEntity.ok(priceResponseMapper.toResponse(price));
    }
}
