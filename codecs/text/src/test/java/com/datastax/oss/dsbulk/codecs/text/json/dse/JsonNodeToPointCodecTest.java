/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.oss.dsbulk.codecs.text.json.dse;

import static com.datastax.oss.dsbulk.tests.assertions.TestAssertions.assertThat;

import com.datastax.dse.driver.api.core.data.geometry.Point;
import com.datastax.dse.driver.internal.core.data.geometry.DefaultPoint;
import com.datastax.oss.driver.shaded.guava.common.collect.Lists;
import com.datastax.oss.dsbulk.codecs.text.json.JsonCodecUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsonNodeToPointCodecTest {

  private List<String> nullStrings = Lists.newArrayList("NULL");
  private Point point = new DefaultPoint(-1.1, -2.2);
  private ObjectMapper objectMapper = JsonCodecUtils.getObjectMapper();
  private JsonNode geoJsonNode =
      objectMapper.readTree("{\"type\":\"Point\",\"coordinates\":[-1.1,-2.2]}");

  JsonNodeToPointCodecTest() throws IOException {}

  @Test
  void should_convert_from_valid_external() throws IOException {
    JsonNodeToPointCodec codec = new JsonNodeToPointCodec(objectMapper, nullStrings);
    assertThat(codec)
        .convertsFromExternal(JsonCodecUtils.JSON_NODE_FACTORY.textNode("'POINT (-1.1 -2.2)'"))
        .toInternal(point)
        .convertsFromExternal(JsonCodecUtils.JSON_NODE_FACTORY.textNode(" point (-1.1 -2.2) "))
        .toInternal(point)
        .convertsFromExternal(
            JsonCodecUtils.JSON_NODE_FACTORY.textNode(objectMapper.writeValueAsString(geoJsonNode)))
        .toInternal(point)
        .convertsFromExternal(geoJsonNode)
        .toInternal(point)
        .convertsFromExternal(null)
        .toInternal(null)
        .convertsFromExternal(JsonCodecUtils.JSON_NODE_FACTORY.textNode(""))
        .toInternal(null)
        .convertsFromExternal(JsonCodecUtils.JSON_NODE_FACTORY.textNode("NULL"))
        .toInternal(null);
  }

  @Test
  void should_convert_from_valid_internal() {
    JsonNodeToPointCodec codec = new JsonNodeToPointCodec(objectMapper, nullStrings);
    assertThat(codec).convertsFromInternal(point).toExternal(geoJsonNode);
  }

  @Test
  void should_not_convert_from_invalid_external() {
    JsonNodeToPointCodec codec = new JsonNodeToPointCodec(objectMapper, nullStrings);
    assertThat(codec)
        .cannotConvertFromExternal(
            JsonCodecUtils.JSON_NODE_FACTORY.textNode("not a valid point literal"));
  }
}
