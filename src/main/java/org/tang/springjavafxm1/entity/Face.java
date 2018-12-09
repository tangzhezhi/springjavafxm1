package org.tang.springjavafxm1.entity;

import lombok.*;

@ToString
@Builder
@Setter
@Getter
@Data
public class Face {
    private int id;
    private String label;
    private String data;
}
