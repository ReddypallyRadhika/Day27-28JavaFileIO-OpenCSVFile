package com.bridgeLabz;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class AddressBook implements AddressBookIF {
    Scanner scannerObject = new Scanner(System.in);
    public Map<String, ContactPerson> contactList = new HashMap<>();
    public static HashMap<String, ArrayList<ContactPerson>> personByCity = new HashMap<>();
    public static HashMap<String, ArrayList<ContactPerson>> personByState = new HashMap<>();
    public String addressBookName;
    public boolean isPresent = false;
    private static final String LIST_SAMPLE="./list-sample.csv";
    private static final String LIST_SAMPLE1="./list-sample.json";
    public String getAddressBookName() {
        return addressBookName;
    }

    public void setAddressBookName(String addressBookName) {
        this.addressBookName = addressBookName;
    }

    public ArrayList<ContactPerson> getContact() {
        return new ArrayList<>(contactList.values());
    }

    @Override
    public void operation() {
        boolean moreChanges = true;
        do {
            System.out.println("\nChoose the operation you want to perform");
            System.out.println("""
                    1.Add To Address
                    2.Edit Existing Entry
                    3.Delete Contact
                    4.Display Address Book
                    5.Display Sorted Address Book By Custom Criteria
                    6.Write To File
                    7.Read From File
                    8.Write To Open CSV File
                    9.Read From Open CSV File
                    10.Read From Json File""");
            switch (scannerObject.nextInt()) {
                case 1 -> addContact();
                case 2 -> editPerson();
                case 3 -> deletePerson();
                case 4 -> displayContents();
                case 5 -> {
                    System.out.println("What Criteria Do you Want In AddressBook To Be Sorted In?");
                    System.out.println("1.firstName\n2.City\n3.state\n4.zipCode");
                    int sortingChoice = scannerObject.nextInt();
                    sortAddressBook(sortingChoice);
                }
                case 6 -> {
                    writeToAddressBookFile();
                    System.out.println("Written TO File");
                }
                case 7 -> {
                    readFromFile();
                    System.out.println("Reading From File");
                }
                case 8 -> {
                    try {
                        writeToAddressBookCSVFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (CsvDataTypeMismatchException e) {
                        throw new RuntimeException(e);
                    } catch (CsvRequiredFieldEmptyException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Writing To Open CSV File");
                }
                case 9-> {
                    try {
                        readFromAddressBookCSVFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Reading From Open CSV File");
                }
                case 10-> {
                    JSonReaderAndWriter();
                    System.out.println("Reading From Open CSV File");
                }
                case 11 -> {
                    moreChanges = false;
                    System.out.println("Exiting Address Book:" + this.getAddressBookName() + " !");
                }
            }
        } while (moreChanges);
    }


    @Override
    public void addContact() {
        ContactPerson person = new ContactPerson();
        Address address = new Address();
        System.out.println("Enter First Name:");
        String firstName = scannerObject.next();
        contactList.forEach((key, value) -> {
            if (key.equals(firstName.toLowerCase())) {
                System.out.println("Contact Already Exist");
                isPresent = true;
            }
        });

        if (!isPresent) {
            System.out.println("Enter Last Name:");
            String lastName = scannerObject.next();
            System.out.println("Enter Phone Number:");
            long phoneNumber = scannerObject.nextLong();
            System.out.println("Enter Email:");
            String email = scannerObject.next();
            System.out.println("Enter City:");
            String city = scannerObject.next();
            System.out.println("Enter State:");
            String state = scannerObject.next();
            System.out.println("Enter Zip Code:");
            long zipCode = scannerObject.nextLong();
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setPhoneNumber(phoneNumber);
            person.setAddress(address);
            person.setEmail(email);
            address.setCity(city);
            address.setState(state);
            address.setZip(zipCode);
            addPersonToCity(person);
            addressPersonToState(person);
            contactList.put(firstName.toLowerCase(), person);


        }
    }
    @Override
    public void editPerson () {
        ContactPerson person = new ContactPerson();
        Address address = new Address();
            System.out.println("\nChoose the operation you want to perform");

            System.out.println("1.Last Name\n2.Phone Number\n3.Email\n4.City\n5.State\n6.Zip Code");
        int choice = scannerObject.nextInt();
        switch (choice) {
            case 1 -> {
                System.out.println("Enter the correct Last Name");
                String lastName = scannerObject.next();
                person.setLastName(lastName);
            }
            case 2 -> {
                System.out.println("Enter the correct Phone Number");
                long phoneNumber = scannerObject.nextLong();
                person.setPhoneNumber(phoneNumber);
            }
            case 3 -> {
                System.out.println("Enter the correct Email Address");
                String email = scannerObject.next();
                person.setEmail(email);
            }
            case 4 -> {
                System.out.println("Enter the correct City");
                String city = scannerObject.next();
                address.setCity(city);
            }
            case 5 -> {
                System.out.println("Enter the correct State");
                String state = scannerObject.next();
                address.setState(state);
            }
            case 6 -> {
                System.out.println("Enter the correct zip code");
                long zip = scannerObject.nextLong();
                address.setZip(zip);
            }
            default -> System.out.println("Book Does Not Exist");
        }


}
    @Override
    public void deletePerson (){
        System.out.println("Enter the first name of the person to be deleted");
        String firstName=scannerObject.next();
        if(contactList.containsKey(firstName)) {
            contactList.remove(firstName);
            System.out.println("Successfully deleted");
        }
        else{
            System.out.println("Contact Not Found");
        }
}

    @Override
    public void displayContents () {
        System.out.println("------Contents Of AddressBook"+this.getAddressBookName()+"------");
        for(String eachContact: contactList.keySet()) {
            ContactPerson person = contactList.get(eachContact);
            System.out.println(person);
        }
        System.out.println("------");
    }
    private void addressPersonToState(ContactPerson contact) {
        if (personByState.containsKey(contact.getAddress().getState())) {
            personByState.get(contact.getAddress().getState()).add(contact);
        }
        else {
            ArrayList<ContactPerson> stateList = new ArrayList<>();
            stateList.add(contact);
            personByState.put(contact.getAddress().getState(), stateList);
        }
    }

    private void addPersonToCity(ContactPerson contact){
        if (personByCity.containsKey(contact.getAddress().getCity())) {
            personByCity.get(contact.getAddress().getCity()).add(contact);
        } else {
            ArrayList<ContactPerson> cityList = new ArrayList<>();
            cityList.add(contact);
            personByCity.put(contact.getAddress().getCity(), cityList);
        }
    }
public void printSortedList(List<ContactPerson> sortedContactList) {
    System.out.println("----Sorted AddressBook " + this.getAddressBookName() + "------");
    Iterator<ContactPerson> iterator = sortedContactList.iterator();
    while (iterator.hasNext()) {
        System.out.println();
    }
    System.out.println("--------------------");
}

    private void sortAddressBook(int sortingChoice) {
                List<ContactPerson> sortedContactList;
        switch (sortingChoice) {
            case 1 -> {
                sortedContactList = contactList.values().stream().
                        sorted((firstPerson, secondPerson) -> firstPerson.getFirstName()
                                .compareTo(secondPerson.getFirstName())).collect(Collectors.toList());
                printSortedList(sortedContactList);
            }
            case 2 -> {
                sortedContactList = contactList.values().stream().
                        sorted((firstPerson, secondPerson) -> firstPerson.getAddress().getCity()
                                .compareTo(secondPerson.getAddress().getCity())).collect(Collectors.toList());
                printSortedList(sortedContactList);
            }
            case 3 -> {
                sortedContactList = contactList.values().stream().
                        sorted((firstPerson, secondPerson) -> firstPerson.getAddress().getState()
                                .compareTo(secondPerson.getAddress().getState())).collect(Collectors.toList());
                printSortedList(sortedContactList);
            }
            case 4 -> {
                sortedContactList = contactList.values().stream().
                        sorted((firstPerson, secondPerson) -> Long.compare(firstPerson.getAddress().getZip(), secondPerson.getAddress().getZip())).collect(Collectors.toList());
                printSortedList(sortedContactList);
            }
        }
            }


    private void writeToAddressBookFile() {
        String addressBook=this.getAddressBookName();
        String fileName=addressBookName+".txt";
        StringBuffer addressBookBuffer=new StringBuffer();
        contactList.values().forEach(contactPerson-> {
            String personDataString = contactPerson.toString().concat("\n");
            addressBookBuffer.append(personDataString);
        });
        try {
            Files.write(Paths.get(fileName), addressBookBuffer.toString().getBytes());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    private List<String> readFromFile() {
        List<String> addressBookList=new ArrayList<>();
        String bookName=this.getAddressBookName();
        String fileName2=bookName+".txt";
        System.out.println("Reading from the file"+fileName2+"\n");
        try {
            Files.lines(new File(fileName2).toPath()).map(lines -> lines.trim())
                    .forEach(addressPersonDetails->{
            System.out.println(addressPersonDetails);
            addressBookList.add(addressPersonDetails);
        });
        }catch (IOException e) {
            e.printStackTrace();
        }return addressBookList;
    }
    private void writeToAddressBookCSVFile()throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

        try (
         Writer  writer = Files.newBufferedWriter(Paths.get(LIST_SAMPLE));
        ){

            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).build();
            List<AddressBookDirectory> addressBookDirectories= new ArrayList<>();
            addressBookDirectories.add(new AddressBookDirectory());

            beanToCsv.write(addressBookDirectories);

        }

    }

    public void readFromAddressBookCSVFile() throws IOException {
        try(
                Reader reader = Files.newBufferedReader(Paths.get(LIST_SAMPLE));
                CSVReader csvReader = new CSVReader(reader);
        ){
            List<String[]> records = csvReader.readAll();

            for (String[] record : records){
                System.out.println("ADDRESSBookName: "+record[0]);
                System.out.println("FirstName: "+record[1]);
                System.out.println("LastName: "+record[2]);
                System.out.println("City: "+record[3]);
                System.out.println("Email: "+record[4]);
                System.out.println("PhoneNo: "+record[5]);
                System.out.println("State: "+record[6]);
                System.out.println("Zip: "+record[7]);
                System.out.println("Address: "+record[8]);
                System.out.println("======================");
            }
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }
    public void JSonReaderAndWriter(){
        try{
            Reader reader=Files.newBufferedReader(Paths.get(LIST_SAMPLE1));
            CsvToBeanBuilder<ContactPerson> csvToBeanBuilder=new CsvToBeanBuilder<>(reader);
            csvToBeanBuilder.withType(ContactPerson.class);
            csvToBeanBuilder.withIgnoreLeadingWhiteSpace(true);
            CsvToBean<ContactPerson> csvToBean=csvToBeanBuilder.build();
            List<ContactPerson> contactPeople=csvToBean.parse();
            Gson gson =new Gson();
            String json=gson.toJson(contactPeople);
            FileWriter writer=new FileWriter(LIST_SAMPLE1);
            writer.write(json);
            writer.close();
            BufferedReader bufferedReader=new BufferedReader(new FileReader(LIST_SAMPLE1));
            ContactPerson[] contactPeople1=gson.fromJson(bufferedReader, ContactPerson[].class);
            List<ContactPerson> csvUserList=Arrays.asList(contactPeople1);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
