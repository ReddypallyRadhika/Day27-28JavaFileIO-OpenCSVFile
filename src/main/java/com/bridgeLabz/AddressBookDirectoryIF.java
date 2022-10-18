package com.bridgeLabz;

import java.util.ArrayList;
import java.util.HashMap;

public interface AddressBookDirectoryIF {
    void addAddressBook();
    void operationDirectory();
    void displayDirectoryContents();
    void editAddressBook();
    void searchByCity();
    void searchByState();
    void displayPeopleByRegion(HashMap<String, ArrayList<ContactPerson>>listToDisplay);
    void countPeopleByRegion(HashMap<String, ArrayList<ContactPerson>>listToDisplay);
}
