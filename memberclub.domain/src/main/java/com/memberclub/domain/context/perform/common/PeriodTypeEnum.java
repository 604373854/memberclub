/**
 * @(#)PeriodTypeEnum.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.perform.common;

/**
 * @author yuhaiqiang
 */
public enum PeriodTypeEnum {

    FIX_DAY(1, "fix_day"),
    NATURE_MONTY(2, "nature_month");

    private int value;

    private String name;

    PeriodTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PeriodTypeEnum findByCode(int value) throws IllegalArgumentException {
        for (PeriodTypeEnum item : PeriodTypeEnum.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid PeriodTypeEnum value: " + value);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int toInt() {
        return this.value;
    }
}
