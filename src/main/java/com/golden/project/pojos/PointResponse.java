package com.golden.project.pojos;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.geo.Point;

@Getter
@Builder
public class PointResponse implements Serializable
{
    private final String message;
    private final Point center;
}
