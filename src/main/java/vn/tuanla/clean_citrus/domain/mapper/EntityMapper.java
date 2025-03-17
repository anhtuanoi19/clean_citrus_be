package vn.tuanla.clean_citrus.domain.mapper;

import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Contract for a generic dto to entity mapper.
 *
 * @param <D> - DTO type parameter.
 * @param <E> - Entity type parameter.
 */

public interface EntityMapper<D, E> {
    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

    E toEntity(D dto);

    D toDto(E entity);

    List <E> toEntity(List<D> dtoList);

    List <D> toDto(List<E> entityList);
}
