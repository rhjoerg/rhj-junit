package ch.rhj.junit.resource;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import ch.rhj.junit.support.ParameterSupport;
import ch.rhj.junit.support.ResourcesSupport;

public class ResourcesExtension implements ParameterResolver {

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		return ParameterSupport.supports(parameterContext, Resource.class,
				ByteBuffer.class, byte[].class, String.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {

		Parameter parameter = parameterContext.getParameter();
		Resource annotation = parameter.getAnnotation(Resource.class);
		String name = annotation.value();
		URL url = ResourcesSupport.resource(extensionContext, annotation.value());
		
		if (url == null)
			throw new ParameterResolutionException(name + " not found");
		
		Class<?> parameterType = parameter.getType();
		
		try {
		
		if (ByteBuffer.class.equals(parameterType))
			return ResourcesSupport.buffer(url);
		
		if (byte[].class.equals(parameterType))
			return ResourcesSupport.bytes(url);
		
		if (String.class.equals(parameterType))
			return ResourcesSupport.string(url, annotation.charset());
		
		} catch(IOException e) {
			
			throw new ParameterResolutionException(name + " not readable");
		}
		
		throw new IllegalStateException();
	}
}
