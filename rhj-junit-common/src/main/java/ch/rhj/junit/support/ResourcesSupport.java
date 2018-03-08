package ch.rhj.junit.support;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.extension.ExtensionContext;

public class ResourcesSupport {

	public static URL resource(ExtensionContext context, String name) {
		
		Class<?> testClass = context.getRequiredTestClass();
		Method testMethod = context.getRequiredTestMethod();
		String longName = testClass.getSimpleName() + "_" + testMethod.getName() + "/" + name;
		URL resource = testClass.getResource(longName);
		
		return resource == null ? testClass.getResource(name) : resource;
	}
	
	public static ByteBuffer buffer(URL url) throws IOException {
		
		URLConnection connection = url.openConnection();
		int length = connection.getContentLength();
		ByteBuffer buffer = ByteBuffer.allocate(length);
		
		try (InputStream input = connection.getInputStream()) {
			
			Channels.newChannel(input).read(buffer);
			buffer.flip();
		}
		
		return buffer;
	}
	
	public static byte[] bytes(URL url) throws IOException {
		
		return buffer(url).array();
	}
	
	public static String string(URL url, Charset charset) throws IOException {
		
		return new String(bytes(url), charset);
	}
	
	public static String string(URL url, String charset) throws IOException {
		
		return string(url, Charset.forName(charset));
	}
	
	public static String string(URL url) throws IOException {
		
		return string(url, StandardCharsets.UTF_8);
	}
}
