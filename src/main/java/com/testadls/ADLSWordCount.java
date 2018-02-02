package com.testadls;

import com.testadls.common.ExampleUtils;

/*
import com.microsoft.azure.datalake.store.ADLStoreClient;
import com.microsoft.azure.datalake.store.oauth2.AccessTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.ClientCredsTokenProvider;
import com.microsoft.azure.datalake.store.DirectoryEntry;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
*/

import java.io.IOException;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

public class ADLSWordCount 
{  

static class ExtractWordsFn extends DoFn<String, String> {
        private final Counter emptyLines = Metrics.counter(ExtractWordsFn.class, "emptyLines");
    
        @ProcessElement
        public void processElement(ProcessContext c) {
          if (c.element().trim().isEmpty()) {
            emptyLines.inc();
          }
    
          // Split the line into words.
          String[] words = c.element().split(ExampleUtils.TOKENIZER_PATTERN);
    
          // Output each word encountered into the output PCollection.
          for (String word : words) {
            if (!word.isEmpty()) {
              c.output(word);
            }
          }
        }
      }
    
      /** A SimpleFunction that converts a Word and Count into a printable string. */
      public static class FormatAsTextFn extends SimpleFunction<KV<String, Long>, String> {
        @Override
        public String apply(KV<String, Long> input) {
          return input.getKey() + ": " + input.getValue();
        }
      }
    
      /**
       * A PTransform that converts a PCollection containing lines of text into a PCollection of
       * formatted word counts.
       *
       * <p>Concept #3: This is a custom composite transform that bundles two transforms (ParDo and
       * Count) as a reusable PTransform subclass. Using composite transforms allows for easy reuse,
       * modular testing, and an improved monitoring experience.
       */
      public static class CountWords extends PTransform<PCollection<String>,
          PCollection<KV<String, Long>>> {
        @Override
        public PCollection<KV<String, Long>> expand(PCollection<String> lines) {
    
          // Convert lines of text into individual words.
          PCollection<String> words = lines.apply(
              ParDo.of(new ExtractWordsFn()));
    
          // Count the number of times each word occurs.
          PCollection<KV<String, Long>> wordCounts =
              words.apply(Count.<String>perElement());
    
          return wordCounts;
        }
      }
    
      /**
       * Options supported by {@link WordCount}.
       *
       * <p>Concept #4: Defining your own configuration options. Here, you can add your own arguments
       * to be processed by the command-line parser, and specify default values for them. You can then
       * access the options values in your pipeline code.
       *
       * <p>Inherits standard configuration options.
       */
      public interface ADLSWordCountOptions extends PipelineOptions {
    
        @Description("Service Principal with access to Azure Data Lake Storage URL we are accessing"
        + " - Azure Active Directory Authorizaion End Point")
        @Default.String("SECUREDATA")
        String getAadAuthEndpoint();
        void setAadAuthEndpoint(String value);

        @Description("Service Principal with access to Azure Data Lake Storage URL we are accessing"
                + " - Azure Active Directory Client ID")
        @Default.String("SECUREDATA")
        String getAadClientId();
        void setAadClientId(String value);

        @Description("Service Principal with access to Azure Data Lake Storage URL we are accessing"
                + " - Azure Active Directory Client Secret")
        @Default.String("SECUREDATA")
        String getAadClientSecret();
        void setAadClientSecret(String value);

        @Description("Azure Data Lake Input File URI - must start with 'adl://' ")
        @Default.String("adl://gidatalake1.azuredatalakestore.net/gisampledata/Drivers.txt")
        String getAdlInputURI();
        void setAdlInputURI(String value);
      }
    
      public static void main(String[] args) {
        ADLSWordCountOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
          .as(ADLSWordCountOptions.class);
        Pipeline p = Pipeline.create(options);
        System.out.println(">>ADLSWordCount Got Input URI: " + options.getAdlInputURI());
        p.apply("ReadLines", TextIO.read().from(options.getAdlInputURI()))
         .apply(new CountWords())
         .apply(MapElements.via(new FormatAsTextFn()))
         .apply("Display Lines", ParDo.of(new DoFn<String, Void>() {
                @ProcessElement
                public void processElement(ProcessContext processContext) throws IOException {
                  System.out.println(">>ADLSWordCount processContext " + processContext.element());
                  String element = processContext.element();
                  System.out.println(element);
                }
              }));
    
        p.run().waitUntilFinish();
      }
}
