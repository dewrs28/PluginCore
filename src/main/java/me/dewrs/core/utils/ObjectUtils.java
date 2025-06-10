package me.dewrs.core.utils;

public class ObjectUtils {
    public enum ObjectType{
        STRING, INTEGER, LONG, FLOAT, DOUBLE
    }

    public static ObjectType getObjectType(Object object){
        if(object instanceof String) return ObjectType.STRING;
        if(object instanceof Integer) return ObjectType.INTEGER;
        if(object instanceof Float) return ObjectType.FLOAT;
        if(object instanceof Long) return ObjectType.LONG;
        if(object instanceof Double) return ObjectType.DOUBLE;
        return null;
    }
}
