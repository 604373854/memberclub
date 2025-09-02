/**
 * @(#)ExceptionWrapper.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.exception;

/**
 * 用于统一包装异常的简单容器，便于向上层返回或统一记录日志。
 *
 * <p>该类保持轻量，若需要自定义异常处理，可扩展错误码、提示信息等字段。</p>
 *
 * @author 掘金五阳
 */
public class ExceptionWrapper {

    /**
     * 构造空包装对象，后续可通过继承方式扩展上下文信息。
     */
    public ExceptionWrapper() {
        // 保留默认实现
    }
}