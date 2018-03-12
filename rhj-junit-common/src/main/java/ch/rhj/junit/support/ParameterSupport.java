package ch.rhj.junit.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.platform.commons.util.AnnotationUtils;

public class ParameterSupport {

	public static boolean supports(Parameter parameter, Class<? extends Annotation> annotationType, Class<?>... allowedTypes) {
		
		if (!AnnotationUtils.isAnnotated(parameter, annotationType))
			return false;
		
		if (allowedTypes.length == 0)
			return true;
		
		Class<?> parameterType = parameter.getType();
		
		for (Class<?> allowedType : allowedTypes ) {
			
			if (allowedType.equals(parameterType))
				return true;
		}
		
		return false;
	}
	
	public static boolean supports(ParameterContext context, Class<? extends Annotation> annotationType, Class<?>... allowedTypes) {
		
		return supports(context.getParameter(), annotationType, allowedTypes);
	}
}
