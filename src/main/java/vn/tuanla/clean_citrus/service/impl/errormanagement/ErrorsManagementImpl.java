package vn.tuanla.clean_citrus.service.impl.errormanagement;

import jakarta.transaction.Transactional;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.tuanla.clean_citrus.common.ConstantsErrorCode;
import vn.tuanla.clean_citrus.common.Constants;
import vn.tuanla.clean_citrus.common.utils.ErrorManagementUtils;
import vn.tuanla.clean_citrus.domain.dtos.ErrorMessageDTO;
import vn.tuanla.clean_citrus.domain.entity.SystemErrorsManagement;
import vn.tuanla.clean_citrus.domain.mapper.ErrorMessageMapper;
import vn.tuanla.clean_citrus.exception.BusinessException;
import vn.tuanla.clean_citrus.repository.jpa.ErrorsManagementRepo;
import vn.tuanla.clean_citrus.service.abs.errormanagement.ErrorsManagementService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ErrorsManagementImpl implements ErrorsManagementService {

    private static final Logger logger = LoggerFactory.getLogger(ErrorsManagementImpl.class);
    private final ErrorsManagementRepo errorsManagementRepo;
    private final ErrorManagementUtils errorManagementUtils;
    private final ErrorMessageMapper errorMapper;

    @Value("${app.errors.loader.ttl}")
    private Long timeToLiveErrorLoader;

    @Autowired
    public ErrorsManagementImpl(ErrorsManagementRepo errorsManagementRepo, @Lazy ErrorManagementUtils errorManagementUtils, ErrorMessageMapper errorMapper) {
        this.errorsManagementRepo = errorsManagementRepo;
        this.errorManagementUtils = errorManagementUtils;
        this.errorMapper = errorMapper;
    }

    @Override
    public void findAllErrorsByActive() throws Exception {
        ErrorMessageMapper errorMessageMapper = Mappers.getMapper(ErrorMessageMapper.class);
        try {
            logger.info("Remove ErrorMessage from RedisCache");
            logger.info("Fetch all ErrorMessage From Database");
            List<SystemErrorsManagement> lstErrorEntity = errorsManagementRepo.findAllByDeletedAndStatus(Constants.DELETE.INACTIVE, Constants.STATUS.ACTIVE);
            if(!lstErrorEntity.isEmpty()){
                List<ErrorMessageDTO> listErrorMessage = lstErrorEntity.stream().map(errorMessageMapper::toDto).collect(Collectors.toList());
                logger.info("Push ErrorMessage to RedisCache Successfully");
            }else {
             throw new Exception(errorManagementUtils.getMessageByCode(ConstantsErrorCode.BASE_NOT_FOUND));
            }
        }catch (Exception e){
            throw new Exception(errorManagementUtils.getMessageByCode(ConstantsErrorCode.BASE_INTERNAL_ERROR));
        }
    }

    @Override
    public ErrorMessageDTO findByErrorCode(String errorCode) {
        SystemErrorsManagement systemErrorsManagement = errorsManagementRepo.findByErrorCodeAndLang(errorCode, Constants.DELETE.INACTIVE, Constants.STATUS.ACTIVE).orElseThrow(()-> new BusinessException(ConstantsErrorCode.SYSTEM_ERROR.SYSTEM_ERROR_NOT_FOUND));
        return errorMapper.toDto(systemErrorsManagement);
    }
}
