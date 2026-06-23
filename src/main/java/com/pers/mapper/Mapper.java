package com.pers.mapper;

public interface Mapper<F, T> {

    T toEntity(F object);

    default T map(F fromObject, T toObject) {
        return toObject;
    }

}
