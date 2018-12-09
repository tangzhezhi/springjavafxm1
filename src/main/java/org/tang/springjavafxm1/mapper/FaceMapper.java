package org.tang.springjavafxm1.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tang.springjavafxm1.entity.Face;

import java.util.List;

@Mapper
public interface FaceMapper {

    int insert(Face record);

    List<Face> selectAllFace();

}