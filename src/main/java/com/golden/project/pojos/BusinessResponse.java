package com.golden.project.pojos;

import java.io.Serializable;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BusinessResponse implements Serializable
{
    private final String message;
    private final Map<String, String> businesses;
}
