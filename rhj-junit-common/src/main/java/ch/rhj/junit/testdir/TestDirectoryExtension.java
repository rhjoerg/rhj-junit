package ch.rhj.junit.testdir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import ch.rhj.junit.AbstractExtension;
import ch.rhj.junit.support.DirectorySupport;
import ch.rhj.junit.support.ParameterSupport;

public class TestDirectoryExtension extends AbstractExtension implements ParameterResolver {
	
	private static final String KEY = "testDirectory";

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		return ParameterSupport.supports(parameterContext, TestDirectory.class, Path.class, File.class);
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
		
		throw new IllegalStateException();
	}
	
	protected Path getOrCreateDirectory(ExtensionContext context) {
		
		return getOrCreateObject(context, KEY, Path.class, this::createDirectory);
	}
	
	private Path createDirectory(ExtensionContext context) {
		
		Path directory = DirectorySupport.of(Paths.get("build", "test"), context);
		
		try {
			
			return Files.createDirectories(directory);
			
		} catch (IOException e) {

			throw new ParameterResolutionException("cannot create directory '" + directory + "'", e);
		}
	}
}
