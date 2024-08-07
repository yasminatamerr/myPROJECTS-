package main.java.exceptions;

public class TypeMissMatchException extends DBAppException {

    public TypeMissMatchException(){
        super();
    }

    public TypeMissMatchException(String message){
        super(message);
    }

}
