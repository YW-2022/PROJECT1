package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.IReviewStore;
import uk.ac.warwick.cs126.models.Review;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import uk.ac.warwick.cs126.structures.*;

import uk.ac.warwick.cs126.util.*;

public class ReviewStore implements IReviewStore {

    private MyHashSet<Long> blacklist;
    private MyLinkedList<Review> reviews;
    private MyHashMap<IDs,Review> reviewsCustomerID;
    private MyHashMap<Long,Review> reviewsID;
    private DataChecker dataChecker;
    private KeywordChecker keywordChecker;
    public ReviewStore() {
        // Initialise variables here
        dataChecker = new DataChecker();
        blacklist=new MyHashSet<>();
        reviews=new MyLinkedList<>();
        reviewsCustomerID = new MyHashMap<>();
        reviewsID = new MyHashMap<>();
        keywordChecker= new KeywordChecker();
    }

    public Review[] loadReviewDataToArray(InputStream resource) {
        Review[] reviewArray = new Review[0];

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

            Review[] loadedReviews = new Review[lineCount - 1];

            BufferedReader tsvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int reviewCount = 0;
            String row;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            tsvReader.readLine();
            while ((row = tsvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split("\t");
                    Review review = new Review(
                            Long.parseLong(data[0]),
                            Long.parseLong(data[1]),
                            Long.parseLong(data[2]),
                            formatter.parse(data[3]),
                            data[4],
                            Integer.parseInt(data[5]));
                    loadedReviews[reviewCount++] = review;
                }
            }
            tsvReader.close();

            reviewArray = loadedReviews;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return reviewArray;
    }

    public boolean addReview(Review review) {
        if (!dataChecker.isValid(review))return false;
        if (blacklist.contains(review.getID()))return false;
        Review fake =reviewsID.get(review.getID());
        if (fake!=null){
            remove(fake);
            return false;
        }
        Review dup = reviewsCustomerID.get(new IDs(review.getCustomerID(),review.getRestaurantID()));
        if (dup!=null){
            remove(dup);
            add(review);
            return true;
        }
        add(review);
        return true;
    }

    private void remove(Review review){
        reviews.removeAll(review);
        reviewsCustomerID.removeAllOnValue(review);
        reviewsID.remove(review.getID());
        blacklist.add(review.getID());
    }
    private void add(Review review){
        reviews.add(review);
        reviewsCustomerID.put(new IDs(review.getCustomerID(),review.getRestaurantID()),review);
        reviewsID.put(review.getID(),review);
    }

    public boolean addReview(Review[] reviews) {
        boolean result = true;
        for (Review review:reviews) {
            result&=addReview(review);
        }
        return result;
    }

    public Review getReview(Long id) {
        if (id==null)return null;
        return reviewsID.get(id);
    }

    public Review[] getReviews() {
        Review[] r = new Review[reviews.size()];
        r = reviews.toArray(r);
        Sorter.sort(r, new Comparator<Review>() {
            @Override
            public int compare(Review o1, Review o2) {
                int a;
                a = o1.getID().compareTo(o2.getID());
                return a;
            }
        });
        return r;
    }

    public Review[] getReviewsByDate() {
        return Sorter.sort(getReviews(), new Comparator<Review>() {
            @Override
            public int compare(Review o1, Review o2) {
                int a;
                a = -o1.getDateReviewed().compareTo(o2.getDateReviewed());
                if (a!=0)return a;
                a = o1.getID().compareTo(o2.getID());
                return a;
            }
        });

    }

    public Review[] getReviewsByRating() {
        return Sorter.sort(getReviews(), new Comparator<Review>() {
            @Override
            public int compare(Review o1, Review o2) {
                int a;
                a = -Integer.compare(o1.getRating(),o2.getRating());
                if (a!=0)return a;
                a = -o1.getDateReviewed().compareTo(o2.getDateReviewed());
                if (a!=0)return a;
                a = o1.getID().compareTo(o2.getID());
                return a;
            }
        });
    }

    public Review[] getReviewsByCustomerID(Long id) {
//        for (Review review: reviews) {
//
//        }
        if (id==null) return new Review[0];
        MyLinkedList<Review> r = new MyLinkedList<>();
        for (Review review:reviews) {
            if (review.getCustomerID().equals(id)){
                r.add(review);
            }
        }

        return sortByDateID(r);
    }

    private Review[] sortByDateID(MyLinkedList<Review> r) {
        Review[] ra = new Review[r.size()];
        ra= r.toArray(ra);

        return Sorter.sort(ra, new Comparator<Review>() {
            @Override
            public int compare(Review o1, Review o2) {
                int a;
                a = -o1.getDateReviewed().compareTo(o2.getDateReviewed());
                if (a!=0)return a;
                a = o1.getID().compareTo(o2.getID());
                return a;
            }
        });
    }

    public Review[] getReviewsByRestaurantID(Long id) {
        if (id==null) return new Review[0];
        MyLinkedList<Review> r = new MyLinkedList<>();
        for (Review review:reviews) {
            if (review.getRestaurantID().equals(id)){
                r.add(review);
            }
        }

        return sortByDateID(r);
    }

    public float getAverageCustomerReviewRating(Long id) {
        if (id==null)return 0f;
        int count = 0;
        float all = 0;
        for (Review r:reviews) {
            if (r.getCustomerID().equals(id)){
                count++;
                all+=r.getRating();
            }
        }
        if (count==0)return 0;
        return FloatTo1.floatTo1(all/count);
    }

    public float getAverageRestaurantReviewRating(Long id) {
        if (id==null)return 0f;
        int count = 0;
        float all = 0;
        for (Review r:reviews) {
            if (r.getRestaurantID().equals(id)){
                count++;
                all+=r.getRating();
            }
        }
        if (count==0)return 0;
        return FloatTo1.floatTo1(all/count);
    }

    public int[] getCustomerReviewHistogramCount(Long id) {
        if (id==null)return new int[5];
        int[] stars = new int[5];
        for (Review r:reviews) {
            if (r.getCustomerID().equals(id)){
                stars[r.getRating()-1]++;
            }
        }
        return stars;
    }

    public int[] getRestaurantReviewHistogramCount(Long id) {
        if (id==null)return new int[5];
        int[] stars = new int[5];
        for (Review r:reviews) {
            if (r.getRestaurantID().equals(id)){
                stars[r.getRating()-1]++;
            }
        }
        return stars;
    }


    public Long[] getTopCustomersByReviewCount() {
        MyHashMap<Long,Comp> r = new MyHashMap<>();
        for (Review review:reviews) {
            Comp t = r.get(review.getCustomerID());
            if (t!=null){
                t.count++;
                if (t.date.compareTo(review.getDateReviewed())<0){
                    t.date=review.getDateReviewed();
                }
            }else {
                r.put(review.getCustomerID(),new Comp(review.getDateReviewed(),review.getCustomerID()));
            }
        }
        return sortByCountDateId((MyHashMap<Long, Comp>) r);
    }

    public Long[] getTopRestaurantsByReviewCount() {
        MyHashMap<Long,Comp> r = new MyHashMap<>();
        for (Review review:reviews) {
            Comp t = r.get(review.getRestaurantID());
            if (t!=null){
                t.count++;
                if (t.date.compareTo(review.getDateReviewed())<0){
                    t.date=review.getDateReviewed();
                }
            }else {
                r.put(review.getRestaurantID(),new Comp(review.getDateReviewed(),review.getRestaurantID()));
            }
        }
        return sortByCountDateId((MyHashMap<Long, Comp>) r);
    }

    private Long[] sortByCountDateId(MyHashMap<Long, Comp> r) {
        MyLinkedList<Comp> list = r.valueList();
        Comp[] a = new Comp[list.size()];
        a=list.toArray(a);
        Sorter.sort(a, new Comparator<Comp>() {
            @Override
            public int compare(Comp o1, Comp o2) {
                return o1.compareTo(o2);
            }
        });
        Long[] results = new Long[20];
        for (int i = 0; i < 20&&i<a.length; i++) {
            results[i]=a[i].id;
        }
        return results;
    }


    public Long[] getTopRatedRestaurants() {
        MyHashMap<Long,Comp2> r = new MyHashMap<>();
        for (Review review:reviews) {
            Comp2 t = r.get(review.getRestaurantID());
            if (t!=null){
                t.put(review.getRating());
                if (t.date.compareTo(review.getDateReviewed())<0){
                    t.date=review.getDateReviewed();
                }
            }else {
                Comp2 a = new Comp2(review.getDateReviewed(),review.getRestaurantID());
                a.put(review.getRating());
                r.put(review.getRestaurantID(),a);
            }
        }

        MyLinkedList<Comp2> list = r.valueList();
        Comp2[] a = new Comp2[list.size()];
        a=list.toArray(a);

        Sorter.sort(a, new Comparator<Comp2>() {
            @Override
            public int compare(Comp2 o1, Comp2 o2) {
                return o1.compareTo(o2);
            }
        });

        Long[] results = new Long[20];
        for (int i = 0; i < 20&&i<a.length; i++) {
            results[i]=a[i].id;
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public String[] getTopKeywordsForRestaurant(Long id) {
        if (id==null)return new String[5];
        Review[] reviews = getReviewsByRestaurantID(id);
        MyHashMap<String,Integer> count = new MyHashMap<>();
        for (Review review:reviews) {
            String l = review.getReview().replaceAll("[-!$%^&*()_+|~=`{}\\[\\]:\";'<>?,./]"," ").toLowerCase();
            String[] a=l.split(" ");
            for (String s:a) {
                boolean r = keywordChecker.isAKeyword(s);
                if (r){
                    Integer i =count.get(s);
                    if (i==null){
                        count.put(s,1);
                    }else {
                        count.put(s,i+1);
                    }
                }
            }
        }

        Entry<String,Integer>[] array = (Entry<String,Integer>[]) new Entry[count.size()];
        count.toArray(array);
        Sorter.sort(array, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                int a;
                a = -(o1.value-o2.value);
                if (a!=0)return a;
                a = String.CASE_INSENSITIVE_ORDER.compare(o1.key,o2.key);
                return a;
            }
        });
        String[] results = new String[5];
        for (int i = 0; i < 5&&i<array.length; i++) {
            results[i]=array[i].key;
        }
        return results;
    }

    public Review[] getReviewsContaining(String searchTerm) {
        if (searchTerm==null)return new Review[0];
        searchTerm = StringFormatter.convertAccentsFaster(searchTerm).toLowerCase();
        MyLinkedList<Review> r = new MyLinkedList<>();
        for (Review review:reviews) {
            boolean contain = review.getReview().toLowerCase().contains(searchTerm);
            if (contain)r.add(review);
        }
        Review[] rs = new Review[r.size()];
        r.toArray(rs);
        return rs;
    }

}
class IDs{
    long cid;
    long rid;

    IDs(long cid,long rid){
        this.cid=cid;
        this.rid=rid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IDs iDs = (IDs) o;

        if (cid != iDs.cid) return false;
        return rid == iDs.rid;
    }

    @Override
    public int hashCode() {
        int result = (int) (cid ^ (cid >>> 32));
        result = 31 * result + (int) (rid ^ (rid >>> 32));
        return result;
    }
}
class Comp implements Comparable<Comp>{
    int count;
    Date date;
    long id;

    Comp(Date date,long id){
        count=1;
        this.date=date;
        this.id=id;
    }

    @Override
    public int compareTo(Comp o) {
        int a;
        a = -(this.count-o.count);
        if (a!=0)return a;
        a = this.date.compareTo(o.date);
        if (a!=0)return a;
        a = Long.compare(this.id,o.id);
        return a;
    }
}


class Comp2 implements Comparable<Comp2>{
    float sum;
    float count;
    Date date;
    long id;

    Comp2(Date date,long id){
        this.date=date;
        this.id=id;
    }

    void put (float score){
        sum+=score;
        count+=1;

    }
    float avg (){
        if (count==0)return 0;
        return sum/count;
    }

    @Override
    public int compareTo(Comp2 o) {
        int a;
        a = -Float.compare(this.avg(),o.avg());
        if (a!=0)return a;
        a = this.date.compareTo(o.date);
        if (a!=0)return a;
        a = Long.compare(this.id,o.id);
        return a;
    }
}
