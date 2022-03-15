package uk.ac.warwick.cs126.stores;
import uk.ac.warwick.cs126.interfaces.ICustomerStore;
import uk.ac.warwick.cs126.models.Customer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import uk.ac.warwick.cs126.structures.Entry;
import uk.ac.warwick.cs126.structures.MyHashMap;
import uk.ac.warwick.cs126.structures.MyHashSet;
import uk.ac.warwick.cs126.structures.MyLinkedList;
import uk.ac.warwick.cs126.util.DataChecker;
import uk.ac.warwick.cs126.util.NameMaker;
import uk.ac.warwick.cs126.util.Sorter;
import uk.ac.warwick.cs126.util.StringFormatter;

public class CustomerStore implements ICustomerStore {

    private final MyLinkedList<Customer> customerArray;
    private final MyHashMap<Long,Customer> customerIdMap;
    private final MyHashMap<String,Customer> custmerNameMap;
    private final DataChecker dataChecker;
    private final MyHashSet<Long> blacklist;

    public CustomerStore() {
        this.blacklist = new MyHashSet<>();
        // Initialise variables here
        customerArray = new MyLinkedList<>();
        customerIdMap = new MyHashMap<>();
        custmerNameMap = new MyHashMap<>();
        dataChecker = new DataChecker();
    }

    public Customer[] loadCustomerDataToArray(InputStream resource) {
        Customer[] customerArray = new Customer[0];

        try {
            byte[] inputStreamBytes = IOUtils.toByteArray(resource);
            BufferedReader lineReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int lineCount = 0;
            String line;
            while ((line=lineReader.readLine()) != null) {
                if (!("".equals(line))) {
                    lineCount++;
                }
            }
            lineReader.close();

            Customer[] loadedCustomers = new Customer[lineCount - 1];

            BufferedReader csvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int customerCount = 0;
            String row;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split(",");

                    Customer customer = (new Customer(
                            Long.parseLong(data[0]),
                            data[1],
                            data[2],
                            formatter.parse(data[3]),
                            Float.parseFloat(data[4]),
                            Float.parseFloat(data[5])));

                    loadedCustomers[customerCount++] = customer;
                }
            }
            csvReader.close();

            customerArray = loadedCustomers;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return customerArray;
    }

    public boolean addCustomer(Customer customer) {
        if(!dataChecker.isValid(customer)) return false;

        long id =customer.getID();
        if(blacklist.contains(id))return false;

        if(customerIdMap.contains(id)) {
            blacklist.add(id);
            Customer fake = customerIdMap.get(id);
            customerArray.removeFirst(fake);
            customerIdMap.remove(id);
            Iterator<Entry<String,Customer>> iterator = custmerNameMap.iterator();
            while (iterator.hasNext()){
                if (iterator.next().value==fake){
                    iterator.remove();
                    break;
                }
            }
            return false;
        }


        customerArray.add(customer);
        customerIdMap.put(customer.getID(),customer);
        custmerNameMap.put(NameMaker.nameMaker(customer),customer);
        return true;
    }


    public boolean addCustomer(Customer[] customers) {
        if (customers==null)return false;
        boolean result = true;
        for (Customer customer: customers){
            result&=addCustomer(customer);
        }
        return result;
    }

    public Customer getCustomer(Long id) {
        if (id==null)return null;
        return customerIdMap.get(id);
    }

    public Customer[] getCustomers() {
        Customer[] array = new Customer[customerArray.size()];
        array=customerArray.toArray(array);
        getCustomers(array);
        return array;
    }

    public Customer[] getCustomers(Customer[] customers) {
        if (customers==null)return null;
        Sorter.sort(customers, new Comparator<Customer>() {
            @Override
            public int compare(Customer o1, Customer o2) {
                return o1.getID().compareTo(o2.getID());
            }
        });
        return customers;
    }

    public Customer[] getCustomersByName() {
        Customer[] array = new Customer[customerArray.size()];
        array = customerArray.toArray(array);
        getCustomersByName(array);
        return array;
    }

    public Customer[] getCustomersByName(Customer[] customers) {
        if (customers==null)return new Customer[0];
        Sorter.sort(customers, new Comparator<Customer>() {
                    @Override
                    public int compare(Customer o1, Customer o2) {
                        int resultA= o1.getLastName().toLowerCase().compareTo(o2.getLastName().toLowerCase());
                        if (resultA!=0)return resultA;
                        int resultB= o1.getFirstName().toLowerCase().compareTo(o2.getFirstName().toLowerCase());
                        if (resultB!=0)return resultB;
                        int resultC= o1.getID().compareTo(o2.getID());
                        return resultC;
                    }
                }
        );
        return customers;
    }

    public Customer[] getCustomersContaining(String searchTerm) {
        if (searchTerm==null||searchTerm.equals(""))return new Customer[0];
        searchTerm = StringFormatter.convertAccentsFaster(searchTerm).toLowerCase();
        MyLinkedList<Customer> results = new MyLinkedList<>();
        for (Customer customer: customerArray){
            if(customer.getFirstName().toLowerCase().contains(searchTerm)
                    ||customer.getLastName().toLowerCase().contains(searchTerm)){
                results.add(customer);
            }
        }
        Customer[] array = new Customer[results.size()];
        results.toArray(array);
        getCustomersByName(array);
        return array;
    }

}
