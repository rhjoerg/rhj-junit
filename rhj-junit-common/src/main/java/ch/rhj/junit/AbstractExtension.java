package ch.rhj.junit;

import java.util.function.Function;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

public abstract class AbstractExtension {

	protected Namespace getNamespace(ExtensionContext context) {
		
		return Namespace.create(getClass(), context);
	}
	
	protected Store getStore(ExtensionContext context) {
		
		return context.getStore(getNamespace(context));
	}
	
	protected <K, V> V getObject(ExtensionContext context, K key, Class<V> type) {
		
		return getStore(context).get(key, type);
	}
	
	protected <K, V> V getOrCreateObject(ExtensionContext context, K key, Class<V> type, Function<ExtensionContext, V> creator) {
		
		return getStore(context).getOrComputeIfAbsent(key, k -> creator.apply(context), type);
	}
}
