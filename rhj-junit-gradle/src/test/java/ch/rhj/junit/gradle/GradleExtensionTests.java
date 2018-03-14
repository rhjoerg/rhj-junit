package ch.rhj.junit.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

import ch.rhj.junit.resource.Resource;

public class GradleExtensionTests {

	@Test
	public void help(@Gradle GradleRunner runner, @Gradle Path directory,
			@Resource("build.gradle") String buildGradle) throws Exception {
		
		assertTrue(Files.isRegularFile(directory.resolve("settings.gradle")));
		assertTrue(Files.isRegularFile(directory.resolve("build.gradle")));
		
		Files.readAllLines(directory.resolve("build.gradle"));
		
		BuildResult result = runner.withArguments("help").build();
		
		assertEquals(TaskOutcome.SUCCESS, result.task(":help").getOutcome());
	}
	
	@Test
	public void project(@Gradle Project project) {
		
		Path buildDir = project.getBuildDir().toPath();

		assertTrue(buildDir.endsWith(Paths.get("GradleExtensionTests", "project", "build")));
	}
	
	@Test
	public void projectInternal(@Gradle ProjectInternal project) {
		
		PluginContainer plugins = project.getPlugins();
		
		plugins.apply(TestPlugin.class);
		
		assertFalse(plugins.getPlugin(TestPlugin.class).evaluated);
		project.evaluate();
		assertTrue(plugins.getPlugin(TestPlugin.class).evaluated);
	}
	
	public static class TestPlugin implements Plugin<Project> {
		
		public boolean evaluated = false;

		@Override
		public void apply(Project project) {
			
			project.afterEvaluate((p) -> { evaluated = true; });
		}
	}
	
	@Test void path(@Gradle Path directory) {
		
		assertTrue(directory.endsWith(Paths.get("GradleExtensionTests", "path")));
	}
	
	@Test void file(@Gradle File directory) {
		
		assertTrue(directory.toPath().endsWith(Paths.get("GradleExtensionTests", "file")));
	}
}
