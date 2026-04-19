package com.jorgeturbica.prices.infrastructure.rest.mapper;

import com.jorgeturbica.prices.domain.model.Price;
import com.jorgeturbica.prices.infrastructure.rest.generated.model.PriceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PriceResponseMapperTest {

    private final PriceResponseMapper mapper = new PriceResponseMapperImpl();

    @Test
    @DisplayName("Maps all Price domain model fields to PriceResponse")
    void toResponse_mapsAllFields() {
        Price price = new Price(
                35455L,
                1L,
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                new BigDecimal("35.50"),
                "EUR"
        );

        PriceResponse response = mapper.toResponse(price);

        assertThat(response.getProductId()).isEqualTo(price.productId());
        assertThat(response.getBrandId()).isEqualTo(price.brandId());
        assertThat(response.getPriceList()).isEqualTo(price.priceList());
        assertThat(response.getStartDate()).isEqualTo(price.startDate());
        assertThat(response.getEndDate()).isEqualTo(price.endDate());
        assertThat(response.getPrice()).isEqualTo(price.price().doubleValue());
        assertThat(response.getCurrency()).isEqualTo(price.currency());
    }

    @Test
    @DisplayName("Returns null when price is null")
    void toResponse_whenPriceIsNull_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
