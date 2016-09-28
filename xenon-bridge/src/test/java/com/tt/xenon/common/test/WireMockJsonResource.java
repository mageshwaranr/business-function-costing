package com.tt.xenon.common.test;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by mageshwaranr on 8/22/2016.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface WireMockJsonResource {

  String fileName();

  Class<?> clazz() default StubMapping.class;
}