package uk.ac.warwick.cs126.test;

import uk.ac.warwick.cs126.structures.Entry;
import uk.ac.warwick.cs126.structures.MyHashMap;
import uk.ac.warwick.cs126.structures.MyLinkedList;

import java.util.Arrays;
import java.util.Iterator;

public class TestStructures {

    public static void main(String[] args) {
        MyHashMap<String,Integer>a=new MyHashMap<>();
        a.put("a",1);
        a.put("b",2);
        a.put("c",1);
        a.put("a",3);
        a.put("e",1);

        System.out.println(a.size());
        System.out.println(a.get("a"));
        Iterator<Entry<String,Integer>> iterator = a.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next().value);
        }
        a.removeAllOnValue(1);
        System.out.println(a.contains("c"));

        iterator = a.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next().value);
        }
    }

}
