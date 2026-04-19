package com.jorgeturbica.prices.infrastructure.persistence.mapper;

import com.jorgeturbica.prices.PriceEntityTestBuilder;
import com.jorgeturbica.prices.domain.model.Price;
import com.jorgeturbica.prices.infrastructure.persistence.entity.PriceEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PriceEntityMapperTest {

    private final PriceEntityMapper mapper = new PriceEntityMapperImpl();

    @Test
    @DisplayName("Maps all PriceEntity fields to Price domain model")
    void toDomain_mapsAllFields() {
        PriceEntity entity = PriceEntityTestBuilder.defaultPrice();

        Price price = mapper.toDomain(entity);

        assertThat(price.productId()).isEqualTo(entity.getProductId());
        assertThat(price.brandId()).isEqualTo(entity.getBrandId());
        assertThat(price.priceList()).isEqualTo(entity.getPriceList());
        assertThat(price.startDate()).isEqualTo(entity.getStartDate());
        assertThat(price.endDate()).isEqualTo(entity.getEndDate());
        assertThat(price.price()).isEqualByComparingTo(entity.getPrice());
        assertThat(price.currency()).isEqualTo(entity.getCurrency());
    }

    @Test
    @DisplayName("Returns null when entity is null")
    void toDomain_whenEntityIsNull_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }
}
