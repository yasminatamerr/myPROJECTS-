package main.java.index;

import main.java.main.SQLTerm;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class OctTree implements Serializable {
    private final String strIndexName;
    private OctTreeNode root;
    private final Point3D minPoint, maxPoint;
    private static int maxEntriesInOctTreeNode;

    public OctTree(String strIndexName, Hashtable<String, Double>htblColNameRanges){
        this.strIndexName = strIndexName;
        try {
            FileReader reader = new FileReader("src/main/resources/DBApp.config");
            Properties p = new Properties();
            p.load(reader);
            maxEntriesInOctTreeNode = Integer.parseInt(p.getProperty("MaximumEntriesinOctreeNode"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        root = new OctTreeNode();
        minPoint = new Point3D(htblColNameRanges.get("MinX"), htblColNameRanges.get("MinY"),
                htblColNameRanges.get("MinZ"));
        maxPoint = new Point3D(htblColNameRanges.get("MaxX"), htblColNameRanges.get("MaxY"),
                htblColNameRanges.get("MaxZ"));
        root.setMinimum(minPoint);
        root.setMaximum(maxPoint);
    }

    public String getStrIndexName() {
        return strIndexName;
    }

    public Point3D getMinPoint() {
        return minPoint;
    }

    public Point3D getMaxPoint() {
        return maxPoint;
    }

    public static int getMaxEntriesInOctTreeNode(){
        return maxEntriesInOctTreeNode;
    }

    public void truncateIndex(){
        root = new OctTreeNode();
    }

    public void insert(String strClusteringKey, String pageName, Hashtable<String, Object> htblColNameValues){
        String[] info = strIndexName.split("x");
        double x = enumerateObjects(htblColNameValues.get(info[1]));
        double y = enumerateObjects(htblColNameValues.get(info[2]));
        double z = enumerateObjects(htblColNameValues.get(info[3]));
        Hashtable<String, Object> recordInfo = new Hashtable<>();
        System.out.println(info[1]);
        System.out.println(htblColNameValues.get(info[1]));
        recordInfo.put(info[1], htblColNameValues.get(info[1]));
        recordInfo.put(info[2], htblColNameValues.get(info[2]));
        recordInfo.put(info[3], htblColNameValues.get(info[3]));
        recordInfo.put(strClusteringKey, htblColNameValues.get(strClusteringKey));
        root.insert(pageName, new Point3D(x, y, z), recordInfo);
    }

    public void shiftByOnePage(String strClusteringKey, Hashtable<String, Object> htblColNameValues){
        String[] info = strIndexName.split("x");
        double x = enumerateObjects(htblColNameValues.get(info[1]));
        double y = enumerateObjects(htblColNameValues.get(info[2]));
        double z = enumerateObjects(htblColNameValues.get(info[3]));
        root.shiftByOnePage(strClusteringKey, new Point3D(x, y, z), htblColNameValues);
    }

    public void delete(String strClusteringKey, Hashtable<String, Object> htblColNameValues){
        String[] info = strIndexName.split("x");
        double x = enumerateObjects(htblColNameValues.get(info[1]));
        double y = enumerateObjects(htblColNameValues.get(info[2]));
        double z = enumerateObjects(htblColNameValues.get(info[3]));
        root.delete(strClusteringKey, new Point3D(x, y ,z), htblColNameValues);
    }

    public Vector<Object[]> select(String strClusteringKey, SQLTerm[] arrSQLTerms){
        Vector<Object[]> result = new Vector<>();
        double inputX = enumerateObjects(arrSQLTerms[0]._objValue);
        double inputY = enumerateObjects(arrSQLTerms[1]._objValue);
        double inputZ = enumerateObjects(arrSQLTerms[2]._objValue);
        double minX = 0.0, minY = 0.0, minZ = 0.0, maxX = 0.0, maxY = 0.0, maxZ = 0.0;
        switch (arrSQLTerms[0]._strOperator) {
            case "<", "<=" -> {
                minX = minPoint.x();
                maxX = inputX;
            }
            case ">", ">=" -> {
                minX = inputX;
                maxX = maxPoint.x();
            }
            case "=" -> minX = maxX = inputX;
            case "!=" -> {
                minX = minPoint.x();
                maxX = maxPoint.x();
            }
        }
        switch (arrSQLTerms[1]._strOperator) {
            case "<", "<=" -> {
                minY = minPoint.y();
                maxY = inputY;
            }
            case ">", ">=" -> {
                minY = inputY;
                maxY = maxPoint.y();
            }
            case "=" -> minY = maxY = inputY;
            case "!=" -> {
                minY = minPoint.y();
                maxY = maxPoint.y();
            }
        }
        switch (arrSQLTerms[2]._strOperator) {
            case "<", "<=" -> {
                minZ = minPoint.z();
                maxZ = inputZ;
            }
            case ">", ">=" -> {
                minZ = inputZ;
                maxZ = maxPoint.z();
            }
            case "=" -> minZ = maxZ = inputZ;
            case "!=" -> {
                minZ = minPoint.z();
                maxZ = maxPoint.z();
            }
        }
        root.select(strClusteringKey, new Point3D(minX, minY, minZ), new Point3D(maxX, maxY, maxZ), arrSQLTerms, result);
        return result;
    }

    public static double enumerateObjects(Object data){
        double result = 0.0;
        if(data instanceof Integer)
            result = ((Integer)data).doubleValue();
        else if(data instanceof Double)
            result = (Double) data;
        else if(data instanceof String temp){
            for(int i = 1; i <= temp.length(); i++){
                result += temp.charAt(i - 1) * i;
            }
        }else if(data instanceof Date temp){
            result += temp.getDate() + (temp.getMonth() + 1) * 2 + (temp.getYear() + 1900);
        }
        return result;
    }

    public void saveIndex(){
        try{
            FileOutputStream fileOut = new FileOutputStream("src/main/resources/data/" + strIndexName + ".class");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            fileOut.close();
            out.close();
        }catch(Exception ignored){
            //Ignore
        }
    }

}
