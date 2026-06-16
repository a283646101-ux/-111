package com.zzyl.exception;

import com.zzyl.enums.BasicEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * BaseException
 * 
 * @author itheima
 **/
public class BaseException extends RuntimeException {

    private BasicEnum basicEnum;

    public BaseException(BasicEnum basicEnum) {
        this.basicEnum = basicEnum;
    }

    public BasicEnum getBasicEnum() {
        return basicEnum;
    }

    public void setBasicEnum(BasicEnum basicEnum) {
        this.basicEnum = basicEnum;
    }
}
