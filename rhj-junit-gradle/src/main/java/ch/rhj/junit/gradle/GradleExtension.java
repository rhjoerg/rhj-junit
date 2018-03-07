package ch.rhj.junit.gradle;

import java.nio.file.Path;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import ch.rhj.junit.testdir.TestDirectoryExtension;
import ch.rhj.junit.util.Parameters;
import ch.rhj.junit.util.Paths;

public class GradleExtension extends TestDirectoryExtension implements ParameterResolver, BeforeEachCallback {
	
	private static final String KEY = "gradleRunner";

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		return Parameters.test(parameterContext, Gradle.class, GradleRunner.class)
				|| super.supportsParameter(parameterContext, extensionContext);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		if (!GradleRunner.class.equals(parameterContext.getParameter().getType()))
			return super.resolveParameter(parameterContext, extensionContext);
		
		return getOrCreateRunner(extensionContext);
	}
	
	protected GradleRunner getOrCreateRunner(ExtensionContext context) {
		
		return getOrCreateObject(context, KEY, GradleRunner.class, this::createRunner);
	}
	
	protected GradleRunner createRunner(ExtensionContext context) {
		
		Path directory = getOrCreateDirectory(context);
		
		return GradleRunner.create().withProjectDir(directory.toFile());
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {

		Path directory = getOrCreateDirectory(context);
		
		Paths.delete(directory);
		Paths.mkdirs(directory);
	}
}
