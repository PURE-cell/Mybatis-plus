package com.chen.mybatis_plus.common;

import java.io.Serializable;

public class Response<T> implements Serializable {
    private final static String SUCCESS_CODE = "200";
    private final static String SUCCESS_STATUS = "操作成功";
    private final static String ERROR_CODE = "200";

    private String code;
    private String status;
    private T resultMap;

    public Response(T result){
        this.setCode(SUCCESS_CODE);
        this.setStatus(SUCCESS_STATUS);
        this.setResultMap(result);
    }
    public Response(){
        this.setCode(SUCCESS_CODE);
        this.setStatus(SUCCESS_STATUS);
    }

    public Response(String errorMsg) {
        this.setCode(ERROR_CODE);
        this.setStatus(errorMsg);
    }

    public Response(String errorCode,String errorMsg) {
        this.setCode(errorCode);
        this.setStatus(errorMsg);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getResultMap() {
        return resultMap;
    }

    public void setResultMap(T resultMap) {
        this.resultMap = resultMap;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", status='" + status + '\'' +
                ", resultMap=" + resultMap +
                '}';
    }
}
