package car2go.com.endpoint2mock2;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

import static java.util.Collections.singleton;
import static javax.lang.model.SourceVersion.latestSupported;

@AutoService(Processor.class)
public class MockAnnotationProccessor extends AbstractProcessor{

    private CodeBlock.Builder initializationBlockBuilder;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        initializationBlockBuilder = CodeBlock.builder();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return singleton(MockedEndpoint.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(MockedEndpoint.class);

        for (Element element : elements) {
            try {
                String path = extractPath(element);

                initializationBlockBuilder.add("registry.add($S);\n", path);
            } catch (NoAnnotationException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "No GET, POST, DELETE, or PUT annotation", element);
                return true;
            }
        }

        if (elements.size() != 0) {
            FieldSpec registryField = FieldSpec.builder(Set.class, "registry", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new $T();", HashSet.class)
                    .build();

            MethodSpec getMockedEndpoints = MethodSpec.methodBuilder("getMockedEndpoints")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(Set.class)
                    .addCode("return registry;\n")
                    .build();

            TypeSpec mocksRegistry = TypeSpec.classBuilder("MocksRegistry")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(registryField)
                    .addStaticBlock(initializationBlockBuilder.build())
                    .addMethod(getMockedEndpoints)
                    .build();

            try {
                JavaFile.builder("com.car2go.mock", mocksRegistry)
                        .build()
                        .writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "IOException: " + e);
                return true;
            }
        }

        return true;
    }

    private static String extractPath(Element element) throws NoAnnotationException {
        GET getAnnotation = element.getAnnotation(GET.class);
        if (getAnnotation != null) {
            return getAnnotation.value();
        }

        POST postAnnotation = element.getAnnotation(POST.class);
        if (postAnnotation != null) {
            return postAnnotation.value();
        }

        DELETE deleteAnnotation = element.getAnnotation(DELETE.class);
        if (deleteAnnotation != null) {
            return deleteAnnotation.value();
        }

        PUT putAnnotation = element.getAnnotation(PUT.class);
        if (putAnnotation != null) {
            return putAnnotation.value();
        }

        throw new NoAnnotationException();
    }

    /**
     * Thrown when there is no {@link GET}, {@link POST}, {@link PUT} or {@link DELETE} annotation for the method.
     */
    private static class NoAnnotationException extends Exception {
    }

}
