package ma.tiwtiw.core.controller;

import java.util.ArrayList;
import java.util.List;
import ma.tiwtiw.core.dto.BaseDto;
import ma.tiwtiw.core.exception.ServerException;
import ma.tiwtiw.core.model.BaseModel;
import ma.tiwtiw.core.service.BaseService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class BaseController<T extends BaseModel<ID>, D extends BaseDto<T, ID>, ID, S extends BaseService<T, ID, ?>> {

  protected abstract S getService();

  protected abstract ModelMapper getMapper();

  @PostMapping
  public ResponseEntity save(@RequestBody D dto, Class<T> clazz) {
    try {
      final T object = clazz.newInstance();

      getMapper().map(dto, object);

      getService().save(object);

      return ResponseEntity.created(null).build();

    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  @PutMapping("{id}")
  public ResponseEntity update(@PathVariable ID id, @RequestBody D dto, Class<T> clazz) {
    try {
      final T object = clazz.newInstance();

      getMapper().map(dto, object);

      getService().update(id, object);

      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  @PatchMapping("{id}")
  public ResponseEntity patch(@PathVariable ID id, @RequestBody D dto, Class<T> clazz) {
    try {
      final T object = clazz.newInstance();

      getMapper().map(dto, object);

      getService().patch(id, object);

      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  @GetMapping("{id}")
  public ResponseEntity findById(@PathVariable ID id, Class<D> clazz) {
    try {
      final T object = getService().findById(id);

      final D dto = clazz.newInstance();

      getMapper().map(object, dto);

      return ResponseEntity.ok(dto);
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  @GetMapping("{id}/exists")
  public ResponseEntity existsById(@PathVariable ID id) {
    final boolean exists = getService().existsById(id);

    return ResponseEntity.ok(exists);
  }

  @DeleteMapping("{id}")
  public ResponseEntity deleteById(@PathVariable ID id) {
    getService().deleteById(id);

    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity findAll(Class<D> clazz) {
    try {
      final List<T> objects = getService().findAll();

      final List<D> dtos = new ArrayList<>();

      for (T object : objects) {
        final D dto = clazz.newInstance();

        getMapper().map(object, dto);

        dtos.add(dto);
      }

      return ResponseEntity.ok(dtos);
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  @GetMapping("getPage")
  public ResponseEntity findAll(Pageable pageable, Class<D> clazz) {
    try {
      final Page<T> objects = getService().findAll(pageable);

      final Page<D> dtos = new PageImpl<>(new ArrayList<>());

      for (T object : objects) {
        final D dto = clazz.newInstance();

        getMapper().map(object, dto);

        dtos.and(dto);
      }

      return ResponseEntity.ok(dtos);
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }
}
