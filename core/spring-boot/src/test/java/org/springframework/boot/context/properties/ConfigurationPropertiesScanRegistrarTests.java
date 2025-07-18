/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.context.properties;

import java.io.IOException;
import java.util.Locale;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.bind.BindMethod;
import org.springframework.boot.context.properties.scan.combined.c.CombinedConfiguration;
import org.springframework.boot.context.properties.scan.combined.d.OtherCombinedConfiguration;
import org.springframework.boot.context.properties.scan.valid.ConfigurationPropertiesScanConfiguration;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConfigurationPropertiesScanRegistrar}.
 *
 * @author Madhura Bhave
 */
class ConfigurationPropertiesScanRegistrarTests {

	private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

	private final ConfigurationPropertiesScanRegistrar registrar = new ConfigurationPropertiesScanRegistrar(
			new MockEnvironment(), null);

	@Test
	void registerBeanDefinitionsShouldScanForConfigurationProperties() throws IOException {
		this.registrar.registerBeanDefinitions(getAnnotationMetadata(ConfigurationPropertiesScanConfiguration.class),
				this.beanFactory);
		BeanDefinition bingDefinition = this.beanFactory.getBeanDefinition(
				"bing-org.springframework.boot.context.properties.scan.valid.ConfigurationPropertiesScanConfiguration$BingProperties");
		BeanDefinition fooDefinition = this.beanFactory.getBeanDefinition(
				"foo-org.springframework.boot.context.properties.scan.valid.ConfigurationPropertiesScanConfiguration$FooProperties");
		BeanDefinition barDefinition = this.beanFactory.getBeanDefinition(
				"bar-org.springframework.boot.context.properties.scan.valid.ConfigurationPropertiesScanConfiguration$BarProperties");
		assertThat(bingDefinition).satisfies(hasBindMethod(BindMethod.JAVA_BEAN));
		assertThat(fooDefinition).satisfies(hasBindMethod(BindMethod.JAVA_BEAN));
		assertThat(barDefinition).satisfies(hasBindMethod(BindMethod.VALUE_OBJECT));
	}

	@Test
	void scanWhenBeanDefinitionExistsShouldSkip() throws IOException {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.setAllowBeanDefinitionOverriding(false);
		this.registrar.registerBeanDefinitions(
				getAnnotationMetadata(ConfigurationPropertiesScanConfiguration.TestConfiguration.class), beanFactory);
		assertThat(beanFactory.containsBeanDefinition(
				"foo-org.springframework.boot.context.properties.scan.valid.ConfigurationPropertiesScanConfiguration$FooProperties"))
			.isTrue();
		assertThat(beanFactory.getBeanDefinitionNames())
			.filteredOn((name) -> name.toLowerCase(Locale.ENGLISH).contains("fooproperties"))
			.hasSize(1);
	}

	@Test
	void scanWhenBasePackagesAndBasePackageClassesProvidedShouldUseThat() throws IOException {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.setAllowBeanDefinitionOverriding(false);
		this.registrar.registerBeanDefinitions(
				getAnnotationMetadata(ConfigurationPropertiesScanConfiguration.DifferentPackageConfiguration.class),
				beanFactory);
		assertThat(beanFactory.containsBeanDefinition(
				"foo-org.springframework.boot.context.properties.scan.valid.ConfigurationPropertiesScanConfiguration$FooProperties"))
			.isFalse();
		BeanDefinition aDefinition = beanFactory.getBeanDefinition(
				"a-org.springframework.boot.context.properties.scan.valid.a.AScanConfiguration$AProperties");
		BeanDefinition bFirstDefinition = beanFactory.getBeanDefinition(
				"b.first-org.springframework.boot.context.properties.scan.valid.b.BScanConfiguration$BFirstProperties");
		BeanDefinition bSecondDefinition = beanFactory.getBeanDefinition(
				"b.second-org.springframework.boot.context.properties.scan.valid.b.BScanConfiguration$BSecondProperties");
		assertThat(aDefinition).satisfies(hasBindMethod(BindMethod.JAVA_BEAN));
		// Constructor injection
		assertThat(bFirstDefinition).satisfies(hasBindMethod(BindMethod.VALUE_OBJECT));
		// Post-processing injection
		assertThat(bSecondDefinition).satisfies(hasBindMethod(BindMethod.JAVA_BEAN));
	}

	@Test
	void scanWhenComponentAnnotationPresentShouldSkipType() throws IOException {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.setAllowBeanDefinitionOverriding(false);
		this.registrar.registerBeanDefinitions(getAnnotationMetadata(CombinedScanConfiguration.class), beanFactory);
		assertThat(beanFactory.getBeanDefinitionCount()).isZero();
	}

	@Test
	void scanWhenOtherComponentAnnotationPresentShouldSkipType() throws IOException {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.setAllowBeanDefinitionOverriding(false);
		this.registrar.registerBeanDefinitions(getAnnotationMetadata(OtherCombinedScanConfiguration.class),
				beanFactory);
		assertThat(beanFactory.getBeanDefinitionCount()).isZero();
	}

	private Consumer<BeanDefinition> hasBindMethod(BindMethod bindMethod) {
		return (definition) -> {
			assertThat(definition.hasAttribute(BindMethod.class.getName())).isTrue();
			assertThat(definition.getAttribute(BindMethod.class.getName())).isEqualTo(bindMethod);
		};
	}

	private AnnotationMetadata getAnnotationMetadata(Class<?> source) throws IOException {
		return new SimpleMetadataReaderFactory().getMetadataReader(source.getName()).getAnnotationMetadata();
	}

	@ConfigurationPropertiesScan(basePackageClasses = CombinedConfiguration.class)
	static class CombinedScanConfiguration {

	}

	@ConfigurationPropertiesScan(basePackageClasses = OtherCombinedConfiguration.class)
	static class OtherCombinedScanConfiguration {

	}

}
