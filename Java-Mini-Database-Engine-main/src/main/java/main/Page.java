package main.java.main;

import java.io.*;
import java.util.*;
public class Page{

    private String name;
    private static int MAX_RECORDS_PER_PAGE;
    private Vector<Hashtable<String, Object>> records;
    private int id;

    //For creating a new page
    public Page(String name, int id) {
        FileReader reader = null;
        try {
            reader = new FileReader("src/main/resources/DBApp.config");
            Properties p = new Properties();
            p.load(reader);

            MAX_RECORDS_PER_PAGE = Integer.parseInt(p.getProperty("MaximumRowsCountinTablePage"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.id = id;
        this.name = name + "_" + id;
        records = new Vector<Hashtable<String, Object>>(MAX_RECORDS_PER_PAGE);
    }

    //For loading a preexisting page
    public Page(Vector<Hashtable<String, Object>> records){
        this.records = records;
        FileReader reader = null;
        try {
            reader = new FileReader("src/main/resources/DBApp.config");
            Properties p = new Properties();
            p.load(reader);

            MAX_RECORDS_PER_PAGE = Integer.parseInt(p.getProperty("MaximumRowsCountinTablePage"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public static int getMaxRecordsPerPage() {
        return MAX_RECORDS_PER_PAGE;
    }

    public Vector<Hashtable<String, Object>> getRecords(){
        return records;
    }

    public boolean isFull() {
        return records.size() >= MAX_RECORDS_PER_PAGE;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public void savePage() {
        try{
            FileOutputStream fileOut = new FileOutputStream("src/main/resources/data/" + name + ".class");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(records);
            fileOut.close();
            out.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public boolean isEmpty(){
        return records.isEmpty();
    }

    public String toString(){
        return "page " + id + ": " + records.toString();
    }
}
