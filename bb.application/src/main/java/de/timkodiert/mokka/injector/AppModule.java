package de.timkodiert.mokka.injector;

import dagger.Module;
import dagger.Provides;
import javafx.application.HostServices;

@Module
public class AppModule {

    private final HostServices hostServices;

    private AppModule(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public static AppModule with(HostServices hostServices) {
        return new AppModule(hostServices);
    }

    @Provides
    public HostServices provideHostServices() {
        return hostServices;
    }
}
