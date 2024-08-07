package main.java.index;

import main.java.main.DBApp;
import main.java.main.SQLTerm;
import main.java.main.Table;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

public class OctTreeNode implements Serializable {
    private OctTreeEntry[] entries;
    private OctTreeNode[] children;
    private boolean full;
    private Point3D centerPoint, minimum, maximum;
    private int size;
    public OctTreeNode(){
        entries = new OctTreeEntry[OctTree.getMaxEntriesInOctTreeNode()];
    }

    /**
     * In case the node is full, and we need to insert a new values.
     * We split the node into 8 nodes that are not full and redistribute the entries accordingly.*/
    public void split(){
        children = new OctTreeNode[8];
        for(int i = 0; i < children.length; i++)
            children[i] = new OctTreeNode();
        centerPoint = new Point3D((minimum.x() + maximum.x()) / 2,
                (minimum.y() + maximum.y()) / 2,
                (minimum.z() + maximum.z()) / 2);

        //First Octant
        children[0].setMinimum(centerPoint);
        children[0].setMaximum(maximum);
        //Second Octant
        children[1].setMinimum(new Point3D(minimum.x(), centerPoint.y(), centerPoint.z()));
        children[1].setMaximum(new Point3D(centerPoint.x(), maximum.y(), maximum.z()));
        //Third Octant
        children[2].setMinimum(new Point3D(minimum.x(), minimum.y(), centerPoint.z()));
        children[2].setMaximum(new Point3D(centerPoint.x(), centerPoint.y(), maximum.z()));
        //Fourth Octant
        children[3].setMinimum(new Point3D(centerPoint.x(), minimum.y(), centerPoint.z()));
        children[3].setMaximum(new Point3D(maximum.x(), centerPoint.y(), maximum.z()));
        //Fifth Octant
        children[4].setMinimum(new Point3D(centerPoint.x(), centerPoint.y(), minimum.z()));
        children[4].setMaximum(new Point3D(maximum.x(), maximum.y(), centerPoint.z()));
        //Sixth Octant
        children[5].setMinimum(new Point3D(minimum.x(), centerPoint.y(), minimum.z()));
        children[5].setMaximum(new Point3D(centerPoint.x(), maximum.y(), centerPoint.z()));
        //Seventh Octant
        children[6].setMinimum(minimum);
        children[6].setMaximum(centerPoint);
        //Eighth Octant
        children[7].setMinimum(new Point3D(centerPoint.x(), minimum.y(), minimum.z()));
        children[7].setMaximum(new Point3D(maximum.x(), centerPoint.y(), centerPoint.z()));
        for(OctTreeEntry e : entries){
            if(e != null){
                distribute(e);
            }
        }
//        entries = null;
    }

    public void revert(){
        int i = 0;
        entries = new OctTreeEntry[OctTree.getMaxEntriesInOctTreeNode()];
        for(OctTreeEntry e : children[0].getEntries())
            if(e != null)
                entries[i++] = e;
        for(OctTreeEntry e : children[1].getEntries())
            if(e != null)
                entries[i++] = e;
        for(OctTreeEntry e : children[2].getEntries())
            if(e != null)
                entries[i++] = e;
        for(OctTreeEntry e : children[3].getEntries())
            if(e != null)
                entries[i++] = e;
        for(OctTreeEntry e : children[4].getEntries())
            if(e != null)
                entries[i++] = e;
        for(OctTreeEntry e : children[5].getEntries())
            if(e != null)
                entries[i++] = e;
        for(OctTreeEntry e : children[6].getEntries())
            if(e != null)
                entries[i++] = e;
        for(OctTreeEntry e : children[7].getEntries())
            if(e != null)
                entries[i++] = e;
        children = null;
        centerPoint = null;
    }

    public void distribute(OctTreeEntry entry){
        Point3D location = entry.getLocation();
        if(location.x() >= centerPoint.x()
                && location.y() >= centerPoint.y()
                && location.z() >= centerPoint.z()){
            //First Octant
            children[0].insert(entry.getPage(), entry.getLocation(), entry.getHtblColNameValue());
        }else if(location.x() < centerPoint.x()
                && location.y() > centerPoint.y()
                && location.z() > centerPoint.z()){
            //Second Octant
            children[1].insert(entry.getPage(), entry.getLocation(), entry.getHtblColNameValue());
        }else if(location.x() < centerPoint.x()
                && location.y() < centerPoint.y()
                && location.z() > centerPoint.z()){
            //Third Octant
            children[2].insert(entry.getPage(), entry.getLocation(), entry.getHtblColNameValue());
        }else if(location.x() > centerPoint.x()
                && location.y() < centerPoint.y()
                && location.z() > centerPoint.z()){
            //Fourth Octant
            children[3].insert(entry.getPage(), entry.getLocation(), entry.getHtblColNameValue());
        }else if(location.x() > centerPoint.x()
                && location.y() > centerPoint.y()
                && location.z() < centerPoint.z()){
            //Fifth Octant
            children[4].insert(entry.getPage(), entry.getLocation(), entry.getHtblColNameValue());
        }else if(location.x() < centerPoint.x()
                && location.y() > centerPoint.y()
                && location.z() < centerPoint.z()){
            //Sixth Octant
            children[5].insert(entry.getPage(), entry.getLocation(), entry.getHtblColNameValue());
        }else if(location.x() < centerPoint.x()
                && location.y() < centerPoint.y()
                && location.z() < centerPoint.z()){
            //Seventh Octant
            children[6].insert(entry.getPage(), entry.getLocation(), entry.getHtblColNameValue());
        }else if(location.x() > centerPoint.x()
                && location.y() < centerPoint.y()
                && location.z() < centerPoint.z()){
            //Eighth Octant
            children[7].insert(entry.getPage(), entry.getLocation(), entry.getHtblColNameValue());
        }
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public Point3D getCenterPoint() {
        return centerPoint;
    }

    public Point3D getMaximum() {
        return maximum;
    }

    public Point3D getMinimum(){
        return minimum;
    }

    public void setMinimum(Point3D minimum) {
        this.minimum = minimum;
    }

    public void setMaximum(Point3D maximum) {
        this.maximum = maximum;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public OctTreeEntry get(int index){
        if(index >= entries.length || index < 0)
            return null;
        return entries[index];
    }

    public OctTreeEntry[] getEntries(){
        return entries;
    }

    public void insert(String pageName, Point3D location, Hashtable<String, Object> htblColNameValue){
        //TODO Check if duplicate and add as overflow.
        if(!full){
            for(OctTreeEntry entry : entries){
                if(entry != null && entry.getLocation().equals(location)){
                    entry.insertOverflow(pageName, location, htblColNameValue);
                    return;
                }
            }
            entries[size++] = new OctTreeEntry(pageName, htblColNameValue, location);
            if(size >= entries.length)
                full = true;
        }
        else{
            this.split();
            OctTreeEntry entry = new OctTreeEntry(pageName, htblColNameValue, location);
            distribute(entry);
            size++;
//            entries = null;
        }
    }

    public void shiftByOnePage(String strClusteringKey, Point3D location,
                               Hashtable<String, Object> htblColNameValues){
        if(entries != null){
            for(OctTreeEntry entry : entries){
                if(entry.getLocation().equals(location) && entry.getHtblColNameValue().get(strClusteringKey).equals(htblColNameValues.get(strClusteringKey))){
                    String[] pageName = entry.getPage().split("x");
                    entry.setPage(pageName[0] + (Integer.parseInt(pageName[1]) + 1));
                    return;
                }else if(entry.getLocation().equals(location) && entry.isOverflown()){
                    entry.shiftByOnePage(strClusteringKey, htblColNameValues);
                    return;
                }
            }
        }else {
            if(location.x() >= centerPoint.x()
                    && location.y() >= centerPoint.y()
                    && location.z() >= centerPoint.z()){
                //First Octant
                children[0].shiftByOnePage(strClusteringKey, location, htblColNameValues);
            }else if(location.x() < centerPoint.x()
                    && location.y() > centerPoint.y()
                    && location.z() > centerPoint.z()){
                //Second Octant
                children[1].shiftByOnePage(strClusteringKey, location, htblColNameValues);
            }else if(location.x() < centerPoint.x()
                    && location.y() < centerPoint.y()
                    && location.z() > centerPoint.z()){
                //Third Octant
                children[2].shiftByOnePage(strClusteringKey, location, htblColNameValues);
            }else if(location.x() > centerPoint.x()
                    && location.y() < centerPoint.y()
                    && location.z() > centerPoint.z()){
                //Fourth Octant
                children[3].shiftByOnePage(strClusteringKey, location, htblColNameValues);
            }else if(location.x() > centerPoint.x()
                    && location.y() > centerPoint.y()
                    && location.z() < centerPoint.z()){
                //Fifth Octant
                children[4].shiftByOnePage(strClusteringKey, location, htblColNameValues);
            }else if(location.x() < centerPoint.x()
                    && location.y() > centerPoint.y()
                    && location.z() < centerPoint.z()){
                //Sixth Octant
                children[5].shiftByOnePage(strClusteringKey, location, htblColNameValues);
            }else if(location.x() < centerPoint.x()
                    && location.y() < centerPoint.y()
                    && location.z() < centerPoint.z()){
                //Seventh Octant
                children[6].shiftByOnePage(strClusteringKey, location, htblColNameValues);
            }else if(location.x() > centerPoint.x()
                    && location.y() < centerPoint.y()
                    && location.z() < centerPoint.z()){
                //Eighth Octant
                children[7].shiftByOnePage(strClusteringKey, location, htblColNameValues);
            }
        }
    }

    public void delete(String strClusteringKey, Point3D location, Hashtable<String, Object> htblColNameValues){
        if(entries != null){
            boolean shift = false;
            for(int i = 0; i < size; i++){
                if(entries[i].getLocation().equals(location)){ // if this is the record in question
                    if(entries[i].isOverflown()){ // if it has an overflow
                        if(htblColNameValues.get(strClusteringKey).equals(entries[i].getHtblColNameValue().get(strClusteringKey))){
                            entries[i] = entries[i].nextOverflow;
                        }else {
                            entries[i].delete(strClusteringKey, htblColNameValues);
                        }
                        if (entries[i].nextOverflow == null)
                            entries[i].setOverflown(false);
                        return;
                    }else {
                        shift = true;
                    }
                }
                if(shift){
                    if(!(i + 1 >= entries.length))
                        entries[i] = entries[i + 1];
                }
            }
            full = false;
        }else {
            if(location.x() >= centerPoint.x()
                    && location.y() >= centerPoint.y()
                    && location.z() >= centerPoint.z()){
                //First Octant
                children[0].delete(strClusteringKey, location, htblColNameValues);
            }else if(location.x() < centerPoint.x()
                    && location.y() > centerPoint.y()
                    && location.z() > centerPoint.z()){
                //Second Octant
                children[1].delete(strClusteringKey, location, htblColNameValues);
            }else if(location.x() < centerPoint.x()
                    && location.y() < centerPoint.y()
                    && location.z() > centerPoint.z()){
                //Third Octant
                children[2].delete(strClusteringKey, location, htblColNameValues);
            }else if(location.x() > centerPoint.x()
                    && location.y() < centerPoint.y()
                    && location.z() > centerPoint.z()){
                //Fourth Octant
                children[3].delete(strClusteringKey, location, htblColNameValues);
            }else if(location.x() > centerPoint.x()
                    && location.y() > centerPoint.y()
                    && location.z() < centerPoint.z()){
                //Fifth Octant
                children[4].delete(strClusteringKey, location, htblColNameValues);
            }else if(location.x() < centerPoint.x()
                    && location.y() > centerPoint.y()
                    && location.z() < centerPoint.z()){
                //Sixth Octant
                children[5].delete(strClusteringKey, location, htblColNameValues);
            }else if(location.x() < centerPoint.x()
                    && location.y() < centerPoint.y()
                    && location.z() < centerPoint.z()){
                //Seventh Octant
                children[6].delete(strClusteringKey, location, htblColNameValues);
            }else if(location.x() > centerPoint.x()
                    && location.y() < centerPoint.y()
                    && location.z() < centerPoint.z()){
                //Eighth Octant
                children[7].delete(strClusteringKey, location, htblColNameValues);
            }
        }
        size--;
        if(entries == null && size <= OctTree.getMaxEntriesInOctTreeNode()){
            this.revert();
        }
    }

    private static boolean checkCondition(SQLTerm term, Hashtable<String, Object> htblColNameValues){
        return switch (term._strOperator) {
            case "=" -> Table.compareWith(htblColNameValues.get(term._strColumnName), term._objValue) == 0;
            case "!=" -> Table.compareWith(htblColNameValues.get(term._strColumnName), term._objValue) != 0;
            case "<" -> Table.compareWith(htblColNameValues.get(term._strColumnName), term._objValue) < 0;
            case "<=" -> Table.compareWith(htblColNameValues.get(term._strColumnName), term._objValue) <= 0;
            case ">" -> Table.compareWith(htblColNameValues.get(term._strColumnName), term._objValue) > 0;
            case ">=" -> Table.compareWith(htblColNameValues.get(term._strColumnName), term._objValue) >= 0;
            default -> false;
        };
    }

    public static boolean evaluate(Hashtable<String, Object> htblColNameValues, SQLTerm[] arrSQLTerms){
        boolean result = true;
        for(SQLTerm term : arrSQLTerms){
            result = result && checkCondition(term, htblColNameValues);
        }
        return result;
    }

    public void select(String strClusteringKey, Point3D min, Point3D max, SQLTerm[] arrSQLTerms, Vector<Object[]> result){
        if(entries != null){
            for(OctTreeEntry entry : entries){
                if(entry.getLocation().x() > max.x() || entry.getLocation().y() > max.y() || entry.getLocation().z() > max.z()) continue;
                if(entry.getLocation().x() < min.x() || entry.getLocation().y() < min.y() || entry.getLocation().z() < min.z()) continue;
                if(evaluate(entry.getHtblColNameValue(), arrSQLTerms)){
                    Object[] res = {entry.getPage(), entry.getHtblColNameValue().get(strClusteringKey)};
                    result.add(res);
                    if(entry.isOverflown()){
                        entry.select(strClusteringKey, arrSQLTerms, result);
                    }
                }
            }
        }else {
            for(OctTreeNode octant : children){
                if(octant.getMaximum().x() < min.x() || octant.getMaximum().y() < min.y() || octant.getMaximum().z() < min.z()) continue;
                if(octant.getMinimum().x() > max.x() || octant.getMinimum().y() > max.y() || octant.getMinimum().z() > max.z()) continue;
                octant.select(strClusteringKey, min, max, arrSQLTerms, result);
            }
        }
    }
}
