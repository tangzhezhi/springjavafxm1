package org.tang.springjavafxm1.model;

import javafx.beans.property.SimpleStringProperty;

public class PictureFileTableEntity implements Comparable<PictureFileTableEntity> {
    private SimpleStringProperty fileName;
    private SimpleStringProperty fileNo;
    private SimpleStringProperty humanName;

    public PictureFileTableEntity() {
    }

    public PictureFileTableEntity(String fileNo, String fileName,String humanName) {
        this.fileNo = new SimpleStringProperty(fileNo);
        this.fileName = new SimpleStringProperty(fileName);
        this.humanName = new SimpleStringProperty(humanName);

    }

    public String getFileName() {
        return fileName.get();
    }

    public SimpleStringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public String getFileNo() {
        return fileNo.get();
    }

    public SimpleStringProperty fileNoProperty() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo.set(fileNo);
    }

    public String getHumanName() {
        return humanName.get();
    }

    public SimpleStringProperty humanNameProperty() {
        return humanName;
    }

    public void setHumanName(String humanName) {
        this.humanName.set(humanName);
    }

    @Override
    public String toString() {
        return "PictureFileTableEntity{" +
                "fileName=" + fileName +
                ", fileNo=" + fileNo +
                ", humanName=" + humanName +
                '}';
    }

    @Override
    public int compareTo(PictureFileTableEntity o) {
        return 0;
    }
}
