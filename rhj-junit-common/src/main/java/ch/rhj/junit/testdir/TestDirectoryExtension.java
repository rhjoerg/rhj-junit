package ch.rhj.junit.testdir;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import ch.rhj.junit.util.AbstractExtension;
import ch.rhj.junit.util.Parameters;
import ch.rhj.junit.util.Paths;

public class TestDirectoryExtension extends AbstractExtension implements ParameterResolver {
	
	private static final String KEY = "testDirectory";

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		return Parameters.test(parameterContext, TestDirectory.class, Path.class, File.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		Path directory = getOrCreateDirectory(extensionContext);
		Class<?> parameterType = parameterContext.getParameter().getType();
		
		if (Path.class.equals(parameterType))
			return directory;
		
		if (File.class.equals(parameterType))
			return directory.toFile();
		
		throw new ParameterResolutionException("doesnt' happen");
	}
	
	protected Path getOrCreateDirectory(ExtensionContext context) {
		
		return getOrCreateObject(context, KEY, Path.class, this::createDirectory);
	}
	
	private Path createDirectory(ExtensionContext context) {
		
		String className = context.getRequiredTestClass().getSimpleName();
		String testName = context.getRequiredTestMethod().getName();
		
		Path directory = Paths.of("build", "test", className, testName);
		
		Paths.mkdirs(directory);
		
		return directory;
	}
}
