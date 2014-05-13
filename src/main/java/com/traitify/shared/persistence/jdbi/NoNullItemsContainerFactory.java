package com.traitify.shared.persistence.jdbi;

import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

import java.util.List;

public class NoNullItemsContainerFactory implements ContainerFactory<NoNullItemsContainer> {

    @Override
    public boolean accepts(Class<?> type) {
        return type.isAssignableFrom(List.class);
    }

    @Override
    public ContainerBuilder<NoNullItemsContainer> newContainerBuilderFor(Class<?> type) {
        return (ContainerBuilder)new NoNullItemsContainer();
    }
}
