package uk.ac.warwick.cs126.util;
import java.util.Comparator;

public class Sorter {
    public static <E> E[] sort(E[] array, Comparator<E> comparator){
        sort(array,comparator,0,array.length-1);
        return array;
    }

    private static <E> void sort(E[] array, Comparator<E> comparator, int low, int high){
        if (low>=high)return;
        int pid = partition(array,comparator,low,high);
        sort(array,comparator,low,pid-1);
        sort(array,comparator,pid+1,high);
    }

    private static <E> int partition(E[] array, Comparator<E> comparator, int low, int high)
    {
        E p = array[high];
        if (p==null) System.out.println("?????????????");
        int smaller = low;
        for (int i=low; i<high; i++)
        {
            if (comparator.compare(array[i],p) <= 0)
            {
                E temp = array[smaller];
                array[smaller] = array[i];
                array[i] = temp;
                smaller+=1;
            }
        }
        E temp = array[smaller];
        array[smaller] = array[high];
        array[high] = temp;

        return smaller;
    }

    

}
