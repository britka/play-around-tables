package com.provectus.edu.tests;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Locale;

import static java.lang.annotation.ElementType.*;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({FIELD})
public @interface ElementLink {
    String locator();
    String dateMask() default "d MMM yy";

    /**
     * salary locale needs for converting currency string to double
     * value should be in format "language-country".
     * For instance, "EN-US"
     * @return
     */
    String salaryLocale() default "en-us";
}
