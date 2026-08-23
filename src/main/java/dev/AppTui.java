package dev;

import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.tui.TuiConfig;
import dev.ui.MainView;

import java.time.Duration;


public class AppTui extends ToolkitApp {

    private final MainView view;
    private final TuiConfig config;

    public static TuiConfig defaultConfig() {
        return TuiConfig.builder()
                .mouseCapture(true)
                .pollTimeout(Duration.ofMillis(25))
                .tickRate(Duration.ofMillis(50))
                .build();
    }

    public AppTui() {
        this(AppTui.defaultConfig());
    }

    public AppTui(TuiConfig config) {
        this.config = config;
        this.view = new MainView(this::quit);
    }

    @Override
    protected Element render() {
        return view;
    }

    @Override
    protected TuiConfig configure() {
        return config;
    }
}
