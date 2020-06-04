package ma.tiwtiw.core.controller;

import javax.servlet.http.HttpServletRequest;
import ma.tiwtiw.core.dto.BaseDto;
import ma.tiwtiw.core.model.BaseModel;
import ma.tiwtiw.core.service.BaseService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface BaseController<T extends BaseModel<ID>, D extends BaseDto<T, ID>, ID> {

  ResponseEntity save(D dto, HttpServletRequest request);

  ResponseEntity update(ID id, D dto);

  ResponseEntity patch(ID id, D dto);

  ResponseEntity findById(ID id);

  ResponseEntity existsById(ID id);

  ResponseEntity deleteById(ID id);

  ResponseEntity findAll();

  ResponseEntity findAll(Pageable pageable);
}
