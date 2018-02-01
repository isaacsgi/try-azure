/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.testadls.common;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.extensions.gcp.auth.NullCredentialInitializer;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryOptions;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubOptions;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.util.BackOff;
import org.apache.beam.sdk.util.BackOffUtils;
import org.apache.beam.sdk.util.FluentBackoff;
import org.apache.beam.sdk.util.RetryHttpRequestInitializer;
import org.apache.beam.sdk.util.Sleeper;
import org.apache.beam.sdk.util.Transport;
import org.joda.time.Duration;

/**
 * The utility class that sets up and tears down external resources,
 * and cancels the streaming pipelines once the program terminates.
 *
 * <p>It is used to run Beam examples.
 */
public class ExampleUtils {

  private static final int SC_NOT_FOUND = 404;

  /**
   * \p{L} denotes the category of Unicode letters,
   * so this pattern will match on everything that is not a letter.
   *
   * <p>It is used for tokenizing strings in the wordcount examples.
   */
  public static final String TOKENIZER_PATTERN = "[^\\p{L}]+";

  private final PipelineOptions options;
  private Bigquery bigQueryClient = null;
  private Pubsub pubsubClient = null;
  private Set<PipelineResult> pipelinesToCancel = Sets.newHashSet();
  private List<String> pendingMessages = Lists.newArrayList();

  /**
   * Do resources and runner options setup.
   */
  public ExampleUtils(PipelineOptions options) {
    this.options = options;
  }




}
