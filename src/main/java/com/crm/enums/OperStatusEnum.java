package com.crm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperStatusEnum {
    NORMAL(0, "正常"),
    EXCEPTION(1, "异常");

    private final Integer value;
    private final String name;

    public static String getNameByValue(Integer value) {
        for (OperStatusEnum enums : values()) {
            if (enums.value.equals(value)) {
                return enums.name;
            }
        }
        return "";
    }
}
