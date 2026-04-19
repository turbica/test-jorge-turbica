package com.jorgeturbica.prices;

import com.jorgeturbica.prices.infrastructure.persistence.entity.PriceEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceEntityTestBuilder {

    public static PriceEntity defaultPrice() {
        return PriceEntity.builder()
                .id(1L)
                .brandId(1L)
                .productId(35455L)
                .priceList(1L)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59, 59))
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();
    }

    public static PriceEntity priceWith(Long priceList, int priority, BigDecimal price) {
        return PriceEntity.builder()
                .id(priceList)
                .brandId(1L)
                .productId(35455L)
                .priceList(priceList)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59, 59))
                .priority(priority)
                .price(price)
                .currency("EUR")
                .build();
    }
}
