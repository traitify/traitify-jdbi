package com.traitify.jdbi;

import org.skife.jdbi.v2.ContainerBuilder;

import java.util.ArrayList;
import java.util.List;

public class NoNullItemsContainer implements ContainerBuilder<List<?>> {

    private final ArrayList<Object> list = new ArrayList<>();

    @Override
    public NoNullItemsContainer add(Object it) {
        if(it != null){
            list.add(it);
        }

        return this;
    }

    @Override
    public List<?> build() {
        return list;
    }
}
