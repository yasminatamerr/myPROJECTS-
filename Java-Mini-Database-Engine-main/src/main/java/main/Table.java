package main.java.main;
import main.java.exceptions.*;
import main.java.index.OctTree;

import java.io.*;
import java.util.*;
public class Table implements Serializable{

    private final String tableName;
    private final Vector<PageDetails> details;
    private int numberOfRecords;
    private int numberOfPages;
    private transient boolean inserting;
    private boolean indexed;
    private Vector<String> vecIndexList;

    public Table(String tableName) {
        this.tableName = tableName;
        details = new Vector<>();
        vecIndexList = new Vector<>();
        numberOfPages = 0;
        numberOfRecords = 0;
    }

    public String getTableName() {
        return tableName;
    }

    public int getNumberOfRecords(){
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords){
        this.numberOfRecords = numberOfRecords;
    }

    public void setNumberOfPages(int numberOfPages){
        this.numberOfPages = numberOfPages;
    }

    public int getNumberOfPages(){
        return numberOfPages;
    }

    public void addNewPage(Page page){
        details.add(new PageDetails(page.getName(), numberOfPages, page.getRecords().get(0), page.getRecords().get(0)));
        page.savePage();
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public Vector<String> getVecIndexList() {
        return vecIndexList;
    }

    public void saveTable(){
        try{
            FileOutputStream fileOut = new FileOutputStream("src/main/resources/data/" + this.tableName + ".class");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            fileOut.close();
            out.close();
        }catch(Exception ignored){

        }
    }

    public void createIndex(String strClusteringKey, String indexName, Hashtable<String, Double> htblMinMax){
        OctTree index = new OctTree(indexName, htblMinMax);
        if(numberOfRecords > 0){
            for(PageDetails e : details){
                Page p = loadPage("src/main/resources/data/" + e.getPageName() + ".class");
                assert p != null;
                for(Hashtable<String, Object> s : p.getRecords()){
                    System.out.println(s);
                    index.insert(strClusteringKey, e.getPageName(), s);
                }
            }
        }
        vecIndexList.add(indexName);
        index.saveIndex();
    }

    /**
     * This method is made to clear the whole table in case the input to the delete method has an
     * <b><i>empty hashtable</i></b>.*/
    public void truncateTable(){
        Vector<OctTree> indices = loadIndices(); // loading all the indices on the table
        for(OctTree e : indices) // looping over all the indices
            e.truncateIndex(); // resetting all the indices of the table
        for(PageDetails e : details){ // looping over all pages if the hashtable is empty
            File page = new File("src/main/resources/data/" + e.getPageName() + ".class"); // creating a file variable for every page in the table
            page.delete(); // deleting every page in the table
        }
        numberOfRecords = 0; // resetting the number of records in the table
        numberOfPages = 0; // resetting the number of pages in the table
        details.clear(); // clearing the details vector
    }

    @SuppressWarnings("unchecked")
    private Page loadPage(String filePath){
        Vector<Hashtable<String, Object>> result;
        try{
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            result = (Vector<Hashtable<String, Object>>) in.readObject();
            fileIn.close();
            in.close();
            return new Page(result);
        }catch (Exception e1){
            return null;
        }
    }

    /** compareWith(o, e) > 0 if o > e, < 0 if o < e, = 0 if o = e*/
    public static int compareWith(Object o, Object e){
        if(o instanceof Integer && e instanceof Integer)
            return ((Integer)o).compareTo((Integer)e);
        else if(o instanceof Double && e instanceof Double)
            return ((Double)o).compareTo((Double)e);
        else if(o instanceof String && e instanceof String)
            return ((String)o).compareTo((String)e);
        else if(o instanceof Date && e instanceof Date)
            return ((Date)o).compareTo((Date)e);
        return 0;
    }

    private int binarySearch(String strClusteringKey, Hashtable<String, Object> record,
                             Vector<Hashtable<String, Object>> page) throws InvalidInputException {
        int start = 0;
        int end = page.size() - 1;
        int middle = (start + end) / 2;
        while(start < end){
            int comparison = compareWith(record.get(strClusteringKey), page.get(middle).get(strClusteringKey));
            if(comparison == 1){
                start = middle + 1;
            }else if(comparison == -1){
                end = middle;
            }else if(comparison == 0){
                if(inserting)
                    throw new InvalidInputException("Clustering Key already exists");
                else
                    return middle;
            }
            middle = (start + end) / 2;
        }
        return middle;
    }
    
    public Vector<OctTree> loadIndices(){
        Vector<OctTree> result = new Vector<>();
        for(String e : vecIndexList){
            try {
                FileInputStream fileIn = new FileInputStream("src/main/resources/data/" + e + ".class");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                result.add((OctTree) in.readObject());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return result;
    }

    public void insert(String strClusteringKey, Hashtable<String,Object> htblColNameValue) throws InvalidInputException {
        Vector<OctTree> indices = loadIndices(); // loading all indices to insert the record in them
        inserting = true; // to indicate that the table is currently inserting a value
        boolean shift = false; // boolean variable to indicate whether we need to shift or not
        Page p; // preparing a Page variable
        /*
         * preparing a temp variable to change the min and max values of a page
         * if needed during the shifting process
         */
        Hashtable<String, Object> temp = null;
        for(PageDetails e : details){ //looping over the pageDetails vector of the table
            if(!shift){ // checking if no shift is needed
                int compareMin = compareWith(htblColNameValue.get(strClusteringKey),
                        e.getMinimumRecord().get(strClusteringKey)); // comparing the input record with the minimum record of the page
                int compareMax = compareWith(htblColNameValue.get(strClusteringKey),
                        e.getMaximumRecord().get(strClusteringKey)); // comparing the input record with the maximum record of the page
                if(compareMin < 0 && compareMax < 0){ // if input record is < the minimum and maximum records of the page
                    p = loadPage("src/main/resources/data/" + e.getPageName() + ".class"); //load the current page
                    assert p != null; // IntelliJ's precautionary measures against NullPointerException
                    p.setName(e.getPageName()); //set the current page name for later serialization
                    for(OctTree index : indices) // looping over all the indices on the table to insert the record
                        index.insert(strClusteringKey, e.getPageName(), htblColNameValue); // inserting the record in the index
                    if(e.isFull()){ // checking if the page is already full
                        shift = true; // setting the shift to true to start the shifting process
                        temp = p.getRecords().get(p.getRecords().size() - 1); // setting the temp variable to be the maximum record of the page to shift it to the next page
                        p.getRecords().remove(temp); // removing the last record from the page to maintain the maximum number of records per page
                        e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // resetting the maximum record of the page to be the current last record of that page
                    }
                    p.getRecords().insertElementAt(htblColNameValue, 0); // inserting the new record at the beginning of the vector since it is smaller than the minimum record
                    e.setMinimumRecord(htblColNameValue); // resetting the minimum record to be the input record since it is now the smallest
                    if(p.isFull()) // checking if the page was not full before insertion and became full after insertion
                        e.setFull(true); // if true then we indicate that the page is now full and any further insertions in that page require shifting
                    p.savePage(); // saving the page after the insertion process
                }else if(compareMin > 0 && compareMax < 0){ // checking if the input belongs to the current page
                    p = loadPage("src/main/resources/data/" + e.getPageName() + ".class"); //load the current page
                    assert p != null; // IntelliJ's precautionary measures against NullPointerException
                    p.setName(e.getPageName()); // setting the name of the page
                    int indexOfInsertion = binarySearch(strClusteringKey, htblColNameValue, p.getRecords()); // get the index of insertion using binary search
                    for(OctTree index : indices) // looping over all the indices on the table to insert the record
                        index.insert(strClusteringKey, e.getPageName(), htblColNameValue); // inserting the record in the index
                    if(e.isFull()){ // checking if the page is already full
                        shift = true; // setting the shift to true to start the shifting process
                        temp = p.getRecords().get(p.getRecords().size() - 1); // setting the temp variable to be the maximum record of the page to shift it to the next page
                        p.getRecords().remove(temp); // removing the last record from the page to maintain the maximum number of records per page
                    }
                    p.getRecords().insertElementAt(htblColNameValue, indexOfInsertion); // inserting the element in the page at the specified index
                    e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // resetting the maximum record of the page to be the current last record of that page
                    if(p.isFull()) // checking if the page was not full before insertion and became full after insertion
                        e.setFull(true); // if true then we indicate that the page is now full and any further insertions in that page require shifting
                    p.savePage(); // saving the page after the insertion process
                }else if(compareMin > 0 && compareMax > 0){ // checking if the input is greater than the maximum
                    if(!e.isFull()) { // if the page is not full
                        int indexOfNextPage = details.indexOf(e) + 1; // finding the index of the next page
                        if(!(indexOfNextPage >= details.size())){ // checking if the index of the next page is greater than the number of pages in the table, then the current page is the last page
                            p = loadPage("src/main/resources/data/" + e.getPageName() + ".class"); //load the current page
                            assert p != null; // IntelliJ's precautionary measures against NullPointerException
                            p.setName(e.getPageName()); // setting the name of the page
                            for(OctTree index : indices) // looping over all the indices on the table to insert the record
                                index.insert(strClusteringKey, e.getPageName(), htblColNameValue); // inserting the record in the index
                            p.getRecords().add(htblColNameValue); // inserting the input in the page
                            e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // updating the maximum since the input is greater than the maximum
                            if(p.isFull()) // checking if the page is full after the insertion process
                                e.setFull(true); // updating the status of the page after the insertion process makes the page full
                            p.savePage(); // saving the page after the insertion process
                            break; // exiting out of the loop
                        }else
                            indexOfNextPage--; // in case the current page is the last page.
                        int compareMin2 = compareWith(htblColNameValue.get(strClusteringKey), details.get(indexOfNextPage).getMinimumRecord().get(strClusteringKey)); // comparing the input with the minimum record of the next page
                        if(compareMin2 > 0){ // checking if the input is greater than the maximum of the current page but less than the minimum of the next page
                            p = loadPage("src/main/resources/data/" + e.getPageName() + ".class"); //load the current page
                            assert p != null; // IntelliJ's precautionary measures against NullPointerException
                            p.setName(e.getPageName()); // setting the name of the page
                            for(OctTree index : indices) // looping over all the indices on the table to insert the record
                                index.insert(strClusteringKey, e.getPageName(), htblColNameValue); // inserting the record in the index
                            p.getRecords().add(htblColNameValue); // inserting the input in the page
                            e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // updating the maximum since the input is greater than the maximum
                            if(p.isFull()) // checking if the page is full after the insertion process
                                e.setFull(true); // updating the status of the page after the insertion process makes the page full
                            p.savePage(); // saving the page after the insertion process
                            break; // exiting out of the loop
                        }// if it is not the case that the input is less than the minimum of the next page. We skip the iteration because the input is greater than the minimum of the next page
                    }
                }else if(compareMin == 0 || compareMax == 0) // checking if to see is the value already in the page as a min or max or not
                    throw new InvalidInputException(); // if true then we throw an exception to indicate that we were trying to insert duplicate values
            }else { // if shifting must be done.
                for(OctTree index : indices) // looping over all the indices on the table to insert the record
                    index.shiftByOnePage(strClusteringKey, temp);// shifting the temp variable's page reference in the index to the next page during the shifting process
                if(!e.isFull()){ // if the page in question is not full then the shifting stops here
                    p = loadPage("src/main/resources/data/" + e.getPageName() + ".class"); //load the current page
                    assert p != null; // IntelliJ's precautionary measures against NullPointerException
                    p.setName(e.getPageName()); // setting the name of the page
                    p.getRecords().insertElementAt(temp, 0); // inserting the maximum of the previous page as the minimum in the next page
                    e.setMinimumRecord(p.getRecords().get(0)); // updating the minimum of the current Page Detail in question
                    if(p.isFull()) // checking if the page is full after insertion
                        e.setFull(true); // updating the page's details to be true the next time we check if it is full
                    p.savePage(); // saving the page after the insertion process
                    temp = null; // setting the temp to null to check if the last page was full or not.
                    break; // exiting out of the loop immediately after
                }else { // if the page in question is also empty, same process with minor differences
                    p = loadPage("src/main/resources/data/" + e.getPageName() + ".class"); //load the current page
                    assert p != null; // IntelliJ's precautionary measures against NullPointerException
                    p.setName(e.getPageName()); // setting the name of the page
                    p.getRecords().insertElementAt(temp, 0); // inserting the maximum of the previous page as the minimum in the next page
                    e.setMinimumRecord(p.getRecords().get(0)); // updating the minimum of the current Page Detail in question
                    temp = p.getRecords().get(p.getRecords().size() - 1); // updating the temp to be the maximum of the current page in question to continue the shifting process
                    p.getRecords().remove(p.getRecords().size() - 1); // removing the old maximum in the page
                    e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // updating the detail of the page by setting the maximum to the current maximum in the page
                    p.savePage(); // saving the page after the insertion process
                }
            }
        }
        if(temp != null){ // if the temp is not null then the last page was also full, and we need to continue insertion
            numberOfPages++; // incrementing the number of pages by one
            p = new Page(tableName, numberOfPages); // creating the new page
            p.getRecords().add(temp); // inserting the maximum of the last page in the table as the minimum in the new page
            addNewPage(p); // adding the new page to the table's details and saving it
            for(OctTree index : indices) // looping over all the indices on the table to insert the record
                index.shiftByOnePage(strClusteringKey, temp);// shifting the temp variable's page reference in the index to the next page after leaving the loop since the element that will be added to a new page on its own will not be shifted in the loop
        }
        for(OctTree e : indices) // looping over all the indices
            e.saveIndex(); // saving the changes made to each index
        indices = null; // nulling out the indices vector
        p = null; // nulling out the page variable
        System.gc(); // restarting the garbage collector
        numberOfRecords++; // incrementing the number of records in the table after each successful insertion
    }

    private boolean toBeDeleted(Hashtable<String, Object> record, Hashtable<String, Object> values){
        for(String e : values.keySet())
            if(!record.get(e).equals(values.get(e)))
                return false;
        return true;
    }

    public void delete(String strClusteringKey, Hashtable<String,Object> htblColNameValue) throws InvalidInputException {
        Vector<OctTree> indices = loadIndices(); // loading all the indices on the table to delete the records with the values in question
        Page p; // creating a page variable
        if(htblColNameValue.containsKey(strClusteringKey)){ // having the clustering key means that we will delete only one record. So binary search.
            for(PageDetails e : details){ // looping over all the page details
                int compareMin = compareWith(htblColNameValue.get(strClusteringKey),
                        e.getMinimumRecord().get(strClusteringKey)); // comparing the input with the minimum record in the page
                int compareMax = compareWith(htblColNameValue.get(strClusteringKey),
                        e.getMaximumRecord().get(strClusteringKey)); // comparing the input with the maximum record in the page
                if(compareMin == 0){ // checking if the input record is equal to the minimum
                    p = loadPage("src/main/resources/data/" + e.getPageName() + ".class"); //load the current page
                    assert p != null; // IntelliJ's precautionary measures against NullPointerException
                    p.setName(e.getPageName()); // setting the name of the page
                    for(OctTree i : indices) // looping over all the indices on the table
                        i.delete(strClusteringKey, p.getRecords().get(0)); // deleting the record that satisfies the input values
                    p.getRecords().remove(0); // removing the minimum record
                    numberOfRecords--; // decrementing the number of records by one
                    if(p.isEmpty()){ // checking if the page is empty after deletion
                        File page = new File("src/main/resources/data/" + e.getPageName() + ".class"); // creating a file object to be able to delete the page
                        page.delete(); // deleting the page
                        details.remove(e); // removing the details of the page from the details vector
                        return; // exiting out of the method entirely.
                    }else{ // if the page is not empty
                        e.setMinimumRecord(p.getRecords().get(0)); // updating the minimum value
                        e.setFull(false); // if the page was already full before deletion then it is not full after deletion
                    }
                }else if(compareMax == 0){ // checking if the input record is equal to the maximum
                    p = loadPage("src/main/resources/data/" + e.getPageName() + ".class"); //load the current page
                    assert p != null; // IntelliJ's precautionary measures against NullPointerException
                    p.setName(e.getPageName()); // setting the name of the page
                    for(OctTree i : indices) // looping over all the indices on the table
                        i.delete(strClusteringKey, p.getRecords().get(p.getRecords().size() - 1)); // deleting the record that satisfies the input values
                    p.getRecords().remove(p.getRecords().size() - 1); // removing the maximum record
                    numberOfRecords--; // decrementing the number of records by one
                    if(p.isEmpty()){ // checking if the page is empty after deletion
                        File page = new File("src/main/resources/data/" + e.getPageName() + ".class"); // creating a file object to be able to delete the page
                        page.delete(); // deleting the page
                        details.remove(e); // removing the details of the page from the details vector
                        return; // exiting out of the method entirely.
                    }else{ // if the page is not empty
                        e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // updating the maximum value
                        e.setFull(false); // if the page was already full before deletion then it is not full after deletion
                    }
                }else if(compareMin > 0 && compareMax < 0){ // checking if the input record is within the page
                    p = loadPage("src/main/resources/data/" + e.getPageName() + ".class"); //load the current page
                    assert p != null; // IntelliJ's precautionary measures against NullPointerException
                    p.setName(e.getPageName()); // setting the name of the page
                    int indexOfDeletion = binarySearch(strClusteringKey, htblColNameValue, p.getRecords()); // finding the index of deletion using binary search
                    if(!htblColNameValue.get(strClusteringKey).equals(p.getRecords().get(indexOfDeletion).get(strClusteringKey))) // checking if the acquired record does indeed exist with the same value for the clustering key
                        throw new InvalidInputException("The input value for the clustering key does not exist"); // if not then we throw an exception and halt the whole program
                    for(OctTree i : indices) // looping over all the indices on the table
                        i.delete(strClusteringKey, p.getRecords().get(indexOfDeletion)); // deleting the record that satisfies the input values
                    p.getRecords().remove(indexOfDeletion); // otherwise we simply remove the record
                    numberOfRecords--; // we decrement the number of records in the table
                    e.setFull(false); // if the page was already full before deletion then it is not full after deletion
                    /*The reason why we did not check if the page is empty or not is because of the above two conditions.
                    * If the page does have a maximum and a minimum and the record in question is neither,
                    * then the page has more than two records, and since we are deleting only one record,
                    * we do not need to check if the page will be empty or not as it will never be empty in this condition
                    */
                }
            }
        }else { // if we do not have the clustering key, we have to search Linearly XD
            Vector<PageDetails> pagesToDelete = new Vector<>(); // since we cannot access the internal counter of the for each loop, we create this vector to store the pages that will be deleted later once they get empty
            for(PageDetails e : details){ // looping over all the page details
                p = loadPage("src/main/resources/data/" + e.getPageName() + ".class"); //load the current page
                assert p != null; // IntelliJ's precautionary measures against NullPointerException
                p.setName(e.getPageName()); // setting the name of the page
                for(int i = 0; i < p.getRecords().size(); i++){ // looping over all the records in the page
                    if(toBeDeleted(p.getRecords().get(i), htblColNameValue)){ // checking if the current record is one of the records in question
                        for(OctTree s : indices) // looping over all the indices on the table
                            s.delete(strClusteringKey, p.getRecords().get(i)); // deleting the record that satisfies the input values
                        p.getRecords().remove(i--); // removing the records and decrementing the counter by one to accommodate the condition that two records that should be deleted are consecutive
                        numberOfRecords--; // decrementing the number of records in the table each time a record is deleted happens
                        e.setFull(false); // making sure that the page is empty after every delete.
                    }
                }
                if(p.isEmpty()) // checking to see if the page becomes empty after serial deletion
                    pagesToDelete.add(e); // adding the page to the vector in case the page is found to be empty
            }
            p = null;
            System.gc();
            for(PageDetails e : pagesToDelete){ // looping over the vector to delete the pages that need to be deleted
                File page = new File("src/main/resources/data/" + e.getPageName() + ".class"); // creating a file variable that targets the page in question
                page.delete(); // deleting the page
                details.remove(e); // removing the page from the details vector of the table
            }
        }
        for(OctTree e : indices) // looping over all the indices
            e.saveIndex(); // saving the changes made to each index
    }

    public void update(String strClusteringKey, Object objClusteringKeyValue,
                       Hashtable<String,Object> htblColNameValue) throws DBAppException {
        Vector<OctTree> indices = loadIndices(); // loading all the indices that are on the table to update the location of the record of interest
        Page p; // preparing a temporary page
        for(PageDetails e : details){ // looping over the details of the page to compare the min and max values of the clustering key
            int compareMin = compareWith(objClusteringKeyValue,
                    e.getMinimumRecord().get(strClusteringKey)); // comparing with the minimum in the current page
            int compareMax = compareWith(objClusteringKeyValue,
                    e.getMaximumRecord().get(strClusteringKey)); // comparing with the maximum of the current page
            if(compareMin >= 0 && compareMax <= 0){ // checking if the record in question is in the current page
                p = loadPage("src/main/resources/data/" + e.getPageName() + ".class"); // loading the current page
                assert p != null;
                p.setName(e.getPageName()); // setting the current page name to be able to save later
                for(OctTree index : indices) // looping over all the indices on the table
                    index.delete(strClusteringKey, p.getRecords().get(0)); // deleting the record we want to update to reinsert it later using the new values for the columns
                if(compareMin == 0){ // checking if the record in question is the minimum record (The first record)
                    for(String s : p.getRecords().get(0).keySet()){ // looping over all the keys in the record if the condition is true
                        if(!s.equals(strClusteringKey)) // checking that the current key is not the clustering key
                            p.getRecords().get(0).put(s, htblColNameValue.get(s)); // updating the content of the record
                    }
                    e.setMinimumRecord(p.getRecords().get(0)); // updating the minimum record of the details of the page to be able to save
                    for(OctTree index : indices) // looping over all the indices on the table
                        index.insert(strClusteringKey, e.getPageName(), p.getRecords().get(0)); // reinserting the record with its new values
                    p.savePage(); // saving the current page
                    return; // exiting out of the method entirely.
                }else if(compareMax == 0){ // checking if the record in question is the maximum record
                    for(OctTree index : indices) // looping over all the indices on the table
                        index.delete(strClusteringKey, p.getRecords().get(p.getRecords().size() - 1)); // deleting the record we want to update to reinsert it later using the new values for the columns
                    for(String s : p.getRecords().get(p.getRecords().size() - 1).keySet()){ // looping over all the keys in the record if the condition is true
                        if(!s.equals(strClusteringKey)) // checking that the current key is not the clustering key
                            p.getRecords().get(p.getRecords().size() - 1).put(s, htblColNameValue.get(s)); // updating the content of the record
                    }
                    e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // updating the maximum record of the details of the page to be able to save
                    for(OctTree index : indices) // looping over all the indices on the table
                        index.insert(strClusteringKey, e.getPageName(), p.getRecords().get(p.getRecords().size() - 1)); // reinserting the record with its new values
                    p.savePage(); // saving the page
                    return; // exiting out of the method entirely
                }else { // if it is somewhere in the middle of the page
                    Hashtable<String, Object> temp = new Hashtable<>(); // creating a temp hashtable for the binary search operation
                    temp.put(strClusteringKey, objClusteringKeyValue); // adding the clustering key and its value to the temp
                    int index = binarySearch(strClusteringKey, temp, p.getRecords()); // performing binary search on the page to find the record in question
                    if(compareWith(p.getRecords().get(index).get(strClusteringKey), objClusteringKeyValue) != 0) // checking if the record in question is present in the table
                        throw new InvalidInputException("Input value for clustering key could not be found"); // halting the program and throwing an exception if the record in question is not present in the table if the condition is true
                    for(OctTree s : indices) // looping over all the indices on the table
                        s.delete(strClusteringKey, p.getRecords().get(index)); // deleting the record we want to update to reinsert it later using the new values for the columns
                    for(String s : p.getRecords().get(index).keySet()){ // looping over all the keys in the record if the condition is true
                        if(!s.equals(strClusteringKey)) // checking that the current key is not the clustering key
                            p.getRecords().get(index).put(s, htblColNameValue.get(s)); // updating the content of the record
                    }
                    for(OctTree s : indices) // looping over all the indices on the table
                        s.insert(strClusteringKey, e.getPageName(), p.getRecords().get(index)); // reinserting the record with its new values
                    p.savePage(); // saving the page
                    return; // exiting out of the method entirely
                }
            }
        }
        for(OctTree e : indices) // looping over all the indices
            e.saveIndex(); // saving the changes made to each index
        indices = null; // nulling out the indices vector
        p = null; // nulling out the page variable
        System.gc(); // starting the garbage collector
    }

    private boolean checkCondition(SQLTerm term, Hashtable<String, Object> htblColNameValues){
        return switch (term._strOperator) {
            case "=" -> compareWith(htblColNameValues.get(term._strColumnName), term._objValue) == 0;
            case "!=" -> compareWith(htblColNameValues.get(term._strColumnName), term._objValue) != 0;
            case "<" -> compareWith(htblColNameValues.get(term._strColumnName), term._objValue) < 0;
            case "<=" -> compareWith(htblColNameValues.get(term._strColumnName), term._objValue) <= 0;
            case ">" -> compareWith(htblColNameValues.get(term._strColumnName), term._objValue) > 0;
            case ">=" -> compareWith(htblColNameValues.get(term._strColumnName), term._objValue) >= 0;
            default -> false;
        };
    }

    private boolean evaluate(Hashtable<String, Object> htblColNameValues, SQLTerm[] arrSQLTerms, String[] strarrOperators){
        boolean result = checkCondition(arrSQLTerms[0], htblColNameValues);
        for(int i = 0; i < strarrOperators.length; i++){
            if(strarrOperators[i].equalsIgnoreCase("AND"))
                result = result && checkCondition(arrSQLTerms[i + 1], htblColNameValues);
            else if(strarrOperators[i].equalsIgnoreCase("OR"))
                result = result || checkCondition(arrSQLTerms[i + 1], htblColNameValues);
            else if(strarrOperators[i].equalsIgnoreCase("XOR"))
                result = result ^ checkCondition(arrSQLTerms[i + 1], htblColNameValues);
        }
        return result;
    }

    public boolean allADDED(String[] strarrOperators){
        for(String e : strarrOperators)
            if(!e.equalsIgnoreCase("AND"))
                return false;
        return true;
    }

    public String getIndex(SQLTerm[] arrSQLTerm){
        Vector<String> temp = new Vector();
        for(SQLTerm term : arrSQLTerm){
            temp.add(term._strColumnName);
        }
        for (String s : vecIndexList) {
            String[] info = s.split("x");
            if (temp.contains(info[0]) && temp.contains(info[1]) && temp.contains(info[2]))
                return s;
        }
        return null;
    }

    public Iterator select(String strClusteringKey, SQLTerm[] arrSQLTerms, String[] strarrOperators) throws InvalidInputException {
        Vector<OctTree> index = loadIndices();
        String indexName = getIndex(arrSQLTerms);
        Vector<Hashtable<String, Object>> result = new Vector<>();
        if(allADDED(strarrOperators) && indexName != null){
            OctTree e = null;
            for(OctTree x : index){
                if(x.getStrIndexName().equals(indexName)){
                    e = x;
                    break;
                }
            }
            assert e != null;
            String[] indexColumns = indexName.split("x");
            SQLTerm[] selectTermsArray = new SQLTerm[3];
            for(SQLTerm term : arrSQLTerms){
                if(term._strColumnName.equals(indexColumns[1]))
                    selectTermsArray[0] = term;
                else if(term._strColumnName.equals(indexColumns[2]))
                    selectTermsArray[1] = term;
                else if(term._strColumnName.equals(indexColumns[3]))
                    selectTermsArray[2] = term;
            }
            Vector<Object[]> list = e.select(strClusteringKey, selectTermsArray);
            for(Object[] entry : list){
                Page p = loadPage("src/main/resources/data/" + entry[0].toString() + ".class");
                assert p != null;
                Hashtable<String, Object> temp = new Hashtable<>();
                temp.put(strClusteringKey, entry[1]);
                int indexOfSelect = binarySearch(strClusteringKey, temp, p.getRecords());
                result.add(p.getRecords().get(indexOfSelect));
            }
            return result.iterator();
        }
        for(PageDetails e : details){
            Page p = loadPage("src/main/resources/data/" + e.getPageName() + ".class");
            assert p != null;
            for(Hashtable<String, Object> s : p.getRecords()){
                if(evaluate(s, arrSQLTerms, strarrOperators))
                    result.add(s);
            }
        }
        return result.iterator();
    }

    public String toString(){
        StringBuilder result = new StringBuilder(tableName + ":\n");
        for(PageDetails e : details){
            Page p = loadPage("src/main/resources/data/" + e.getPageName() + ".class");
            assert p != null;
            p.setId(e.getPageID());
            result.append(p).append("\n");
        }
        return result.toString();
    }
}
