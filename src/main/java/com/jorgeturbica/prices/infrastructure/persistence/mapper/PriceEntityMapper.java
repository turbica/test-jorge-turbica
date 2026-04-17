package com.jorgeturbica.prices.infrastructure.persistence.mapper;

import com.jorgeturbica.prices.domain.model.Price;
import com.jorgeturbica.prices.infrastructure.persistence.entity.PriceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceEntityMapper {

    Price toDomain(PriceEntity entity);
}
