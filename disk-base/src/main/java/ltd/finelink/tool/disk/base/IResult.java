package ltd.finelink.tool.disk.base;

import java.io.Serializable;

import lombok.Data;
import ltd.finelink.tool.disk.enums.ResultMessage;


@Data
public class IResult<T> implements Serializable {

    private static final long serialVersionUID = 1903256279680746393L;

    private int code;
    private T data;
    private String message; 

    public IResult() {
    }

    protected IResult(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message; 
    } 
    
    public static <T> IResult<T> ok() {
        return ok(null, ResultMessage.SUCCESS.getMessage());
    }

    public static <T> IResult<T> ok(T data) {
        return ok(data, null);
    }

    public static <T> IResult<T> ok(String message) {
        return ok(null, message);
    }

    public static <T> IResult<T> ok(T data, String message) {
        return new IResult<>(ResultMessage.SUCCESS.getCode(), data, message);
    }
    
    public static <T> IResult<T> fail(T data, ResultMessage result) {
        return new IResult<>(result.getCode(), data, result.getMessage());
    }
    
    public static <T> IResult<T> fail(ResultMessage result) {
        return fail(null, result);
    }
    
    public static <T> IResult<T> fail(String message) {
        return new IResult<>(ResultMessage.EXCEPTION.getCode(), null,message);
    }
    
    public static <T> IResult<T> fail(int code,String message) {
        return new IResult<>(code, null,message);
    }
    

    
    
   
}
