package uk.ac.warwick.cs126.util;

import uk.ac.warwick.cs126.models.Customer;

public class NameMaker {
    public static String nameMaker(Customer customer){
        return StringFormatter.convertAccentsFaster(customer.getFirstName().trim()).replaceAll("\\w{2,}", " ").toLowerCase() +
                " " +
                StringFormatter.convertAccentsFaster(customer.getFirstName().trim()).replaceAll("\\w{2,}", " ").toLowerCase();

    }
}
