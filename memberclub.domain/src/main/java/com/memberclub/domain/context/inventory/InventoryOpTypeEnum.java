/**
 * @(#)InventoryOpTypeEnum.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.inventory;

/**
 * 库存操作类型枚举
 *
 * <p>author: wuyang</p>
 */
public enum InventoryOpTypeEnum {

    /** 扣减库存 */
    DECREMENT(1, "扣减库存"),

    /** 回补库存 */
    ROLLBACK(2, "回补库存");

    private int code; // 操作码

    private String name; // 操作名称

    InventoryOpTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据编码查找操作类型
     *
     * @param code 操作码
     * @return 对应的操作类型
     * @throws IllegalArgumentException 当编码不存在时抛出
     */
    public static InventoryOpTypeEnum findByCode(int code) throws IllegalArgumentException {
        // 遍历所有枚举并匹配编码
        for (InventoryOpTypeEnum item : InventoryOpTypeEnum.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.code;
    }
}
