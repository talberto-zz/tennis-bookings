/*
 * Created by IntelliJ IDEA.
 * User: trodriguez
 * Date: 01/05/15
 * Time: 15:20
 */
package models.site;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE, PARAMETER})
@BindingAnnotation
public @interface ScreenshotsFolder {
}
