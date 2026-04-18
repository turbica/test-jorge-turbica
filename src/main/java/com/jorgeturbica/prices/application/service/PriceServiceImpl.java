package com.jorgeturbica.prices.application.service;

import com.jorgeturbica.prices.application.port.PriceService;
import com.jorgeturbica.prices.domain.exception.PriceNotFoundException;
import com.jorgeturbica.prices.domain.model.Price;
import com.jorgeturbica.prices.domain.port.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;

    @Override
    @Cacheable(value = "prices", key = "#productId + ':' + #brandId + ':' + #applicationDate")
    public Price getApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate) {
        log.debug("Querying applicable price for productId={}, brandId={}, date={}", productId, brandId, applicationDate);

        return priceRepository
                .findApplicablePrice(productId, brandId, applicationDate)
                .orElseThrow(() -> {
                    log.warn("No applicable price found for productId={}, brandId={}, date={}", productId, brandId, applicationDate);
                    return new PriceNotFoundException(productId, brandId, applicationDate);
                });
    }
}