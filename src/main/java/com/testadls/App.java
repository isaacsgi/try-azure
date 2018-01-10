package com.testadls;

import com.microsoft.azure.datalake.store.ADLStoreClient;
import com.microsoft.azure.datalake.store.oauth2.AccessTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.ClientCredsTokenProvider;
import com.microsoft.azure.datalake.store.DirectoryEntry;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

public class App 
{
        private static String accountFQDN = "SECUREDATA.azuredatalakestore.net";
        private static String cId = "SECUREDATA"; 
        private static String authTokenRoot = "https://login.microsoftonline.com/";
        private static String authTokenChild = "SECUREDATA/oauth2/token";
        private static String authTokenUrl = authTokenRoot + authTokenChild;
        private static String cKey = "SECUREDATA";
  
    public static void main(String[] args) throws Exception {
      
            PipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().create();
            Pipeline pipeline = Pipeline.create(options);
            ArrayList<String> data = new ArrayList<String>();
            data.add("/gisampledata/Drivers.txt");
            data.add("/gisampledata/DriverShiftTrips.csv");
            System.out.println("peekaboo");
            System.out.println(data);

            pipeline.apply(Create.of(data))
                    .apply(ParDo.of(new DoFn<String, String>() {
              @ProcessElement
              public void processElement(ProcessContext processContext) throws IOException {
                 // read the file at filename using the azure client
                 // for instance if we have multiple file
                 System.out.println("In processElement");
                 String filename = processContext.element();
                 System.out.println(">>filename: "+filename);
                 try{
                      AccessTokenProvider provider = new ClientCredsTokenProvider(authTokenUrl, cId, cKey);    
                      System.out.println("created provider");
                      ADLStoreClient datalakeClient = ADLStoreClient.createClient(accountFQDN, provider);
                      System.out.println("created client");
                      InputStream in = datalakeClient.getReadStream(filename);
                      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                      String line;
                      int lineNum = 0;
                      while ((line = reader.readLine()) != null) 
                       {
                        lineNum++;
                        System.out.println(">>"+filename+" Line No: "+lineNum);
                        processContext.output(">>"+filename+" Line No "+lineNum+": "+line);
                       }
                      reader.close();
                      }
                      catch (IOException e) {
                       //do something clever with the exception
                       System.out.println(e.getMessage());
                      }
              }    
        }))
       .apply("Display Lines", ParDo.of(new DoFn<String, Void>() {
          @ProcessElement
          public void processElement(ProcessContext processContext) throws IOException {
            String element = processContext.element();
            System.out.println(element);
          }
        }));

        pipeline.run();
}    

}
