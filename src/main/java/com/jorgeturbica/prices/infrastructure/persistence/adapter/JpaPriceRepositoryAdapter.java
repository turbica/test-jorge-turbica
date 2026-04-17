package com.jorgeturbica.prices.infrastructure.persistence.adapter;

import com.jorgeturbica.prices.domain.model.Price;
import com.jorgeturbica.prices.domain.port.PriceRepository;
import com.jorgeturbica.prices.infrastructure.persistence.mapper.PriceEntityMapper;
import com.jorgeturbica.prices.infrastructure.persistence.repository.PriceJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaPriceRepositoryAdapter implements PriceRepository {

    private final PriceJpaRepository jpaRepository;
    private final PriceEntityMapper mapper;

    @Override
    public Optional<Price> findApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate) {
        log.debug("Executing price query — productId={}, brandId={}, applicationDate={}", productId, brandId, applicationDate);

        // PageRequest.of(0, 1) pushes the LIMIT to the DB layer; no in-memory filtering
        return jpaRepository
                .findApplicablePrices(productId, brandId, applicationDate, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(mapper::toDomain);
    }
}
