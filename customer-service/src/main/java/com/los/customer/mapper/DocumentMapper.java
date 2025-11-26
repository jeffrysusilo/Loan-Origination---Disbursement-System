package com.los.customer.mapper;

import com.los.customer.dto.DocumentResponse;
import com.los.customer.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "customer.id", target = "customerId")
    DocumentResponse toResponse(Document document);
}
