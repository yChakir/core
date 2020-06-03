package ma.tiwtiw.core.controller.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ma.tiwtiw.core.controller.BaseController;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
public abstract class BaseRestController<T extends BaseModel<ID>, D extends BaseDto<T, ID>, ID, S extends BaseService<T, ID, ?>> implements
    BaseController<T, D, ID, S> {

  private final Class<T> tClass;
  private final Class<D> dClass;

  protected abstract S getService();

  protected abstract ModelMapper getMapper();

  private T newModelInstance() throws IllegalAccessException, InstantiationException {
    return tClass.newInstance();
  }

  private D newDtoInstance() throws IllegalAccessException, InstantiationException {
    return dClass.newInstance();
  }

  @Override
  @PostMapping
  public ResponseEntity save(@RequestBody D dto, HttpServletRequest request) {
    try {
      final T object = newModelInstance();

      getMapper().map(dto, object);

      getService().save(object);

      URI location = ServletUriComponentsBuilder
          .fromCurrentContextPath().path(request.getRequestURI() + "/{id}")
          .buildAndExpand(object.getId()).toUri();

      return ResponseEntity.created(location).build();

    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  @Override
  @PutMapping("{id}")
  public ResponseEntity update(@PathVariable ID id, @RequestBody D dto) {
    try {
      final T object = newModelInstance();

      getMapper().map(dto, object);

      getService().update(id, object);

      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  @Override
  @PatchMapping("{id}")
  public ResponseEntity patch(@PathVariable ID id, @RequestBody D dto) {
    try {
      final T object = newModelInstance();

      getMapper().map(dto, object);

      getService().patch(id, object);

      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  @Override
  @GetMapping("{id}")
  public ResponseEntity findById(@PathVariable ID id) {
    try {
      final T object = getService().findById(id);

      final D dto = newDtoInstance();

      getMapper().map(object, dto);

      return ResponseEntity.ok(dto);
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  @Override
  @GetMapping("{id}/exists")
  public ResponseEntity existsById(@PathVariable ID id) {
    final boolean exists = getService().existsById(id);

    return ResponseEntity.ok(exists);
  }

  @Override
  @DeleteMapping("{id}")
  public ResponseEntity deleteById(@PathVariable ID id) {
    getService().deleteById(id);

    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping
  public ResponseEntity findAll() {
    try {
      final List<T> objects = getService().findAll();

      final List<D> dtos = new ArrayList<>();

      for (T object : objects) {
        final D dto = newDtoInstance();

        getMapper().map(object, dto);

        dtos.add(dto);
      }

      return ResponseEntity.ok(dtos);
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  @Override
  @GetMapping("getPage")
  public ResponseEntity findAll(Pageable pageable) {
    try {
      final Page<T> objects = getService().findAll(pageable);

      final Page<D> dtos = new PageImpl<>(new ArrayList<>());

      for (T object : objects) {
        final D dto = newDtoInstance();

        getMapper().map(object, dto);

        dtos.and(dto);
      }

      return ResponseEntity.ok(dtos);
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }
}
