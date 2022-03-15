package uk.ac.warwick.cs126.structures;

import java.util.Iterator;


public class MyLinkedList<E> implements Iterable<E>{
    Node<E> head=new Node<>();
    Node<E> last=new Node<>();
    int size = 0;
    public MyLinkedList(){
        head.next=last;
    }
    public int size() {
        return size;
    }

    public boolean contains(Object o) {
        return getNode(o)!=null;
    }

    public void add(E e){
        last.val = e;
        last.next = new Node<>();
        last = last.next;
        size++;
    }

    public MyLinkedList<E> copy(){
        MyLinkedList<E> copied = new MyLinkedList<>();
        for (E e:this) {
            copied.add(e);
        }
        return copied;
    }

    public void add(MyLinkedList<E> that){
        for (E e:that) {
            this.add(e);
        }
    }

    public void add(E[] es){
        for (E e:es) {
            this.add(e);
        }
    }

    public E get(int index){
        if (index>=size)return null;
        Iterator<E> iterator = iterator();
        while (index>0){
            iterator.next();
            index--;
        }
        return iterator.next();
    }

    private Node<E> getNode(Object o){
        Node<E> e;
        if(o==null){
            for(e = this.head.next; e != last; e = e.next) {
                if (e.val == null) {
                    return e;
                }
            }
        }else {
            for(e = this.head.next; e != last; e = e.next) {
                if (e.val.equals(o)) {
                    return e;
                }
            }
        }
        return null;
    }

    private void remove(E e, boolean first){
        Node<E> previous=head;
        Node<E> node = head.next;
        int originalSize = size;
        for (int i=0;i<originalSize;i++){
            if (e==null){
                if (node.val==null){
                    previous.next=node.next;
                    size--;
                    if(first)return;
                }else {
                    previous=node;
                }
            }else {
                if (e.equals(node.val)){
                    previous.next=node.next;
                    size--;
                    if(first)return;
                }else {
                    previous=node;
                }
            }
            node=node.next;
        }
    }
    public void removeFirst(E e){
        remove(e,true);
    }
    public void removeAll(E e){
        remove(e,false);
    }


    @Override
    public Iterator<E> iterator() {
        return new MyIterator<>(this);
    }

    public E[] toArray(E[] a){
        int i = 0;
        for (E e:this) {
            a[i]=e;
            i++;
        }
        return a;
    }

}

//class EIterator<E> implements Iterator<Node<E>>{
//    Node<E> e;
//    Node<E> previous = null;
//    int count=0;
//    MyLinkedList<E> list;
//
//    public EIterator(MyLinkedList<E> list){
//        this.list=list;
//        e=new Node<>();
//        e.next=list.head;
//    }
//
//    @Override
//    public boolean hasNext() {
//        return count<list.size();
//    }
//
//    @Override
//    public Node<E> next() {
//        previous=e;
//        e = e.next;
//        return e;
//    }
//
//    @Override
//    public void remove() {
//
//    }
//}

class MyIterator<E> implements Iterator<E>{
    Node<E> e;
    Node<E> previous = null;
    int count=0;
    MyLinkedList<E> list;

    public MyIterator(MyLinkedList<E> list){
        this.list=list;
        e=list.head;
    }

    @Override
    public boolean hasNext() {
        return count<list.size();
    }

    @Override
    public E next() {
        previous=e;
        e = e.next;
        count++;
        return e.val;
    }

    @Override
    public void remove() {
        previous.next=e.next;
        list.size--;
        count--;
    }
}
class Node<E> {
    E val;
    Node<E> next;
}