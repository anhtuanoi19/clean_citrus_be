package vn.tuanla.clean_citrus.service.abs.errormanagement;

import vn.tuanla.clean_citrus.domain.dtos.ErrorMessageDTO;

public interface ErrorsManagementService {
    void findAllErrorsByActive() throws Exception;
    ErrorMessageDTO findByErrorCode(String errorCode);
}

