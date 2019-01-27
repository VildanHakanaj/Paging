public interface IList<T> {

  int count();                  //return the current number of elements in the list
  Object get(int index);        //return the object at the index in the list
  Object first();               //return the first object in the list
  Object last();                //return the last object in the list
  boolean include(Object obj);  //return true is the object in the list
  void append(Object obj);      //append the object to the end of the list
  void prepend(Object obj);     //insert the object to the front of the list
  void delete(Object obj);      //remove the object from the list
  void deleteLast();            //remove the last element of the list
  void deleteFirst();           //remove the first element of the list
  void deleteAll();             //remove all elements of the list


}
