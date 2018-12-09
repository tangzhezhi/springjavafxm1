package org.tang.springjavafxm1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tang.springjavafxm1.entity.Face;
import org.tang.springjavafxm1.mapper.FaceMapper;

import java.util.List;

@Service
public class FaceService {

    @Autowired
    private FaceMapper faceMapper;

    public int addFace(Face face){
        return   faceMapper.insert(face);
    }


    public List<Face> queryAllFace(){
        return faceMapper.selectAllFace();
    }

}
