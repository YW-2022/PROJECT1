package uk.ac.warwick.cs126.structures;

import java.util.Iterator;

public class MyHashMap<K,V>implements Iterable<Entry<K,V>> {
    int cap = 1<<6;
    Object[] hashArray;
    int size = 0;

    public MyHashMap(){
         hashArray= new Object[cap];
    }

    public MyHashMap(int cap){
        this.cap=cap;
         hashArray= new Object[cap];
    }

    public int size() {
        return size;
    }

    private int localHashCode(K key){
        int hash = key.hashCode()%cap;
        if (hash<0){
            hash+=cap;
        }
        return hash;
    }

    public void put(K key, V value ){
        int hash = localHashCode(key);
        MyLinkedList<Entry<K,V>> list = ensureGet(hash);

        for (Entry<K,V> e:list) {
            if (key.equals(e.key)){
                e.value=value;
                return;
            }
        }

        Entry<K, V> kv = new Entry<>();
        kv.key=key;
        kv.value=value;
        list.add(kv);
        size+=1;
    }

    public V get(K key){
        Entry<K,V> result = getFirst(key);
        if (result==null){
            return null;
        }else {
            return result.value;
        }
    }

    private Entry<K,V> getFirst(K key){
        int hash = localHashCode(key);
        MyLinkedList<Entry<K,V>> list = ensureGet(hash);
        for (Entry<K, V> e : list) {
            if (e.key.equals(key)) {
                return e;
            }
        }
        return null;
    }

    public boolean contains(K key){
        return getFirst(key)!=null;
    }

    public void remove(K key){
        int hash = localHashCode(key);
        MyLinkedList<Entry<K,V>> list = getList(hash);
        if(list==null)return;
        for (Entry<K,V> e:list){
            if(e.key.equals(key)){
                list.removeFirst(e);
                size-=1;
                break;
            }
        }
    }

    public void removeAllOnValue(V v){
        Iterator<Entry<K,V>> iterator = iterator();
        while (iterator.hasNext()){
            V value = iterator.next().value;
            if (v==null){
                if (value==null){
                    iterator.remove();
                }
            }else {
                if (v.equals(value)){
                    iterator.remove();
                }
            }
        }
    }

    public MyLinkedList<V> valueList(){
        MyLinkedList<V> result = new MyLinkedList<>();
        Iterator<Entry<K,V>> iterator = iterator();
        while (iterator.hasNext()){
            result.add(iterator.next().value);
        }
        return result;
    }

    private MyLinkedList<Entry<K,V>> ensureGet(int indexOfArray){
        if(hashArray[indexOfArray]==null){
            hashArray[indexOfArray]= new MyLinkedList<Entry<K,V>>();
        }
        return getList(indexOfArray);
    }

    @SuppressWarnings("unchecked")
    private MyLinkedList<Entry<K,V>> getList(int indexOfArray){
        return (MyLinkedList<Entry<K,V>>)hashArray[indexOfArray];
    }

    @Override
    public Iterator<Entry<K,V>> iterator() {
        return new MapIterator<>(this);
    }
    @SuppressWarnings("unchecked")
    public MyHashMap<K,V[]> common(MyHashMap<K,V> that){
        if (this.cap!=that.cap)throw new UnsupportedOperationException();
        MyHashMap<K,V[]>result=new MyHashMap<>();
        for (int i = 0; i < cap; i++) {
            if (this.getList(i)==null||that.getList(i)==null)continue;
            Iterator<Entry<K,V>> thisIterator = this.getList(i).iterator();
            Iterator<Entry<K,V>> thatIterator = that.getList(i).iterator();
            while (thisIterator.hasNext()){
                Entry<K,V>thisCurrent = thisIterator.next();
                while (thatIterator.hasNext()){
                    Entry<K,V>thatCurrent = thatIterator.next();
                    if (thisCurrent.value.equals(thatCurrent.value)){

                                result.put(thisCurrent.key, (V[]) new Object[]{thisCurrent.value,thatCurrent.value});
                        break;
                    }
                }
            }
        }
        return result;
    }

    public Entry<K,V>[] toArray(Entry<K,V>[] a){
        Entry<K,V>[] result =a;
        Iterator<Entry<K,V>> iterator = iterator();
        for (int i=0;i<size;i++){
            result[i]=iterator.next();
        }
        return result;
    }

    public MyHashMap<K,V> left(MyHashMap<K,V> that){
        if (this.cap!=that.cap)throw new UnsupportedOperationException();
        MyHashMap<K,V>result=new MyHashMap<>();
        for (int i = 0; i < cap; i++) {
            if (this.getList(i)==null)continue;
            if (that.getList(i)==null){
                result.ensureGet(i);
                this.hashArray[i]=this.hashArray[i];
                continue;
            }
            Iterator<Entry<K,V>> thisIterator = this.getList(i).iterator();
            Iterator<Entry<K,V>> thatIterator = that.getList(i).iterator();
            while (thisIterator.hasNext()){
                Entry<K,V>thisCurrent = thisIterator.next();
                boolean toPut = true;
                while (thatIterator.hasNext()){
                    Entry<K,V>thatCurrent = thatIterator.next();
                    if (thisCurrent.value.equals(thatCurrent.value)){
                        toPut=false;
                        break;
                    }
                }
                if (toPut){
                    result.put(thisCurrent.key, thisCurrent.value);
                }
            }
        }
        return result;
    }
    
}

class MapIterator<K,V> implements Iterator<Entry<K,V>> {
    MyHashMap<K,V>map;
    int count = 0;
    int currentSlot = -1;
    Iterator<Entry<K,V>> iterator;
    public MapIterator(MyHashMap<K,V> map){
        this.map=map;
    }

    @Override
    public boolean hasNext() {
        return count<map.size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Entry<K, V> next() {
        MyLinkedList<Entry<K,V>> slot=null;
        while (iterator==null||!iterator.hasNext()){
            currentSlot+=1;
            if (currentSlot>= map.cap){
                System.out.println(count);
                System.out.println(map.size);
            }
            slot = (MyLinkedList<Entry<K,V>>)map.hashArray[currentSlot];
            if (slot==null || slot.size==0){
                continue;
            }else {
                iterator=slot.iterator();
            }
        }
        count++;
        return iterator.next();
    }

    @Override
    public void remove() {
        iterator.remove();
        map.size--;
        count--;
    }
}


