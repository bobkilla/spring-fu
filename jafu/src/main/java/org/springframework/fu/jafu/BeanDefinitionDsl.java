package org.springframework.fu.jafu;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.context.FunctionalClassPathScanningCandidateComponentProvider;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.ClassMetadata;
import org.springframework.util.ClassUtils;

/**
 * Jafu DSL for beans configuration.
 *
 * @see ConfigurationDsl#beans(Consumer)
 * @author Sebastien Deleuze
 */
public class BeanDefinitionDsl extends AbstractDsl {

	private final Consumer<BeanDefinitionDsl> dsl;

	BeanDefinitionDsl(Consumer<BeanDefinitionDsl> dsl) {
		this.dsl = dsl;
	}

	/**
	 * Declare a bean definition from the given bean class.
	 */
	public <T> BeanDefinitionDsl bean(Class<T> beanClass, BeanDefinitionCustomizer... customizers) {
		this.context.registerBean(beanClass, customizers);
		return this;
	}

	/**
	 * Declare a bean definition from the given bean name and class.
	 */
	public <T> BeanDefinitionDsl bean(String beanName, Class<T> beanClass, BeanDefinitionCustomizer... customizers) {
		this.context.registerBean(beanName, beanClass);
		return this;
	}

	/**
	 * Declare a bean definition from the given bean class and supplier.
	 */
	public <T> BeanDefinitionDsl bean(Class<T> beanClass, Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {
		this.context.registerBean(beanClass, supplier, customizers);
		return this;
	}

	/**
	 * Declare a bean definition from the given bean name, class and supplier.
	 */
	public <T> BeanDefinitionDsl bean(String beanName, Class<T> beanClass, Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {
		this.context.registerBean(beanName, beanClass, supplier, customizers);
		return this;
	}

	/**
	 * Scan beans from the provided base package.
	 *
	 * TODO Support generating component indexes
	 * TODO Support exclusion
	 *
	 * @param basePackage The base package to scan
	 */
	public BeanDefinitionDsl scan(String basePackage) {
		FunctionalClassPathScanningCandidateComponentProvider componentProvider = new FunctionalClassPathScanningCandidateComponentProvider();
		for(ClassMetadata metadata : componentProvider.findCandidateComponents(basePackage)) {
			Class<?> source = ClassUtils.resolveClassName(metadata.getClassName(), null);
			String beanName = BeanDefinitionReaderUtils.uniqueBeanName(source.getName(), context);
			context.registerBean(beanName, source);
		}
		return this;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
	}
}
