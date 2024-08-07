package main.java.exceptions;

public class TableDoesNotExistException extends DBAppException {

    public TableDoesNotExistException(){
        super();
    }

    public TableDoesNotExistException(String message){
        super(message);
    }

}
