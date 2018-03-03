package ch.rhj.junit.resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.util.AnnotationUtils;

public class ResourcesExtension implements ParameterResolver {

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		Parameter parameter = parameterContext.getParameter();
		
		if (!AnnotationUtils.isAnnotated(parameter, Resource.class))
			return false;
		
		Class<?> type = parameter.getType();
		
		if (ByteBuffer.class.equals(type))
			return true;
		
		if (byte[].class.equals(type))
			return true;
		
		if (String.class.equals(type))
			return true;
		
		return false;
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {

		Parameter parameter = parameterContext.getParameter();
		Resource resource = parameter.getAnnotation(Resource.class);
		String name = resource.value();
		Class<?> clazz = extensionContext.getRequiredTestClass();
		URL url = clazz.getResource(name);
		
		if (url == null)
			throw new ParameterResolutionException(name + " not found");
		
		try {
			
			URLConnection connection = url.openConnection();
			int length = connection.getContentLength();
			ByteBuffer buffer = ByteBuffer.allocate(length);
			
			try (InputStream input = connection.getInputStream()) {
				
				Channels.newChannel(input).read(buffer);
				buffer.flip();
			}
			
			Class<?> parameterType = parameter.getType();
			
			if (ByteBuffer.class.equals(parameterType))
				return buffer;
			
			byte[] bytes = buffer.array();
			
			if (byte[].class.equals(parameterType))
				return bytes;
			
			return new String(bytes, StandardCharsets.UTF_8);
			
		} catch (IOException e) {

			throw new ParameterResolutionException(name + " not opened", e);
		}
	}

}
