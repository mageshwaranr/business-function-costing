package com.tt.xenon.common.annotations;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.NameBinding;
import java.lang.annotation.*;

/**
 * Created by mageshwaranr on 8/9/2016.
 * <p>
 * Annotation to represent HTTP Method PATCH
 */
@Target( {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("PATCH")
@Documented
@NameBinding
public @interface PATCH {
}
