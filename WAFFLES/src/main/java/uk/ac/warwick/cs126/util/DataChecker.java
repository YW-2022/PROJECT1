package uk.ac.warwick.cs126.util;

import uk.ac.warwick.cs126.interfaces.IDataChecker;

import uk.ac.warwick.cs126.models.Customer;
import uk.ac.warwick.cs126.models.Restaurant;
import uk.ac.warwick.cs126.models.Favourite;
import uk.ac.warwick.cs126.models.Review;
import uk.ac.warwick.cs126.structures.MyHashMap;

import java.util.Date;

public class DataChecker implements IDataChecker {

    public DataChecker() {
        // Initialise things here
    }

    public Long extractTrueID(String[] repeatedID) {
        if (repeatedID==null)return null;
        if (repeatedID.length!=3)return null;
        for (int i = 0; i < repeatedID.length; i++) {
            for (int j = i+1; j < repeatedID.length; j++) {
                if (repeatedID[i].equals(repeatedID[j]))return Long.parseLong(repeatedID[j]);
            }
        }
        return null;
    }

    public boolean isValid(Long inputID) {
        if (inputID==null)return false;
        if ((int)Math.log10(inputID)!=15)return false;
        MyHashMap<Integer,Integer> counter = new MyHashMap<>();
        long id = inputID;
        while (id>0){
            int n = (int) (id%10);
            if (n==0)return false;
            Integer count = counter.get(n);
            if (count==null){
                counter.put(n,1);
            }else {
                if (count==3)return false;
                counter.put(n,count+1);
            }
            id/=10;
        }
        return true;
    }

    public boolean isValid(Customer customer) {
        return customer!=null && isValid(customer.getID());
    }

    public boolean isValid(Restaurant restaurant) {
        return restaurant!=null
                && isValid(restaurant.getID())
                && restaurant.getLastInspectedDate()!=null
                && restaurant.getCuisine()!=null
                && restaurant.getDateEstablished()!=null
                && restaurant.getEstablishmentType()!=null
                && restaurant.getName()!=null
                && restaurant.getStringID()!=null
                && restaurant.getOwnerFirstName()!=null
                && restaurant.getOwnerLastName()!=null
                && restaurant.getRepeatedID()!=null
                && restaurant.getLastInspectedDate().compareTo(restaurant.getDateEstablished())>0
                && restaurant.getFoodInspectionRating()<=5
                && restaurant.getFoodInspectionRating()>=0
                && restaurant.getWarwickStars()>=0
                && restaurant.getWarwickStars()<=3
                && (restaurant.getCustomerRating()==0
                ||(restaurant.getCustomerRating()>=1&&restaurant.getCustomerRating()<=5));

    }

    public boolean isValid(Favourite favourite) {
        return favourite!=null
                && isValid(favourite.getID())
                && isValid(favourite.getCustomerID())
                && isValid(favourite.getRestaurantID())
                && favourite.getDateFavourited()!=null;
    }

    public boolean isValid(Review review) {
        return review!=null
                &&review.getID()!=null
                &&review.getReview()!=null
                &&review.getCustomerID()!=null
                &&review.getRestaurantID()!=null
                &&review.getStringID()!=null
                &&review.getDateReviewed()!=null
                &&isValid(review.getID())
                &&isValid(review.getCustomerID())
                &&isValid(review.getRestaurantID());
    }
}