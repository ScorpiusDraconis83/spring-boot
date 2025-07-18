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

package org.springframework.boot.metrics.autoconfigure.export.jmx;

import io.micrometer.jmx.JmxConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JmxProperties}.
 *
 * @author Stephane Nicoll
 */
class JmxPropertiesTests {

	@Test
	void defaultValuesAreConsistent() {
		JmxProperties properties = new JmxProperties();
		JmxConfig config = JmxConfig.DEFAULT;
		assertThat(properties.getDomain()).isEqualTo(config.domain());
		assertThat(properties.getStep()).isEqualTo(config.step());
	}

}
