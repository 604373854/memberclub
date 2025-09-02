package com.memberclub.starter.util;

import lombok.Data;

/**
 * 存放在 {@link SecurityUtil} 中的简单数据对象，用于表示当前已认证的用户。
 * 如有需要可拓展更多安全相关字段。
 */
@Data
public class SecurityInfo {

    /** 已认证用户的唯一标识 */
    public long userId;

}
