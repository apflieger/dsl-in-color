package com.meetinclass.dslincolor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface NamedColor {
    String value();
}
