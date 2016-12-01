package ua.softgroup.matrix.server.supervisor.jersey.config;

import org.glassfish.jersey.server.validation.ValidationConfig;
import org.glassfish.jersey.server.validation.internal.InjectingConstraintValidatorFactory;

import javax.validation.ParameterNameProvider;
import javax.validation.Validation;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Provider
public class ValidationConfigurationContextResolver implements ContextResolver<ValidationConfig> {

    @Context
    private ResourceContext resourceContext;

    @Override
    public ValidationConfig getContext(final Class<?> type) {
        return new ValidationConfig()
                .constraintValidatorFactory(resourceContext.getResource(InjectingConstraintValidatorFactory.class))
                .parameterNameProvider(new ParamBasedParameterNameProvider());
    }

    private class ParamBasedParameterNameProvider  implements ParameterNameProvider {

        private final ParameterNameProvider nameProvider;

        public ParamBasedParameterNameProvider () {
            nameProvider = Validation.byDefaultProvider().configure().getDefaultParameterNameProvider();
        }

        @Override
        public List<String> getParameterNames(final Constructor<?> constructor) {
            return nameProvider.getParameterNames(constructor);
        }

        @Override
        public List<String> getParameterNames(final Method method) {
            List<String> names = nameProvider.getParameterNames(method);

            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation.annotationType() == HeaderParam.class) {
                        names.set(i, ((HeaderParam) annotation).value() + " header");
                    } else if (annotation.annotationType() == PathParam.class) {
                        names.set(i, ((PathParam) annotation).value() + " path parameter");
                    } else if (annotation.annotationType() == QueryParam.class) {
                        names.set(i, ((QueryParam) annotation).value() + " query parameter");
                    } else if (annotation.annotationType() == FormParam.class) {
                        names.set(i, ((FormParam) annotation).value() + " form parameter");
                    }
                }
            }

            return names;
        }
    }

}

