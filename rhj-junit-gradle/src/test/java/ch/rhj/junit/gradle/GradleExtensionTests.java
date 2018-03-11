package ch.rhj.junit.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.gradle.api.Project;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

import ch.rhj.junit.resource.Resource;
import ch.rhj.junit.resource.WithResources;

@WithGradle
@WithResources
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
}
