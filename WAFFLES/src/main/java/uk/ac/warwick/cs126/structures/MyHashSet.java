package uk.ac.warwick.cs126.structures;

public class MyHashSet<E> {
    MyHashMap<E,Object> map = new MyHashMap<>();

    public void add(E e){
        map.put(e,null);
    }
    public boolean contains(E e){
        return map.contains(e);
    }

    public void remove(E e){
        map.remove(e);
    }

}
