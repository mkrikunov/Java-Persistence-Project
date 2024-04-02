package persistence.project;

import static persistence.project.Utils.findById;
import static persistence.project.Utils.getAllFields;
import static persistence.project.Utils.isCollectionOfSerializedClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import persistence.project.annotations.ID;
import persistence.project.annotations.SerializedClass;
import persistence.project.id.IdGenerator;

public class Serializer {

  private final StorageManager storageManager;
  private final IdGenerator idGenerator;

  Serializer(StorageManager storageManager, IdGenerator idGenerator) {
    this.storageManager = storageManager;
    this.idGenerator = idGenerator;
  }

  private Integer findOrSerialize(Object object) throws Exception {
    // Получаем Id объекта
    var objId = Utils.getObjectId(object);
    if (objId != 0) { // Если объект уже сериализован -> возвращаем Id
      return objId;
    }
    return createObjectMap(object); // иначе сериализуем
  }


  /**
   * Создает Map'у полей и их значений для объекта, отправляет Map'у на запись в файл, а также
   * проверяет, был ли объект уже сериализован, если да - при наличии изменений перезаписывает в
   * файл, иначе скип.
   *
   * @param object объект, который нужно сериализовать.
   * @return идентификатор сериализованного объекта.
   * @throws Exception неожиданная ошибка в ходе работы.
   */
  public Integer createObjectMap(Object object) throws Exception {
    var className = object.getClass().getName();
    if (object.getClass().isAnnotationPresent(SerializedClass.class)) {
      Map<String, Object> classMap = new LinkedHashMap<>(3);
      Map<String, Field> allFields = getAllFields(object.getClass());
      List<Map<String, Object>> fields = new ArrayList<>();
      List<Map<String, Object>> compositeFields = new ArrayList<>();
      int id = 0;
      boolean wasZeroID = false;

      for (String fieldName : allFields.keySet()) {
        Field field = allFields.get(fieldName);
        field.setAccessible(true);
        if (field.isAnnotationPresent(ID.class)) {  // Если поле - ID
          id = Objects.requireNonNull(field).getInt(object);
          if (id == 0) {
            id = idGenerator.generateId(className);
            field.set(object, id);
            wasZeroID = true;
          }
          classMap.put("id", id);
          continue;
        }
        var fieldValue = field.get(object);
        if (fieldValue == null) { // Если поле без значения
          continue;
        }
        Map<String, Object> someField = new LinkedHashMap<>(1);
        if (field.getType().isAnnotationPresent(SerializedClass.class)) {
          Map<String, Object> compositeObjectMap = new LinkedHashMap<>(2);
          compositeObjectMap.put("name", fieldValue.getClass().getName());
          compositeObjectMap.put("id", findOrSerialize(fieldValue));
          someField.put(fieldName, compositeObjectMap);
          compositeFields.add(someField);

        } else if (isCollectionOfSerializedClass(field)) {
          @SuppressWarnings("unchecked")
          Collection<Object> collection = (Collection<Object>) fieldValue;
          List<Map<String, Object>> listObjects = new ArrayList<>(collection.size());
          for (Object element : collection) {
            Map<String, Object> compositeObjectMap = new LinkedHashMap<>(2);
            compositeObjectMap.put("name", element.getClass().getName());
            compositeObjectMap.put("id", findOrSerialize(element));
            listObjects.add(compositeObjectMap);
          }
          someField.put(fieldName, listObjects);
          compositeFields.add(someField);
        } else {
          someField.put(fieldName, fieldValue);
          fields.add(someField);
        }
      }

      if (!fields.isEmpty()) {  // Добавляем наборы полей в Map'у объекта
        classMap.put("fields", fields);
      }
      if (!compositeFields.isEmpty()) {
        classMap.put("compositeFields", compositeFields);
      }

      if (!wasZeroID) {
        Map<String, Object> objectMap = findById(id, className, storageManager);
        if (objectMap != null && Utils.mapsAreNotEqual(classMap, objectMap)) {
          storageManager.remove(className, id);
        }
      }
      storageManager.updateStorage(classMap, className);
      return id;
    }
    System.out.println("Class " + className
        + " isn't marked with an annotation SerializedClass");
    return 0;
  }
}