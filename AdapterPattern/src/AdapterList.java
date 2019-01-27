import java.util.ArrayList;

public class AdapterList<T> extends ArrayList<T> implements IList<T>{

  @Override
  public int count() {
    return size();
  }

  @Override
  public Object first() {
    return get(0);
  }

  @Override
  public Object last() {
    return get(size() - 1);
  }

  @Override
  public boolean include(Object obj) {
    return contains(obj);
  }

  @Override
  public void append(Object obj) {
    add((T) obj);
  }

  @Override
  public void prepend(Object obj) {
    add(0, (T) obj);
  }

  @Override
  public void delete(Object obj) {
    remove(obj);
  }

  @Override
  public void deleteLast() {
    remove(size() - 1);
  }

  @Override
  public void deleteFirst() {
    remove(0);
  }

  @Override
  public void deleteAll() {
    removeRange(0, size());
  }
}
