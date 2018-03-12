package ch.rhj.junit.mockito;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Parameter;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.rhj.junit.support.ParameterSupport;

public class MockitoExtension implements TestInstancePostProcessor, ParameterResolver {

	@Override
	public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
		
		MockitoAnnotations.initMocks(testInstance);
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		return ParameterSupport.supports(parameterContext, Mock.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {

		Parameter parameter = parameterContext.getParameter();
		Class<?> type = parameter.getType();
		Store store = extensionContext.getStore(Namespace.create(MockitoExtension.class, type));
		String name = getName(parameter);
		
		if (name == null)
			return store.getOrComputeIfAbsent(type.getCanonicalName(), key -> mock(type));
		
		return store.getOrComputeIfAbsent(name, key -> mock(type, name));
	}
	
	private String getName(Parameter parameter) {
		
		String name = parameter.getAnnotation(Mock.class).name().trim();
		
		if (name.isEmpty())
			name = null;
		
		if (name == null && parameter.isNamePresent())
			name = parameter.getName();
		
		return name;
	}
}
