package vn.tuanla.clean_citrus.domain.mapper;

import org.mapstruct.Mapper;
import vn.tuanla.clean_citrus.domain.dtos.SystemErrorsManagementDTO;
import vn.tuanla.clean_citrus.domain.entity.SystemErrorsManagement;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SystemErrorsManagementMapper {
    SystemErrorsManagementDTO toDto(SystemErrorsManagement model);
    List<SystemErrorsManagementDTO> toDtoList(List<SystemErrorsManagement> lsModels);
    SystemErrorsManagement toEntity(SystemErrorsManagementDTO dto);
    List<SystemErrorsManagement> toEntityList(List<SystemErrorsManagementDTO> lsSystemErrorsManagementDto);
}
