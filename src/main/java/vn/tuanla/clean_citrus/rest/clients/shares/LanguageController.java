package vn.tuanla.clean_citrus.rest.clients.shares;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.tuanla.clean_citrus.common.Constants;
import vn.tuanla.clean_citrus.domain.dtos.LanguageDTO;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(Constants.CLIENT_API)
public class LanguageController {

    @GetMapping("/supportedLanguages")
    public List<LanguageDTO> getSupportedLanguages() {
        return Arrays.asList(
                new LanguageDTO("vi", "Tiếng Việt"),
                new LanguageDTO("en_US", "English")
        );
    }
}
