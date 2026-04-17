package com.jorgeturbica.prices.infrastructure.rest.mapper;

import com.jorgeturbica.prices.domain.model.Price;
import com.jorgeturbica.prices.infrastructure.rest.generated.model.PriceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceResponseMapper {

    PriceResponse toResponse(Price price);
}
