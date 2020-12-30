package com.golden.project.pojos;

import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NeighborhoodResponse implements Serializable
{
    private final String message = "These neighborhoods all contain businesses with specified business locations";
    private final List<String> names;
}
