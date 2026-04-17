package com.jorgeturbica.prices.application.service;

import com.jorgeturbica.prices.domain.exception.PriceNotFoundException;
import com.jorgeturbica.prices.domain.model.Price;
import com.jorgeturbica.prices.domain.port.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceImplTest {

    private static final Long PRODUCT_ID = 35455L;
    private static final Long BRAND_ID = 1L;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 10, 0);

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceServiceImpl priceServiceImpl;

    private Price samplePrice;

    @BeforeEach
    void setUp() {
        samplePrice = new Price(
                PRODUCT_ID,
                BRAND_ID,
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                new BigDecimal("35.50"),
                "EUR"
        );
    }

    @Test
    @DisplayName("Returns Price when applicable price exists")
    void getApplicablePrice_whenPriceExists_returnsDto() {
        when(priceRepository.findApplicablePrice(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .thenReturn(Optional.of(samplePrice));

        Price result = priceServiceImpl.getApplicablePrice(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);

        assertThat(result.productId()).isEqualTo(PRODUCT_ID);
        assertThat(result.brandId()).isEqualTo(BRAND_ID);
        assertThat(result.priceList()).isEqualTo(1L);
        assertThat(result.price()).isEqualByComparingTo("35.50");
        assertThat(result.currency()).isEqualTo("EUR");
        assertThat(result.startDate()).isEqualTo(samplePrice.startDate());
        assertThat(result.endDate()).isEqualTo(samplePrice.endDate());
    }

    @Test
    @DisplayName("Throws PriceNotFoundException when no price matches")
    void getApplicablePrice_whenNotFound_throwsPriceNotFoundException() {
        when(priceRepository.findApplicablePrice(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> priceServiceImpl.getApplicablePrice(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining(String.valueOf(PRODUCT_ID))
                .hasMessageContaining(String.valueOf(BRAND_ID));
    }
}
