package main.java.exceptions;

abstract public class DBAppException extends Exception{
    public DBAppException(){
        super();
    }

    public DBAppException(String message){
        super(message);
    }
}
