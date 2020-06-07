package ma.tiwtiw.core.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import ma.tiwtiw.core.exception.ResourceNotFoundException;
import ma.tiwtiw.core.model.BaseModel;
import ma.tiwtiw.core.model.SearchQuery;
import ma.tiwtiw.core.service.BaseService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public abstract class BaseMongoRepositoryServiceImpl<T extends BaseModel<ID>, ID> implements
    BaseService<T, ID> {


  protected abstract <R extends MongoRepository<T, ID>> R getRepository();

  @Override
  public T save(T object) {
    if (object.getId() != null) {
      throw new UnsupportedOperationException();
    }
    return getRepository().save(object);
  }

  @Override
  public T update(ID id, T data)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return update(id, data, false);
  }

  @Override
  public T patch(ID id, T data)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return update(id, data, true);
  }

  @Override
  public T findById(ID id) {
    return getRepository().findById(id)
        .orElseThrow(ResourceNotFoundException::new);
  }

  @Override
  public boolean existsById(ID id) {
    return getRepository().existsById(id);
  }

  @Override
  public void deleteById(ID id) {
    final T object = findById(id);
    getRepository().delete(object);
  }

  @Override
  public void delete(T object) {
    getRepository().delete(object);
  }

  @Override
  public List<T> findAll() {
    return getRepository().findAll();
  }

  @Override
  public Page<T> findAll(Pageable pageable) {
    return getRepository().findAll(pageable);
  }

  @Override
  public Long count() {
    return getRepository().count();
  }

  @Override
  public Page<T> search(SearchQuery searchQuery, Pageable pageable) {
    // todo implement
    throw new UnsupportedOperationException();
  }

  @Override
  public Long count(T object) {
    Example<T> example = Example.of(object);
    return getRepository().count(example);
  }

  @Override
  public Long count(SearchQuery searchQuery) {
    // todo implement
    throw new UnsupportedOperationException();
  }
}
