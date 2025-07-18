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

package org.springframework.boot.reactor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import reactor.core.Scannable;
import reactor.core.publisher.Flux;

import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ReactorEnvironmentPostProcessor}.
 *
 * @author Brian Clozel
 */

@Disabled("Tests rely on static initialization and are flaky on CI")
class ReactorEnvironmentPostProcessorTests {

	static {
		MockEnvironment environment = new MockEnvironment();
		environment.setProperty("spring.threads.virtual.enabled", "true");
		ReactorEnvironmentPostProcessor postProcessor = new ReactorEnvironmentPostProcessor();
		postProcessor.postProcessEnvironment(environment, null);
	}

	@Test
	void enablesReactorDebugAgent() {
		InstrumentedFluxProvider fluxProvider = new InstrumentedFluxProvider();
		Flux<Integer> flux = fluxProvider.newFluxJust();
		assertThat(Scannable.from(flux).stepName())
			.startsWith("Flux.just ⇢ at org.springframework.boot.reactor.InstrumentedFluxProvider.newFluxJust");
	}

	@Test
	@EnabledForJreRange(max = JRE.JAVA_20)
	void shouldNotEnableVirtualThreads() {
		assertThat(System.getProperty("reactor.schedulers.defaultBoundedElasticOnVirtualThreads")).isNotEqualTo("true");
	}

	@Test
	@EnabledForJreRange(min = JRE.JAVA_21)
	void shouldEnableVirtualThreads() {
		assertThat(System.getProperty("reactor.schedulers.defaultBoundedElasticOnVirtualThreads")).isEqualTo("true");
	}

	@AfterEach
	void cleanup() {
		System.setProperty("reactor.schedulers.defaultBoundedElasticOnVirtualThreads", "false");
	}

}
