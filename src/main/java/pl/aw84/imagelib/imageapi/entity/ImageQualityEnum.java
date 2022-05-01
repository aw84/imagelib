package pl.aw84.imagelib.imageapi.entity;

public enum ImageQualityEnum {

    original("original"),
    big("big"),
    small("small"),
    tiny("tiny");

    public final String code;

    ImageQualityEnum(String code) {
        this.code = code;
    }
    
}