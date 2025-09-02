package com.memberclub.starter.controller.vo.base;

import lombok.Data;

/**
 * 通用的数据响应包装，携带实际数据及基础响应字段。
 */
@Data
public class DataResponse<T> extends BaseResponse {

    private T data;

    /**
     * 创建携带指定数据的成功响应。
     *
     * @param t   数据载荷
     * @param <T> 数据类型
     * @return 成功的数据响应
     */
    public static <T> DataResponse<T> success(T t) {
        DataResponse<T> response = new DataResponse<T>();
        response.setSucc(true);
        response.setData(t);
        return response;
    }
}
