package ch.rhj.junit.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.platform.commons.util.AnnotationUtils;

public class Parameters {
	
	public static boolean test(Parameter parameter, Class<? extends Annotation> annotationType, Class<?>... allowedTypes) {
		
		if (!AnnotationUtils.isAnnotated(parameter, annotationType))
			return false;
		
		Class<?> parameterType = parameter.getType();
		
		for (Class<?> allowedType : allowedTypes) {
			
			if (allowedType.equals(parameterType))
				return true;
		}
		
		return false;
	}

	public static boolean test(ParameterContext context, Class<? extends Annotation> annotationType, Class<?>... allowedTypes) {
		
		return test(context.getParameter(), annotationType, allowedTypes);
	}
}
