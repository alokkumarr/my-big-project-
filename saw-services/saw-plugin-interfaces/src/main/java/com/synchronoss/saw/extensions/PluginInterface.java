package com.synchronoss.saw.extensions;

import org.pf4j.ExtensionPoint;

import java.util.List;

public interface PluginInterface extends ExtensionPoint {
    String identify();
    List<?> reactiveRoutes();
    List<Object> mvcControllers();
}
