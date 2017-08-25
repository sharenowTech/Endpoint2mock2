package car2go.com.endpoint2mock2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Put this annotation on top of the Retrofit's API declaration methods (the ones which are annotated with GET, POST,
 * DELETE or PUT annotation) and Endpoint2mock will add it to the list of mocked endpoints.
 */
@Target(ElementType.METHOD)
public @interface MockedEndpoint {
}
