package com.golden.project.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum ModeEnum
{
    OLDEST("oldest"),
    NEWEST("newest");

    private final String name;
    private static final Map<String, ModeEnum> nameLookup = new HashMap<>();

    static
    {
        EnumSet.allOf(ModeEnum.class).forEach(s -> nameLookup.put(s.getName(), s));
    }

    ModeEnum(String name)
    {
        this.name = name;
    }

    public static ModeEnum getInstanceByName(String name)
    {
        if (nameLookup.containsKey(name))
        {
            return nameLookup.get(name);
        }
        throw new IllegalArgumentException("Illegal instance name type.");
    }
}
