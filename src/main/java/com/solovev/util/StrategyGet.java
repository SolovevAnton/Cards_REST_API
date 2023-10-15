package com.solovev.util;

import java.util.Map;
import java.util.Optional;
@FunctionalInterface
public interface StrategyGet<T> {
    Optional<T> getObject(Map<String,String[]> parametersMap);
}
