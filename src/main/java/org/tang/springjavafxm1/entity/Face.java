package org.tang.springjavafxm1.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
@Getter
@Data
public class Face {
    private int id;
    private String label;
    private String data;
    private String shape;
}
