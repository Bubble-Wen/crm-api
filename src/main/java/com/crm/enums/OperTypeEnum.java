package com.crm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperTypeEnum {
    OTHER(0, "其它"),
    INSERT(1, "新增"),
    UPDATE(2, "修改"),
    DELETE(3, "删除");

    private final Integer value;
    private final String name;

    public static String getNameByValue(Integer value) {
        for (OperTypeEnum enums : values()) {
            if (enums.value.equals(value)) {
                return enums.name;
            }
        }
        return "";
    }
}

