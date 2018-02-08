/*
 * Copyright DataStax, Inc.
 *
 * This software is subject to the below license agreement.
 * DataStax may make changes to the agreement from time to time,
 * and will post the amended terms at
 * https://www.datastax.com/terms/datastax-dse-bulk-utility-license-terms.
 */
package com.datastax.dsbulk.engine.internal.codecs.writetime;

import static com.datastax.dsbulk.engine.tests.EngineAssertions.assertThat;
import static java.time.ZoneOffset.UTC;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.datastax.driver.extras.codecs.jdk8.InstantCodec;
import com.datastax.dsbulk.engine.internal.codecs.json.JsonNodeToInstantCodec;
import com.datastax.dsbulk.engine.internal.codecs.string.StringToInstantCodec;
import com.datastax.dsbulk.engine.internal.codecs.temporal.DateToTemporalCodec;
import com.datastax.dsbulk.engine.internal.codecs.temporal.TemporalToTemporalCodec;
import com.datastax.dsbulk.engine.internal.settings.CodecSettings;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/** */
class WriteTimeCodecTest {

  private DateTimeFormatter parser = CodecSettings.CQL_DATE_TIME_FORMAT;
  private TimeUnit unit = MILLISECONDS;
  private Instant epoch = Instant.EPOCH;

  @Test
  void should_convert_to_timestamp_micros() {

    assertThat(new WriteTimeCodec<>(new StringToInstantCodec(parser, unit, epoch)))
        .convertsFrom("2017-11-30T14:46:56+01:00")
        .to(
            MILLISECONDS.toMicros(
                ZonedDateTime.parse("2017-11-30T14:46:56+01:00").toInstant().toEpochMilli()));

    assertThat(new WriteTimeCodec<>(new StringToInstantCodec(parser, unit, epoch)))
        .convertsFrom("123456")
        .to(MILLISECONDS.toMicros(123456L));

    assertThat(new WriteTimeCodec<>(new JsonNodeToInstantCodec(parser, unit, epoch)))
        .convertsFrom(JsonNodeFactory.instance.textNode("2017-11-30T14:46:56+01:00"))
        .to(
            MILLISECONDS.toMicros(
                ZonedDateTime.parse("2017-11-30T14:46:56+01:00").toInstant().toEpochMilli()));

    assertThat(new WriteTimeCodec<>(new JsonNodeToInstantCodec(parser, unit, epoch)))
        .convertsFrom(JsonNodeFactory.instance.textNode("123456"))
        .to(MILLISECONDS.toMicros(123456L));

    assertThat(new WriteTimeCodec<>(new JsonNodeToInstantCodec(parser, unit, epoch)))
        .convertsFrom(JsonNodeFactory.instance.numberNode(123456L))
        .to(MILLISECONDS.toMicros(123456L));

    assertThat(
            new WriteTimeCodec<>(
                new TemporalToTemporalCodec<>(Instant.class, InstantCodec.instance, UTC)))
        .convertsFrom(ZonedDateTime.parse("2017-11-30T14:46:56+01:00").toInstant())
        .to(
            MILLISECONDS.toMicros(
                ZonedDateTime.parse("2017-11-30T14:46:56+01:00").toInstant().toEpochMilli()));

    assertThat(
            new WriteTimeCodec<>(
                new TemporalToTemporalCodec<>(ZonedDateTime.class, InstantCodec.instance, UTC)))
        .convertsFrom(ZonedDateTime.parse("2017-11-30T14:46:56+01:00"))
        .to(
            MILLISECONDS.toMicros(
                ZonedDateTime.parse("2017-11-30T14:46:56+01:00").toInstant().toEpochMilli()));

    assertThat(
            new WriteTimeCodec<>(
                new DateToTemporalCodec<>(java.util.Date.class, InstantCodec.instance, UTC)))
        .convertsFrom(Date.from(ZonedDateTime.parse("2017-11-30T14:46:56+01:00").toInstant()))
        .to(
            MILLISECONDS.toMicros(
                ZonedDateTime.parse("2017-11-30T14:46:56+01:00").toInstant().toEpochMilli()));

    assertThat(
            new WriteTimeCodec<>(
                new DateToTemporalCodec<>(java.sql.Timestamp.class, InstantCodec.instance, UTC)))
        .convertsFrom(Timestamp.from(ZonedDateTime.parse("2017-11-30T14:46:56+01:00").toInstant()))
        .to(
            MILLISECONDS.toMicros(
                ZonedDateTime.parse("2017-11-30T14:46:56+01:00").toInstant().toEpochMilli()));
  }
}