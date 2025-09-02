package com.memberclub.starter.controller.vo.base;

import lombok.Data;

/**
 * 通用响应对象，包含基础的结果字段。
 */
@Data
public class BaseResponse {

    public boolean succ;

    public int errorCode;

    private String errorMsg;
}
