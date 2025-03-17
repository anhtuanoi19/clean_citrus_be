package vn.tuanla.clean_citrus.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.tuanla.clean_citrus.domain.dtos.ErrorMessageDTO;
import vn.tuanla.clean_citrus.domain.entity.SystemErrorsManagement;

@Mapper(componentModel = "spring")
public interface ErrorMessageMapper {

    ErrorMessageMapper INSTANCE = Mappers.getMapper(ErrorMessageMapper.class);

    ErrorMessageDTO toDto(SystemErrorsManagement entity);
}
