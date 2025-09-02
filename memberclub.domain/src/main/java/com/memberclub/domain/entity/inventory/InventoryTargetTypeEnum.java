/**
 * @(#)InventoryTargetTypeEnum.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.entity.inventory;

/**
 * 库存目标类型枚举
 *
 * <p>author: wuyang</p>
 */
public enum InventoryTargetTypeEnum {

    /** 商品库存 */
    SKU(1, "商品库存"),
    ;

    private int code; // 枚举码

    private String name; // 类型名称

    InventoryTargetTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据编码查找对应枚举
     *
     * @param code 枚举码
     * @return 匹配的枚举
     * @throws IllegalArgumentException 当找不到对应枚举时抛出
     */
    public static InventoryTargetTypeEnum findByCode(int code) throws IllegalArgumentException {
        // 遍历所有枚举并匹配编码
        for (InventoryTargetTypeEnum item : InventoryTargetTypeEnum.values()) {
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
