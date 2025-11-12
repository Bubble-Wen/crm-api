package com.crm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperPlatformEnum {
    PLATFORM_A(0, "A平台"),
    PLATFORM_B(1, "B平台");

    private final Integer value;
    private final String name;

    public static String getNameByValue(Integer value) {
        for (OperPlatformEnum enums : values()) {
            if (enums.value.equals(value)) {
                return enums.name;
            }
        }
        return "";
    }
}
