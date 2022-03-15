package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.IFavouriteStore;
import uk.ac.warwick.cs126.models.Favourite;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import uk.ac.warwick.cs126.structures.*;

import uk.ac.warwick.cs126.util.DataChecker;
import uk.ac.warwick.cs126.util.Sorter;

public class FavouriteStore implements IFavouriteStore {

    private MyHashSet<Long> blacklist;
    private MyHashMap<Long,Favourite> favouriteIDMap;
    private MyLinkedList<Favourite> favouritesList;
    private MyHashMap<Entry<Long,Long>,Favourite> favouriteCustomerRestaurantIDMap;
    private DataChecker dataChecker;

    public FavouriteStore() {
        // Initialise variables here
        blacklist = new MyHashSet<>();
        dataChecker = new DataChecker();
        favouriteIDMap = new MyHashMap<>();
        favouritesList = new MyLinkedList<>();
        favouriteCustomerRestaurantIDMap = new MyHashMap<>();
    }

    public Favourite[] loadFavouriteDataToArray(InputStream resource) {
        Favourite[] favouriteArray = new Favourite[0];

        try {
            byte[] inputStreamBytes = IOUtils.toByteArray(resource);
            BufferedReader lineReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int lineCount = 0;
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (!("".equals(line))) {
                    lineCount++;
                }
            }
            lineReader.close();

            Favourite[] loadedFavourites = new Favourite[lineCount - 1];

            BufferedReader csvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int favouriteCount = 0;
            String row;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split(",");
                    Favourite favourite = new Favourite(
                            Long.parseLong(data[0]),
                            Long.parseLong(data[1]),
                            Long.parseLong(data[2]),
                            formatter.parse(data[3]));
                    loadedFavourites[favouriteCount++] = favourite;
                }
            }
            csvReader.close();

            favouriteArray = loadedFavourites;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return favouriteArray;
    }

    public boolean addFavourite(Favourite favourite) {
        if (!dataChecker.isValid(favourite))return false;
        long id =favourite.getID();
        if (favouriteIDMap.contains(id)){
            remove(id);
        }
        Entry<Long,Long> ids = new Entry<>();
        ids.key=favourite.getCustomerID();
        ids.value=favourite.getRestaurantID();
        if (favouriteCustomerRestaurantIDMap.contains(ids)){
            long oldId = favouriteCustomerRestaurantIDMap.get(ids).getID();
            remove(oldId);
        }
        favouriteIDMap.put(id,favourite);
        favouriteCustomerRestaurantIDMap.put(ids,favourite);
        favouritesList.add(favourite);
        return true;
    }

    private void remove(long id){
        Favourite fake = favouriteIDMap.get(id);
        favouriteIDMap.remove(id);
        Iterator<Entry<Entry<Long,Long>,Favourite>> iterator = favouriteCustomerRestaurantIDMap.iterator();
        while (iterator.hasNext()){
            if (iterator.next().value==fake){
                iterator.remove();
                break;
            }
        }
        blacklist.add(id);
        favouritesList.removeFirst(fake);
    }

    public boolean addFavourite(Favourite[] favourites) {
        if (favourites==null)return false;
        boolean result = true;
        for (Favourite favorite:favourites) {
            result&=addFavourite(favorite);
        }
        return result;
    }

    public Favourite getFavourite(Long id) {
        if (id==null)return null;
        return favouriteIDMap.get(id);
    }

    public Favourite[] getFavourites() {
        Favourite[] favourites = new Favourite[favouritesList.size()];
        Sorter.sort(favouritesList.toArray(favourites), new Comparator<Favourite>() {
            @Override
            public int compare(Favourite o1, Favourite o2) {
                return o1.getID().compareTo(o2.getID());
            }
        });
        return favourites;
    }

    public Favourite[] getFavouritesByCustomerID(Long id) {
        if (id==null)return new Favourite[0];
        Iterator<Favourite> iterator = favouritesList.iterator();
        MyLinkedList<Favourite> allFavourite = new MyLinkedList<>();
        while (iterator.hasNext()){
            Favourite favourite = iterator.next();
            if(id.equals(favourite.getCustomerID())){
                allFavourite.add(favourite);
            }
        }
        Favourite[] favourites = new Favourite[allFavourite.size()];
        favourites=allFavourite.toArray(favourites);
        Sorter.sort(favourites, new Comparator<Favourite>() {
            @Override
            public int compare(Favourite o1, Favourite o2) {
                int a = o1.getDateFavourited().compareTo(o2.getDateFavourited());
                if (a!=0)return a;
                a = o1.getID().compareTo(o2.getID());
                return a;
            }
        });
        return favourites;
    }

    public Favourite[] getFavouritesByRestaurantID(Long id) {
        if (id==null)return new Favourite[0];
        Iterator<Favourite> iterator = favouritesList.iterator();
        MyLinkedList<Favourite> allFavourite = new MyLinkedList<>();
        while (iterator.hasNext()){
            Favourite favourite = iterator.next();
            if(id.equals(favourite.getRestaurantID())){
                allFavourite.add(favourite);
            }
        }
        Favourite[] favourites = new Favourite[allFavourite.size()];
        favourites = allFavourite.toArray(favourites);
        Sorter.sort(favourites, new Comparator<Favourite>() {
            @Override
            public int compare(Favourite o1, Favourite o2) {
                int a = o1.getDateFavourited().compareTo(o2.getDateFavourited());
                if (a!=0)return a;
                a = o1.getID().compareTo(o2.getID());
                return a;
            }
        });
        return favourites;
    }

    private MyHashMap<Long, Favourite> getRestaurantMapByCustomerID(Long customer1ID){
        Iterator<Favourite> iterator = favouritesList.iterator();
        MyLinkedList<Favourite> c1 = new MyLinkedList<>();

        while (iterator.hasNext()){
            Favourite favourite = iterator.next();
            if (customer1ID.equals(favourite.getCustomerID())){
                c1.add(favourite);
            }
        }
        Favourite[] c1Array = new Favourite[c1.size()];
        c1Array= c1.toArray(c1Array);
        MyHashMap<Long,Favourite>c1Map = new MyHashMap<>();
        for (Favourite favourite:c1Array){
            c1Map.put(favourite.getRestaurantID(),favourite);
        }

        return c1Map;
    }

    public Long[] getCommonFavouriteRestaurants(Long customer1ID, Long customer2ID) {
        if (customer1ID==null||customer2ID==null)return new Long[0];

        MyHashMap<Long, Favourite> c1Map = getRestaurantMapByCustomerID(customer1ID);
        MyHashMap<Long, Favourite> c2Map = getRestaurantMapByCustomerID(customer2ID);
        MyHashMap<Long, Favourite[]> originalResult = c1Map.common(c2Map);
        MyLinkedList<Favourite> result = new MyLinkedList<>();

        for (Entry<Long, Favourite[]> entry:originalResult){
            int compare = entry.value[0].getDateFavourited().compareTo(entry.value[1].getDateFavourited());
            if (compare>0){
                result.add(entry.value[0]);
            }else {
                result.add(entry.value[1]);
            }
        }
        return sortByOrderAndGetID(result);
    }

    public Long[] getMissingFavouriteRestaurants(Long customer1ID, Long customer2ID) {
        if (customer1ID==null||customer2ID==null)return new Long[0];

        MyHashMap<Long, Favourite> c1Map = getRestaurantMapByCustomerID(customer1ID);
        MyHashMap<Long, Favourite> c2Map = getRestaurantMapByCustomerID(customer2ID);
        MyHashMap<Long, Favourite> originalResult = c1Map.left(c2Map);
        MyLinkedList<Favourite> result = originalResult.valueList();

        return sortByOrderAndGetID(result);
    }

    private Long[] sortByOrderAndGetID(MyLinkedList<Favourite> result) {
        Favourite[] resultArray = new Favourite[result.size()];
        resultArray = result.toArray(resultArray);

        Sorter.sort(resultArray, new Comparator<Favourite>() {
            @Override
            public int compare(Favourite o1, Favourite o2) {
                int a;
                a = -o1.getDateFavourited().compareTo(o2.getDateFavourited());
                if (a!=0) return a;
                a = o1.getRestaurantID().compareTo(o2.getRestaurantID());
                return a;
            }
        });
        Long[] idArray = new Long[resultArray.length];
        for (int i = 0; i < resultArray.length; i++) {
            idArray[i]=resultArray[i].getRestaurantID();
        }
        return idArray;
    }

    public Long[] getNotCommonFavouriteRestaurants(Long customer1ID, Long customer2ID) {
        if (customer1ID==null||customer2ID==null)return new Long[0];
        MyHashMap<Long, Favourite> c1Map = getRestaurantMapByCustomerID(customer1ID);
        MyHashMap<Long, Favourite> c2Map = getRestaurantMapByCustomerID(customer2ID);
        MyHashMap<Long, Favourite> originalResult1 = c1Map.left(c2Map);
        MyHashMap<Long, Favourite> originalResult2 = c2Map.left(c1Map);
        MyLinkedList<Favourite>  result = originalResult1.valueList();
        result.add(originalResult2.valueList());
        return sortByOrderAndGetID(result);
    }
    class Comp implements Comparable<Comp>{
        Long id;
        int count;
        Date last;

        Comp(long id,int cnt,Date date){
            count=cnt;
            last=date;
            this.id=id;
        }
        @Override
        public int compareTo(Comp o) {
            int a;
            a = -(this.count-o.count);
            if (a!=0) return a;
            a = this.last.compareTo(o.last);
            if (a!=0) return a;
            a = this.id.compareTo(o.id);
            return a;

        }
    }

    public Long[] getTopCustomersByFavouriteCount()  {
        MyHashMap<Long, Comp> count = new MyHashMap<>();
        for (Favourite favourite:favouritesList) {
            Comp num = count.get(favourite.getCustomerID());
            if (num==null){
                count.put(favourite.getCustomerID(),new Comp(favourite.getCustomerID(),1,favourite.getDateFavourited()));
            }else {
                num.count+=1;
                if (num.last.compareTo(favourite.getDateFavourited())<0){
                    num.last=favourite.getDateFavourited();
                }
            }
        }
        MyLinkedList<Comp> c = count.valueList();
        Comp[] result =  new Comp[c.size()];
        result = c.toArray(result);
        Sorter.sort(result, new Comparator<Comp>() {
            @Override
            public int compare(Comp o1,  Comp o2) {
                return o1.compareTo(o2);
            }

        });
        Long[] results = new Long[20];
        for (int i = 0; i < 20&&i<result.length; i++) {
            results[i]=result[i].id;
        }
        return results;
    }

    public Long[] getTopRestaurantsByFavouriteCount() {
        MyHashMap<Long, Comp> count = new MyHashMap<>();
        for (Favourite favourite:favouritesList) {
            Comp num = count.get(favourite.getRestaurantID());
            if (num==null){
                count.put(favourite.getRestaurantID(),new Comp(favourite.getRestaurantID(),1,favourite.getDateFavourited()));
            }else {
                num.count+=1;
                if (num.last.compareTo(favourite.getDateFavourited())<0){
                    num.last=favourite.getDateFavourited();
                }
            }
        }
        MyLinkedList<Comp> c = count.valueList();
        Comp[] result =  new Comp[c.size()];
        result = c.toArray(result);
        Sorter.sort(result, new Comparator<Comp>() {
            @Override
            public int compare(Comp o1,  Comp o2) {
                return o1.compareTo(o2);
            }

        });
        Long[] results = new Long[20];
        for (int i = 0; i < 20&&i<result.length; i++) {
            results[i]=result[i].id;
        }
        return results;
    }
}
