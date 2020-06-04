package ma.tiwtiw.core.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import ma.tiwtiw.core.model.BaseModel;
import ma.tiwtiw.core.model.SearchQuery;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

public interface BaseService<T extends BaseModel<ID>, ID> {

  T save(T object);

  T update(ID id, T data)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

  T patch(ID id, T data)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

  T findById(ID id);

  boolean existsById(ID id);

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

    return save(object);
  }

  void deleteById(ID id);

  void delete(T object);

  List<T> findAll();

  Page<T> findAll(Pageable pageable);

  Long count();

  Page<T> search(SearchQuery searchQuery, Pageable pageable);

  Long count(T object);

  Long count(SearchQuery searchQuery);
}
