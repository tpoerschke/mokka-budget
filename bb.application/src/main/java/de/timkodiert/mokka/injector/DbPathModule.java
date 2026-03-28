package de.timkodiert.mokka.injector;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;

import de.timkodiert.mokka.properties.PropertiesService;

@Module
public class DbPathModule {

    @Provides
    @Named("dbPath")
    String provideDbPath(PropertiesService propertiesService) {
        return propertiesService.getDbPath();
    }
}
