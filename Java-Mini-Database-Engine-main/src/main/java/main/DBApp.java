package main.java.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.opencsv.CSVWriter;

import java.io.*;
import main.java.exceptions.*;
import main.java.index.OctTree;
import main.java.miscellaneous.Null;

public class DBApp {

    public void init() {
        createMetadata();
    }
    //creating directory
    public void createMetadata() {
        File temp = new File("src/main/resources/metadata.csv");
        try {
            if(!temp.createNewFile())
                System.out.println("File Already Exists");
            else{
                try {
                    FileWriter outputFile = new FileWriter(temp);
                    CSVWriter writer = new CSVWriter(outputFile);
                    String[] header = {"Table Name", "Column Name", "Column Type", "ClusteringKey", "IndexName",
                            "IndexType", "min", "max"};
                    writer.writeNext(header);
                    writer.close();
                }catch(Exception ignored) {
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void createTable(String strTableName, String strClusteringKeyColumn,
                            Hashtable<String, String> htblColNameType,
                            Hashtable<String, String> htblColNameMin,
                            Hashtable<String, String> htblColNameMax) throws DBAppException {
        for(String e : htblColNameType.values()) {
            if(!e.equals("java.lang.Integer") && !e.equals("java.lang.Double") &&
                    !e.equals("java.util.Date") && !e.equals("java.lang.String")) {
                throw new TypeNotSupportedException(e + " is not a supported type.");
            }
        }

        File temp = new File("src/main/resources/metadata.csv");
        try {

            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/metadata.csv"));
            List<String[]> currentMetadataStringList = new ArrayList<>();
            String s = br.readLine();
            while(s != null) {
                currentMetadataStringList.add(s.split(","));
                if(s.contains(strTableName)) {
                    br.close();
                    throw new TableAlreadyExistsException("Cannot have two tables with the same name.");
                }
                s = br.readLine();
            }
            br.close();
            FileWriter outputFile = new FileWriter(temp);
            CSVWriter writer = new CSVWriter(outputFile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            writer.writeAll(currentMetadataStringList);
            String[] header = {strTableName, "", "", "", "", "", "", ""};
            for(Map.Entry<String, String> e : htblColNameType.entrySet()) {
                header[1] = e.getKey();
                header[2] = e.getValue();
                if(e.getKey().equals(strClusteringKeyColumn))
                    header[3] = "True";
                else
                    header[3] = "False";
                header[4] = header[5] = "null";
                header[6] = htblColNameMin.get(e.getKey());
                header[7] = htblColNameMax.get(e.getKey());
                writer.writeNext(header);
            }
            writer.close();
            Table table = new Table(strTableName);
            table.saveTable();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createIndex(String strTableName, String[] strarrColName) throws DBAppException {
        if(strarrColName.length != 3)
            throw new InvalidInputException("Octree Index must be performed on exactly three indices only");
        try {
            Vector<String> vecCols = new Vector<>();
            Hashtable<String, Double> htblMinMax = new Hashtable<>();
            String strClusteringKey = "";
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/metadata.csv"));
            String s = br.readLine();
            while(s != null){
                String[] header = s.split(",");
                if(header[0].equals(strTableName)){
                    if(header[3].equalsIgnoreCase("True"))
                        strClusteringKey = header[1];
                    vecCols.add(header[1]);
                    if(header[1].equals(strarrColName[0])){
                        switch (header[2]) {
                            case "java.lang.Integer" -> {
                                htblMinMax.put("MinX", OctTree.enumerateObjects(Integer.parseInt(header[6])));
                                htblMinMax.put("MaxX", OctTree.enumerateObjects(Integer.parseInt(header[7])));
                            }
                            case "java.lang.Double" -> {
                                htblMinMax.put("MinX", OctTree.enumerateObjects(Double.parseDouble(header[6])));
                                htblMinMax.put("MaxX", OctTree.enumerateObjects(Double.parseDouble(header[7])));
                            }
                            case "java.lang.String" -> {
                                htblMinMax.put("MinX", OctTree.enumerateObjects(header[6]));
                                htblMinMax.put("MaxX", OctTree.enumerateObjects(header[7]));
                            }
                            case "java.util.Date" -> {
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                htblMinMax.put("MinX", OctTree.enumerateObjects(formatter.parse(header[6])));
                                htblMinMax.put("MaxX", OctTree.enumerateObjects(formatter.parse(header[7])));
                            }
                        }
                    }else if(header[1].equals(strarrColName[1])){
                        switch (header[2]) {
                            case "java.lang.Integer" -> {
                                htblMinMax.put("MinY", OctTree.enumerateObjects(Integer.parseInt(header[6])));
                                htblMinMax.put("MaxY", OctTree.enumerateObjects(Integer.parseInt(header[7])));
                            }
                            case "java.lang.Double" -> {
                                htblMinMax.put("MinY", OctTree.enumerateObjects(Double.parseDouble(header[6])));
                                htblMinMax.put("MaxY", OctTree.enumerateObjects(Double.parseDouble(header[7])));
                            }
                            case "java.lang.String" -> {
                                htblMinMax.put("MinY", OctTree.enumerateObjects(header[6]));
                                htblMinMax.put("MaxY", OctTree.enumerateObjects(header[7]));
                            }
                            case "java.util.Date" -> {
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                htblMinMax.put("MinY", OctTree.enumerateObjects(formatter.parse(header[6])));
                                htblMinMax.put("MaxY", OctTree.enumerateObjects(formatter.parse(header[7])));
                            }
                        }
                    }else if(header[1].equals(strarrColName[2])){
                        switch (header[2]) {
                            case "java.lang.Integer" -> {
                                htblMinMax.put("MinZ", OctTree.enumerateObjects(Integer.parseInt(header[6])));
                                htblMinMax.put("MaxZ", OctTree.enumerateObjects(Integer.parseInt(header[7])));
                            }
                            case "java.lang.Double" -> {
                                htblMinMax.put("MinZ", OctTree.enumerateObjects(Double.parseDouble(header[6])));
                                htblMinMax.put("MaxZ", OctTree.enumerateObjects(Double.parseDouble(header[7])));
                            }
                            case "java.lang.String" -> {
                                htblMinMax.put("MinZ", OctTree.enumerateObjects(header[6]));
                                htblMinMax.put("MaxZ", OctTree.enumerateObjects(header[7]));
                            }
                            case "java.util.Date" -> {
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                htblMinMax.put("MinZ", OctTree.enumerateObjects(formatter.parse(header[6])));
                                htblMinMax.put("MaxZ", OctTree.enumerateObjects(formatter.parse(header[7])));
                            }
                        }
                    }
                }
                s = br.readLine();
            }
            if(!vecCols.contains(strarrColName[0]) || !vecCols.contains(strarrColName[1]) || !vecCols.contains(strarrColName[2]))
                throw new InvalidInputException("Columns entered do not exits in the table");

            String indexName = strTableName + "x" + strarrColName[0] + "x" + strarrColName[1] + "x" + strarrColName[2];

            FileInputStream fileIn = new FileInputStream("src/main/resources/data/" + strTableName + ".class");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Table table = (Table) in.readObject();
            table.createIndex(strClusteringKey, indexName, htblMinMax);
            table.saveTable();
            fileIn.close();
            in.close();

            File metadata = new File("src/main/resources/data/metadata.csv");
            br = new BufferedReader(new FileReader("src/main/resources/metadata.csv"));
            List<String[]> currentMetadataStringList = new Vector<>();
            String row = br.readLine();
            while(row != null) {
                String[] head = row.split(",");
                if(head[1].equals(strarrColName[0]) || head[1].equals(strarrColName[1]) || head[1].equals(strarrColName[2])){
                    head[4] = indexName;
                    head[5] = "Octree";
                }
                currentMetadataStringList.add(head);
                row = br.readLine();
            }
            FileWriter outputFile = new FileWriter(metadata);
            CSVWriter writer = new CSVWriter(outputFile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            writer.writeAll(currentMetadataStringList);
            writer.close();
            br.close();
        } catch (IOException | ParseException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
        }
    }

    private void checkMinMaxInput(Hashtable<String, Object> htblColNameValue,
                                  Hashtable<String, String> htblColNameMin,
                                  Hashtable<String, String> htblColNameMax) throws DBAppException {
        for(String e : htblColNameValue.keySet()){
            if(htblColNameValue.get(e) instanceof Integer &&
                    (((Integer)htblColNameValue.get(e)).compareTo(Integer.parseInt(htblColNameMin.get(e))) < 0
            || ((Integer)htblColNameValue.get(e)).compareTo(Integer.parseInt(htblColNameMax.get(e))) > 0)){
                throw new InvalidInputException("The input value is less than the minimum or greater than the maximum");
            }else if(htblColNameValue.get(e) instanceof Double &&
                    (((Double)htblColNameValue.get(e)).compareTo(Double.parseDouble(htblColNameMin.get(e))) < 0
                    || ((Double)htblColNameValue.get(e)).compareTo(Double.parseDouble(htblColNameMax.get(e))) > 0)){
                throw new InvalidInputException("The input value is less than the minimum or greater than the maximum");
            }else if(htblColNameValue.get(e) instanceof String &&
                    (((String)htblColNameValue.get(e)).compareTo(htblColNameMin.get(e)) < 0
                    || ((String)htblColNameValue.get(e)).compareTo(htblColNameMax.get(e)) > 0)){
                throw new InvalidInputException("The input value is less than the minimum or greater than the maximum");
            }else if(htblColNameValue.get(e) instanceof Date){
                try{
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date inputDate = (Date) htblColNameValue.get(e);
                    Date minDate = formatter.parse(htblColNameMin.get(e));
                    Date maxDate = formatter.parse(htblColNameMax.get(e));
                    if(inputDate.compareTo(minDate) < 0 || inputDate.compareTo(maxDate) > 0)
                        throw new InvalidInputException();
                }catch(Exception e1){
                    throw new InvalidInputException("Date not entered correctly");
                }
            }
        }
    }

    private Object[] getTableDetails(String strTableName,
                                     Hashtable<String, Object> htblColNameValue,
                                     String strClusteringKeyValue, boolean inserting) throws DBAppException, IOException {
        Object[] result = new Object[4];
        Hashtable<String, String> htblColNameType = new Hashtable<>();
        Hashtable<String, String> htblColNameMin = new Hashtable<>();
        Hashtable<String, String> htblColNameMax = new Hashtable<>();
        String strClusteringKey = "";
        Object objClusteringKeyValue = strClusteringKeyValue;
        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/metadata.csv"));
        String s = br.readLine();
        while(s != null){
            String[] header = s.split(",");
            if(header[0].equals(strTableName)){
                htblColNameType.put(header[1], header[2]);
                htblColNameMin.put(header[1], header[6]);
                htblColNameMax.put(header[1], header[7]);
                if(header[3].equals("True"))
                    strClusteringKey = header[1];
            }
            s = br.readLine();
        }
        if(htblColNameType.isEmpty())
            throw new TableDoesNotExistException(strTableName  + " table does not exist");
        else if(htblColNameValue.size() > htblColNameType.size())
            throw new InvalidInputException("The table of interest has " +
                    htblColNameType.size() + " Columns only.");
        else if(inserting && !htblColNameValue.containsKey(strClusteringKey))
            throw new InvalidInputException("The clustering key is not available as input.");
        else
            for(String e : htblColNameValue.keySet())
                if(!htblColNameType.containsKey(e))
                    throw new InvalidInputException("The column names entered do not match those of the table.");
        for(String e : htblColNameType.keySet()){
            if((htblColNameType.get(e).equals("java.lang.Integer") &&
                    !(htblColNameValue.get(e) instanceof Integer)) ||
                    (htblColNameType.get(e).equals("java.lang.Double") &&
                            !(htblColNameValue.get(e) instanceof Double)) ||
                    (htblColNameType.get(e).equals("java.lang.String") &&
                            !(htblColNameValue.get(e) instanceof String)) ||
                    (htblColNameType.get(e).equals("java.util.Date") &&
                            !(htblColNameValue.get(e) instanceof Date)))
                throw new TypeMissMatchException(e + " is of type " + htblColNameType.get(e));
            if(inserting && (htblColNameValue.size() < htblColNameType.size()))
                if(!htblColNameValue.containsKey(e))
                    htblColNameValue.put(e, new Null());
        }
        if(strClusteringKeyValue != null){
            try{
                if(htblColNameType.get(strClusteringKey).equals("java.lang.Integer"))
                    objClusteringKeyValue = Integer.parseInt(strClusteringKeyValue);
                else if(htblColNameType.get(strClusteringKey).equals("java.lang.Double"))
                    objClusteringKeyValue = Double.parseDouble(strClusteringKeyValue);
                else if(htblColNameType.get(strClusteringKey).equals("java.util.Date")){
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        objClusteringKeyValue = formatter.parse(strClusteringKeyValue);
                    } catch (ParseException e) {
                        throw new InvalidInputException();
                    }
                }
            }catch(NumberFormatException | InvalidInputException e){
                throw new InvalidInputException("Input data does not match the type of the clustering key");
            }
        }
        result[0] = strClusteringKey;
        result[1] = htblColNameMin;
        result[2] = htblColNameMax;
        result[3] = objClusteringKeyValue;
        return result;
    }

    public void insertIntoTable(String strTableName,
                                Hashtable<String, Object> htblColNameValue) throws DBAppException {
        try{
            Object[] parameters = getTableDetails(strTableName, htblColNameValue,
                    null, true);
            checkMinMaxInput(htblColNameValue, (Hashtable<String, String>) parameters[1],
                    (Hashtable<String, String>) parameters[2]);
            Table table;
            FileInputStream fileIn = new FileInputStream("src/main/resources/data/" + strTableName + ".class");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            table = (Table) in.readObject();
            fileIn.close();
            in.close();
            if(table.getNumberOfRecords() == 0 && table.getNumberOfPages() == 0){
                table.setNumberOfPages(1);
                table.setNumberOfRecords(1);
                Page page = new Page(table.getTableName(), table.getNumberOfPages());
                page.getRecords().add(htblColNameValue);
                table.addNewPage(page);
            }else{
                table.insert(parameters[0].toString(), htblColNameValue);
            }
            table.saveTable();
        }catch(IOException | ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    public void updateTable(String strTableName,
                            String strClusteringKeyValue,
                            Hashtable<String,Object> htblColNameValue) throws DBAppException {
        try{
            if(strClusteringKeyValue == null || strClusteringKeyValue.equals(""))
                throw new InvalidInputException("Clustering Key value should not be empty");
            Object[] parameters = getTableDetails(strTableName, htblColNameValue,
                    strClusteringKeyValue, false);
            checkMinMaxInput(htblColNameValue, (Hashtable<String, String>) parameters[1],
                    (Hashtable<String, String>) parameters[2]);
            if(htblColNameValue.containsKey(parameters[0].toString()))
                throw new InvalidInputException("Cannot update Clustering Key");
            Table table;
            FileInputStream fileIn = new FileInputStream("src/main/resources/data/" + strTableName + ".class");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            table = (Table) in.readObject();
            fileIn.close();
            in.close();
            table.update(parameters[0].toString(), parameters[3], htblColNameValue);
            table.saveTable();
        }catch(IOException | ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    public void deleteFromTable(String strTableName, Hashtable<String,Object> htblColNameValue) throws DBAppException {
        try{
            Object[] parameters = getTableDetails(strTableName, htblColNameValue,
                    null, false);
            checkMinMaxInput(htblColNameValue, (Hashtable<String, String>) parameters[1],
                    (Hashtable<String, String>) parameters[2]);
            Table table;
            FileInputStream fileIn = new FileInputStream("src/main/resources/data/" + strTableName + ".class");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            table = (Table) in.readObject();
            fileIn.close();
            in.close();
            if(htblColNameValue.isEmpty())
                table.truncateTable();
            else {
                table.delete(parameters[0].toString(), htblColNameValue);
            }
            table.saveTable();
        }catch (IOException | ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    public Iterator selectFromTable(SQLTerm[] arrSQLTerms, String[] strarrOperators) throws DBAppException {
        if(strarrOperators.length != arrSQLTerms.length - 1)
            throw new InvalidInputException("Size of Operators must be one less than the number of queried columns");

        try{
            String strClusteringKey = "";
            String strTableName = arrSQLTerms[0]._strTableName;
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/metadata.csv"));
            String s = br.readLine();
            while(s != null){
                String[] header = s.split(",");
                if(header[0].equals(strTableName)){

                    if(header[3].equals("True"))
                        strClusteringKey = header[1];
                }
                s = br.readLine();
            }
            Table table;
            FileInputStream fileIn = new FileInputStream("src/main/resources/data/" + strTableName + ".class");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            table = (Table) in.readObject();
            fileIn.close();
            in.close();
            return table.select(strClusteringKey, arrSQLTerms, strarrOperators);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void  insertCoursesRecords(DBApp dbApp, int limit) throws Exception {
        BufferedReader coursesTable = new BufferedReader(new FileReader("src/main/resources/courses_table.csv"));
        String record;
        Hashtable<String, Object> row = new Hashtable<>();
        int c = limit;
        if (limit == -1) {
            c = 1;
        }
        while ((record = coursesTable.readLine()) != null && c > 0) {
            String[] fields = record.split(",");


            int year = Integer.parseInt(fields[0].trim().substring(0, 4));
            int month = Integer.parseInt(fields[0].trim().substring(5, 7));
            int day = Integer.parseInt(fields[0].trim().substring(8));

            Date dateAdded = new Date(year - 1900, month - 1, day);

            row.put("date_added", dateAdded);

            row.put("course_id", fields[1]);
            row.put("course_name", fields[2]);
            row.put("hours", Integer.parseInt(fields[3]));

            dbApp.insertIntoTable("courses", row);
            row.clear();

            if (limit != -1) {
                c--;
            }
        }

        coursesTable.close();
    }

    private static void  insertStudentRecords(DBApp dbApp, int limit) throws Exception {
        BufferedReader studentsTable = new BufferedReader(new FileReader("src/main/resources/students_table.csv"));
        String record;
        int c = limit;
        if (limit == -1) {
            c = 1;
        }

        Hashtable<String, Object> row = new Hashtable<>();
        while ((record = studentsTable.readLine()) != null && c > 0) {
            String[] fields = record.split(",");

            row.put("id", fields[0]);
            row.put("first_name", fields[1]);
            row.put("last_name", fields[2]);

            int year = Integer.parseInt(fields[3].trim().substring(0, 4));
            int month = Integer.parseInt(fields[3].trim().substring(5, 7));
            int day = Integer.parseInt(fields[3].trim().substring(8));

            Date dob = new Date(year - 1900, month - 1, day);
            row.put("dob", dob);

            double gpa = Double.parseDouble(fields[4].trim());

            row.put("gpa", gpa);

            dbApp.insertIntoTable("students", row);
            row.clear();
            if (limit != -1) {
                c--;
            }
        }
        studentsTable.close();
    }
    private static void insertTranscriptsRecords(DBApp dbApp, int limit) throws Exception {
        BufferedReader transcriptsTable = new BufferedReader(new FileReader("src/main/resources/transcripts_table.csv"));
        String record;
        Hashtable<String, Object> row = new Hashtable<>();
        int c = limit;
        if (limit == -1) {
            c = 1;
        }
        while ((record = transcriptsTable.readLine()) != null && c > 0) {
            String[] fields = record.split(",");

            row.put("gpa", Double.parseDouble(fields[0].trim()));
            row.put("student_id", fields[1].trim());
            row.put("course_name", fields[2].trim());

            String date = fields[3].trim();
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8));

            Date dateUsed = new Date(year - 1900, month - 1, day);
            row.put("date_passed", dateUsed);

            dbApp.insertIntoTable("transcripts", row);
            row.clear();

            if (limit != -1) {
                c--;
            }
        }

        transcriptsTable.close();
    }
    private static void insertPCsRecords(DBApp dbApp, int limit) throws Exception {
        BufferedReader pcsTable = new BufferedReader(new FileReader("src/main/resources/pcs_table.csv"));
        String record;
        Hashtable<String, Object> row = new Hashtable<>();
        int c = limit;
        if (limit == -1) {
            c = 1;
        }
        while ((record = pcsTable.readLine()) != null && c > 0) {
            String[] fields = record.split(",");

            row.put("pc_id", Integer.parseInt(fields[0].trim()));
            row.put("student_id", fields[1].trim());

            dbApp.insertIntoTable("pcs", row);
            row.clear();

            if (limit != -1) {
                c--;
            }
        }

        pcsTable.close();
    }
    private static void createTranscriptsTable(DBApp dbApp) throws Exception {
        // Double CK
        String tableName = "transcripts";

        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("gpa", "java.lang.Double");
        htblColNameType.put("student_id", "java.lang.String");
        htblColNameType.put("course_name", "java.lang.String");
        htblColNameType.put("date_passed", "java.util.Date");

        Hashtable<String, String> minValues = new Hashtable<>();
        minValues.put("gpa", "0.7");
        minValues.put("student_id", "43-0000");
        minValues.put("course_name", "AAAAAA");
        minValues.put("date_passed", "1990-01-01");

        Hashtable<String, String> maxValues = new Hashtable<>();
        maxValues.put("gpa", "5.0");
        maxValues.put("student_id", "99-9999");
        maxValues.put("course_name", "zzzzzz");
        maxValues.put("date_passed", "2020-12-31");

        dbApp.createTable(tableName, "gpa", htblColNameType, minValues, maxValues);
    }

    private static void createStudentTable(DBApp dbApp) throws Exception {
        // String CK
        String tableName = "students";

        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.String");
        htblColNameType.put("first_name", "java.lang.String");
        htblColNameType.put("last_name", "java.lang.String");
        htblColNameType.put("dob", "java.util.Date");
        htblColNameType.put("gpa", "java.lang.Double");

        Hashtable<String, String> minValues = new Hashtable<>();
        minValues.put("id", "43-0000");
        minValues.put("first_name", "AAAAAA");
        minValues.put("last_name", "AAAAAA");
        minValues.put("dob", "1990-01-01");
        minValues.put("gpa", "0.7");

        Hashtable<String, String> maxValues = new Hashtable<>();
        maxValues.put("id", "99-9999");
        maxValues.put("first_name", "zzzzzz");
        maxValues.put("last_name", "zzzzzz");
        maxValues.put("dob", "2000-12-31");
        maxValues.put("gpa", "5.0");

        dbApp.createTable(tableName, "id", htblColNameType, minValues, maxValues);
    }
    private static void createPCsTable(DBApp dbApp) throws Exception {
        // Integer CK
        String tableName = "pcs";

        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("pc_id", "java.lang.Integer");
        htblColNameType.put("student_id", "java.lang.String");


        Hashtable<String, String> minValues = new Hashtable<>();
        minValues.put("pc_id", "0");
        minValues.put("student_id", "43-0000");

        Hashtable<String, String> maxValues = new Hashtable<>();
        maxValues.put("pc_id", "20000");
        maxValues.put("student_id", "99-9999");

        dbApp.createTable(tableName, "pc_id", htblColNameType, minValues, maxValues);
    }
    private static void createCoursesTable(DBApp dbApp) throws Exception {
        // Date CK
        String tableName = "courses";

        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("date_added", "java.util.Date");
        htblColNameType.put("course_id", "java.lang.String");
        htblColNameType.put("course_name", "java.lang.String");
        htblColNameType.put("hours", "java.lang.Integer");


        Hashtable<String, String> minValues = new Hashtable<>();
        minValues.put("date_added", "1901-01-01");
        minValues.put("course_id", "0000");
        minValues.put("course_name", "AAAAAA");
        minValues.put("hours", "1");

        Hashtable<String, String> maxValues = new Hashtable<>();
        maxValues.put("date_added", "2020-12-31");
        maxValues.put("course_id", "9999");
        maxValues.put("course_name", "zzzzzz");
        maxValues.put("hours", "24");

        dbApp.createTable(tableName, "date_added", htblColNameType, minValues, maxValues);

    }

    public static void main(String[] args) throws Exception {
        DBApp db = new DBApp();
        db.init();

	        SQLTerm[] arrSQLTerms;
	        arrSQLTerms = new SQLTerm[3];
//	        arrSQLTerms[0] = new SQLTerm();
//	        arrSQLTerms[0]._strTableName = "students";
//	        arrSQLTerms[0]._strColumnName= "first_name";
//	        arrSQLTerms[0]._strOperator = "=";
//	        arrSQLTerms[0]._objValue =row.get("first_name");

	        arrSQLTerms[0] = new SQLTerm();
	        arrSQLTerms[0]._strTableName = "students";
	        arrSQLTerms[0]._strColumnName= "gpa";
	        arrSQLTerms[0]._strOperator = ">";
	        arrSQLTerms[0]._objValue = 0.78;

        arrSQLTerms[1] = new SQLTerm();
        arrSQLTerms[1]._strTableName = "students";
        arrSQLTerms[1]._strColumnName= "id";
        arrSQLTerms[1]._strOperator = ">";
        arrSQLTerms[1]._objValue = "80-0000";

        arrSQLTerms[2] = new SQLTerm();
        arrSQLTerms[2]._strTableName = "students";
        arrSQLTerms[2]._strColumnName= "first_name";
        arrSQLTerms[2]._strOperator = "!=";
        arrSQLTerms[2]._objValue = "hWknCP";

            Iterator it = db.selectFromTable(arrSQLTerms, new String[]{"AND", "AND"});
            while (it.hasNext())
            {
                System.out.println(it.next());
            }
//
//	        String[]strarrOperators = new String[1];
//	        strarrOperators[0] = "OR";
//	      String table = "students";
//
//	        row.put("first_name", "fooooo");
//	        row.put("last_name", "baaaar");
//
//	        Date dob = new Date(1992 - 1900, 9 - 1, 8);
//	        row.put("dob", dob);
//	        row.put("gpa", 1.1);
//
//	        dbApp.updateTable(table, clusteringKey, row);
//        createCoursesTable(db);
//        createPCsTable(db);
//        createTranscriptsTable(db);
//        createStudentTable(db);
//        insertPCsRecords(db,200);
//        insertTranscriptsRecords(db,200);
//        insertStudentRecords(db,200);
//        insertCoursesRecords(db,200);

        Hashtable<String, Object> h = new Hashtable<>();
//        h.put("date_added", new Date(1999-1900, 8-1, 4));
//        h.put("hours", 16);
//        h.put("course_id", "0101");
//        h.put("course_name", "db");
//        db.deleteFromTable("courses", h);

//        db.createIndex("students", new String []{"id", "gpa", "first_name"});
//        db.createIndex("courses", new String[]{"date_added", "course_id", "hours"});
//        FileInputStream fileIn = new FileInputStream("src/main/resources/data/courses.class");
//        ObjectInputStream in = new ObjectInputStream(fileIn);
//        Table t = (Table) in.readObject();
//        System.out.println(t);

    }

}
