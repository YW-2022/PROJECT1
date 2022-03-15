package uk.ac.warwick.cs126.test;

import uk.ac.warwick.cs126.structures.MyLinkedList;
import uk.ac.warwick.cs126.util.Sorter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TestSorter {

    public static void main(String[] args) {
        int[] a= new int[]{1,3,5,8,4,2,4};
        Integer[] d = Arrays.stream( a ).boxed().toArray( Integer[]::new );
        Sorter.sort(d,new Comparator<Integer>(){

            @Override
            public int compare(Integer o1, Integer o2) {
                return o1-o2;
            }
        });
        System.out.println(Arrays.toString(d));
    }

}
