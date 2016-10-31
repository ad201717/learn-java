package com.howe.learn.common.request;

import com.howe.learn.common.RemoteRequest;

/**
 * @Author Karl
 * @Date 2016/10/28 10:11
 */
public class CalcRequest extends RemoteRequest{

    private CalMethod calMethod;

    private int x;

    private int y;

    public CalMethod getCalMethod() {
        return calMethod;
    }

    public void setCalMethod(CalMethod calMethod) {
        this.calMethod = calMethod;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public enum CalMethod {
        ADDITION('+'),
        SUBTRACTION('-'),
        MULTIPLICATION('*'),
        DIVISION('/');

        char value;
        CalMethod(char value){
            this.value = value;
        }

        public char getValue() {
            return value;
        }

        public static CalMethod from(char value){
            for(CalMethod method : CalMethod.values()){
                if(method.getValue() == value){
                    return method;
                }
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return x + String.valueOf(calMethod.getValue()) + y;
    }

}
