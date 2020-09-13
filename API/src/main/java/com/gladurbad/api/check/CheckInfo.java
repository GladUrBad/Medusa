package com.gladurbad.api.check;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CheckInfo {
    String name();
    String type();
    boolean dev() default false;
}