package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.IRestaurantStore;
import uk.ac.warwick.cs126.models.Cuisine;
import uk.ac.warwick.cs126.models.EstablishmentType;
import uk.ac.warwick.cs126.models.PriceRange;
import uk.ac.warwick.cs126.models.Restaurant;
import uk.ac.warwick.cs126.models.RestaurantDistance;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import org.apache.commons.io.IOUtils;

import uk.ac.warwick.cs126.structures.*;

import uk.ac.warwick.cs126.util.*;

public class RestaurantStore implements IRestaurantStore {

    private MyHashMap<Long,Restaurant> restaurantID;
    private MyHashMap<SearchIndex,Restaurant> restaurantSearch;
    private MyLinkedList<Restaurant> restaurantsList;
    private MyHashSet<Long> blacklist;
    private DataChecker dataChecker;

    public RestaurantStore() {
        // Initialise variables here
        dataChecker = new DataChecker();
        restaurantID=new MyHashMap<>();
        restaurantSearch=new MyHashMap<>();
        restaurantsList=new MyLinkedList<>();
        blacklist = new MyHashSet<>();
    }

    public Restaurant[] loadRestaurantDataToArray(InputStream resource) {
        Restaurant[] restaurantArray = new Restaurant[0];

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

            Restaurant[] loadedRestaurants = new Restaurant[lineCount - 1];

            BufferedReader csvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            String row;
            int restaurantCount = 0;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split(",");

                    Restaurant restaurant = new Restaurant(
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            Cuisine.valueOf(data[4]),
                            EstablishmentType.valueOf(data[5]),
                            PriceRange.valueOf(data[6]),
                            formatter.parse(data[7]),
                            Float.parseFloat(data[8]),
                            Float.parseFloat(data[9]),
                            Boolean.parseBoolean(data[10]),
                            Boolean.parseBoolean(data[11]),
                            Boolean.parseBoolean(data[12]),
                            Boolean.parseBoolean(data[13]),
                            Boolean.parseBoolean(data[14]),
                            Boolean.parseBoolean(data[15]),
                            formatter.parse(data[16]),
                            Integer.parseInt(data[17]),
                            Integer.parseInt(data[18]));

                    loadedRestaurants[restaurantCount++] = restaurant;
                }
            }
            csvReader.close();

            restaurantArray = loadedRestaurants;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return restaurantArray;
    }

    public boolean addRestaurant(Restaurant restaurant) {
        Long id = dataChecker.extractTrueID(restaurant.getRepeatedID());
        restaurant.setID(id);
        if (!dataChecker.isValid(restaurant))return false;
        if (blacklist.contains(restaurant.getID()))return false;
        Restaurant fake = restaurantID.get(id);
        if (fake!=null){
            blacklist.add(id);
            restaurantID.remove(id);
            restaurantsList.removeFirst(fake);
            restaurantsList.removeFirst(fake);
            restaurantSearch.removeAllOnValue(fake);
            return false;
        }
        restaurantID.put(id,restaurant);
        restaurantsList.add(restaurant);
        restaurantSearch.put(new SearchIndex(restaurant),restaurant);
        return true;
    }

    public boolean addRestaurant(Restaurant[] restaurants) {
        boolean result = true;
        for (Restaurant restaurant: restaurants) {
            result&=addRestaurant(restaurant);
        }
        return result;
    }

    public Restaurant getRestaurant(Long id) {
        if (id==null)return null;
        return restaurantID.get(id);
    }

    public Restaurant[] getRestaurants() {
        Restaurant[] restaurants = new Restaurant[restaurantID.size()];
        restaurantsList.toArray(restaurants);
        getRestaurants(restaurants);
        return restaurants;
    }

    public Restaurant[] getRestaurants(Restaurant[] restaurants) {
        if (restaurants==null)return null;
        Sorter.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                return o1.getID().compareTo(o2.getID());
            }
        });
        return restaurants;
    }
    public Restaurant[] getRestaurantsByName() {
        Restaurant[] array = new Restaurant[restaurantsList.size()];
        array = restaurantsList.toArray(array);
        Sorter.sort(array, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                int a;
                a = o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                if (a!=0)return a;
                a = o1.getID().compareTo(o2.getID());
                return a;
            }
        });

        return array;
    }

    public Restaurant[] getRestaurantsByDateEstablished() {
        Restaurant[] array = new Restaurant[restaurantsList.size()];
        array = restaurantsList.toArray(array);
        return getRestaurantsByDateEstablished(array);
    }

    public Restaurant[] getRestaurantsByDateEstablished(Restaurant[] restaurants) {
        Sorter.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                int a;
                a = o1.getDateEstablished().compareTo(o2.getDateEstablished());
                if (a!=0)return a;
                a = o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                if (a!=0)return a;
                a = o1.getID().compareTo(o2.getID());
                return a;
            }
        });

        return restaurants;
    }

    public Restaurant[] getRestaurantsByWarwickStars() {
        Restaurant[] array = new Restaurant[restaurantsList.size()];
        array = restaurantsList.toArray(array);
        Sorter.sort(array, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                int a;
                a = -(o1.getWarwickStars()-o2.getWarwickStars());
                if (a!=0)return a;
                a = o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                if (a!=0)return a;
                a = o1.getID().compareTo(o2.getID());
                return a;
            }
        });

        return array;
    }

    public Restaurant[] getRestaurantsByRating(Restaurant[] restaurants) {
        Sorter.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                int a;
                a = -Float.compare(o1.getCustomerRating(), o2.getCustomerRating());
                if (a!=0)return a;
                a = o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                if (a!=0)return a;
                a = o1.getID().compareTo(o2.getID());
                return a;
            }
        });
        return restaurants;
    }

    public RestaurantDistance[] getRestaurantsByDistanceFrom(float latitude, float longitude) {
        Restaurant[] array = new Restaurant[restaurantsList.size()];
        array = restaurantsList.toArray(array);
        return getRestaurantsByDistanceFrom(array,latitude,longitude);
    }

    public RestaurantDistance[] getRestaurantsByDistanceFrom(Restaurant[] restaurants, float latitude, float longitude) {
        if (restaurants==null)return new RestaurantDistance[0];
        RestaurantDistance[] restaurantDistances = new RestaurantDistance[restaurants.length];
        for (int i = 0; i < restaurants.length; i++) {
            Restaurant r = restaurants[i];
            restaurantDistances[i]=new RestaurantDistance(r,
                    HaversineDistanceCalculator.inKilometres(r.getLatitude(),r.getLongitude(),latitude,longitude));
        }
        Sorter.sort(restaurantDistances, new Comparator<RestaurantDistance>() {
            @Override
            public int compare(RestaurantDistance o1, RestaurantDistance o2) {
                int a;
                a = Float.compare(o1.getDistance(),o2.getDistance());
                if (a!=0)return a;
                a = o1.getRestaurant().getID().compareTo(o2.getRestaurant().getID());
                return a;
            }
        });
        return restaurantDistances;
    }

    public Restaurant[] getRestaurantsContaining(String searchTerm) {
        MyLinkedList<Restaurant> results = new MyLinkedList<>();
        for (Entry<SearchIndex, Restaurant> e : restaurantSearch) {
            if (e.getKey().contains(searchTerm)) {
                results.add(e.getValue());
            }
        }
        Restaurant[] result = new Restaurant[results.size()];
        result=results.toArray(result);
        Sorter.sort(result, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                int a;
                a = o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                if (a!=0)return a;
                a = o1.getID().compareTo(o2.getID());
                return a;
            }
        });
        return result;
    }
}

class SearchIndex{
    String place;
    String name;
    String cuisine;
    private final static ConvertToPlace convertToPlace= new ConvertToPlace();

    SearchIndex(Restaurant restaurant){
        place=StringFormatter.convertAccentsFaster(convertToPlace.convert(restaurant.getLatitude(),restaurant.getLongitude()).getName());
        name = StringFormatter.convertAccentsFaster(restaurant
                .getName()
                .toLowerCase()
                .trim()
                .replaceAll("\\w{1,}"," "));
        cuisine = StringFormatter.convertAccentsFaster(restaurant.getCuisine().toString().toLowerCase());
    }

    boolean contains(String word){
        word = StringFormatter.convertAccentsFaster(word.toLowerCase().trim());
        return place.contains(word)
                || name.contains(word)
                || cuisine.contains(word);
    }
}