package ma.tiwtiw.core.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ma.tiwtiw.core.exception.ServerException;
import ma.tiwtiw.core.model.BaseModel;
import ma.tiwtiw.core.service.BaseService;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

@RequiredArgsConstructor
public abstract class BaseMongoTemplateServiceImpl<T extends BaseModel<ID>, ID> implements
    BaseService<T, ID> {

  private final Class<T> tClass;

  protected abstract MongoTemplate getMongoTemplate();

  @Override
  public T save(T object) {
    return getMongoTemplate().save(object);
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
    return getMongoTemplate().findById(id, tClass);
  }

  @Override
  public boolean existsById(ID id) {
    final Query query = new Query();
    query.addCriteria(Criteria.where(getIdFieldName()).is(id));

    return getMongoTemplate().exists(query, tClass);
  }

  @Override
  public void deleteById(ID id) {
    final T object = findById(id);
    delete(object);
  }

  @Override
  public void delete(T object) {
    getMongoTemplate().remove(object);
  }

  @Override
  public List<T> findAll() {
    return getMongoTemplate().findAll(tClass);
  }

  @Override
  public Page<T> findAll(Pageable pageable) {
    Query query = new Query();
    query.with(pageable);

    final List<T> list = getMongoTemplate().find(query, tClass);

    return PageableExecutionUtils.getPage(list, pageable, this::count);
  }

  @Override
  public Long count() {
    return getMongoTemplate().count(new Query(), tClass);
  }

  @Override
  public Long count(T object) {
    return getMongoTemplate().count(new Query(Criteria.byExample(object)), tClass);
  }

  private String getIdFieldName() {
    for (Field field : tClass.getDeclaredFields()) {
      final Id annotation = field.getAnnotation(Id.class);

      if (annotation != null) {
        return field.getName();
      }
    }

    throw new ServerException(
        "No field with Id annotation found in class: " + tClass.getCanonicalName());
  }
}
