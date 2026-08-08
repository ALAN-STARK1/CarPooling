package org.example.carpooling.annotation;

import org.example.carpooling.enumeration.OperationType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoFill {
    OperationType value();

}
