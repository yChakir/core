package ma.tiwtiw.core.controller.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.tiwtiw.core.controller.BaseController;
import ma.tiwtiw.core.dto.BaseDto;
import ma.tiwtiw.core.exception.ServerException;
import ma.tiwtiw.core.model.BaseModel;
import ma.tiwtiw.core.model.SearchQuery;
import ma.tiwtiw.core.service.BaseService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseRestController<T extends BaseModel<ID>, D extends BaseDto<T, ID>, ID> implements
    BaseController<T, D, ID> {

  private final Class<T> tClass;

  private final Class<D> dClass;

  protected abstract <S extends BaseService<T, ID>> S getService();

  protected abstract ModelMapper getMapper();

  private T newModelInstance() throws IllegalAccessException, InstantiationException {
    return tClass.newInstance();
  }

  private D newDtoInstance() throws IllegalAccessException, InstantiationException {
    return dClass.newInstance();
  }

  @Override
  @PostMapping
  public ResponseEntity save(@Valid @RequestBody D dto, HttpServletRequest request) {
    try {
      final T object = newModelInstance();

      getMapper().map(dto, object);

      getService().save(object);

      final URI location = ServletUriComponentsBuilder
          .fromCurrentContextPath().path(request.getRequestURI() + "/{id}")
          .buildAndExpand(object.getId()).toUri();

      return ResponseEntity.created(location).build();

    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  @Override
  @PutMapping("{id}")
  public ResponseEntity update(@PathVariable ID id, @Valid @RequestBody D dto) {
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
  public ResponseEntity patch(@PathVariable ID id, @Valid @RequestBody D dto) {
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

      final List<D> dtos = new ArrayList<>();

      for (T object : objects) {
        final D dto = newDtoInstance();

        getMapper().map(object, dto);

        dtos.add(dto);
      }

      final Page<D> page = PageableExecutionUtils
          .getPage(dtos, pageable, () -> getService().count());

      return ResponseEntity.ok(page);
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  @Override
  @PostMapping("search")
  public ResponseEntity search(@Valid @RequestBody SearchQuery searchQuery, Pageable pageable) {
    try {
      final Page<T> objects = getService().search(searchQuery, pageable);

      final List<D> dtos = new ArrayList<>();

      for (T object : objects) {
        final D dto = newDtoInstance();

        getMapper().map(object, dto);

        dtos.add(dto);
      }

      final Page<D> page = PageableExecutionUtils
          .getPage(dtos, pageable, () -> getService().count(searchQuery));

      return ResponseEntity.ok(page);
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }
}
