package ma.tiwtiw.core.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import ma.tiwtiw.core.exception.ResourceNotFoundException;
import ma.tiwtiw.core.model.BaseModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.util.StringUtils;

public interface BaseService<T extends BaseModel<ID>, ID, R extends MongoRepository<T, ID>> {

  R getRepository();

  default T save(T object) {
    return getRepository().save(object);
  }

  default T update(ID id, T data)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return update(id, data, false);
  }

  default T update(ID id, T data, boolean patch)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    final T object = findById(id);

    final Class<?> clazz = object.getClass();

    for (Field field : clazz.getDeclaredFields()) {
      final Id isId = field.getAnnotation(Id.class);

      if (isId != null) {
        continue;
      }

      final String fieldName = field.getName();

      final Method getter = clazz
          .getDeclaredMethod("get" + StringUtils.capitalize(fieldName));

      final Object value = getter.invoke(data);

      final Method setter = clazz
          .getDeclaredMethod("set" + StringUtils.capitalize(fieldName), field.getType());

      if (!patch || value != null) {
        setter.invoke(object, value);
      }
    }

    return getRepository().save(object);
  }

  default T patch(ID id, T data)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return update(id, data, true);
  }

  default T findById(ID id) {
    return getRepository().findById(id)
        .orElseThrow(ResourceNotFoundException::new);
  }

  default boolean existsById(ID id) {
    return getRepository().existsById(id);
  }

  default void deleteById(ID id) {
    final T object = findById(id);
    getRepository().delete(object);
  }

  default void delete(T object) {
    getRepository().delete(object);
  }

  default List<T> findAll() {
    return getRepository().findAll();
  }

  default Page<T> findAll(Pageable pageable) {
    return getRepository().findAll(pageable);
  }

}
