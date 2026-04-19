package com.jorgeturbica.prices.infrastructure.rest.exception;

import com.jorgeturbica.prices.domain.exception.PriceNotFoundException;
import com.jorgeturbica.prices.infrastructure.rest.generated.model.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("PriceNotFoundException returns 404 with product and brand in message")
    void handlePriceNotFound_returns404() {
        PriceNotFoundException ex = new PriceNotFoundException(35455L, 1L, LocalDateTime.of(2020, 6, 14, 10, 0));

        ResponseEntity<ErrorResponse> response = handler.handlePriceNotFound(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).contains("35455").contains("1");
    }

    @Test
    @DisplayName("MissingServletRequestParameterException returns 400 with parameter name")
    void handleMissingParam_returns400WithParamName() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("applicationDate", "LocalDateTime");

        ResponseEntity<ErrorResponse> response = handler.handleMissingParam(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).contains("applicationDate");
    }

    @Test
    @DisplayName("MethodArgumentTypeMismatchException returns 400 with param name and value")
    void handleTypeMismatch_returns400WithParamNameAndValue() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("brandId");
        when(ex.getValue()).thenReturn("abc");

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).contains("brandId").contains("abc");
    }
}
