package persistence.project.id;

public class DefaultIdGenerator implements IdGenerator{

  @Override
  public String generateId() {
    //берем id из файла в ресурсах, увеличиваем на один и возвращаем + записываем в файл
    return "1";
  }
}
