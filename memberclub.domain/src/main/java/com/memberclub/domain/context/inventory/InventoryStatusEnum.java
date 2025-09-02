/**
 * @(#)InventoryStatusEnum.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.inventory;

/**
 * 库存状态枚举
 *
 * <p>author: wuyang</p>
 */
public enum InventoryStatusEnum {


    /** 上架中 */
    ACTIVTE(0, "上架中"),
    /** 已下架 */
    INACTIVE(999, "已下架"),
    ;

    private int code; // 状态码

    private String name; // 状态名称

    InventoryStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据编码查找库存状态
     *
     * @param code 状态码
     * @return 对应的状态
     * @throws IllegalArgumentException 当编码无效时抛出
     */
    public static InventoryStatusEnum findByCode(int code) throws IllegalArgumentException {
        // 遍历所有枚举并匹配编码
        for (InventoryStatusEnum item : InventoryStatusEnum.values()) {
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
