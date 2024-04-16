package com.spamdetector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spamdetector.domain.TestFile;
import com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.net.URL;

import jakarta.ws.rs.core.Response;

@Path("/spam")
public class SpamResource {

//    your SpamDetector Class responsible for all the SpamDetecting logic
    SpamDetector detector = new SpamDetector();

    ObjectMapper objectMapper = new ObjectMapper();

    SpamResource(){
//        TODO: load resources, train and test to improve performance on the endpoint calls
        System.out.print("Training and testing the model, please wait");

//      TODO: call  this.trainAndTest();
        this.trainAndTest();

    }

    private String readFileContents(String filename) {
        /**
         * if there is no '/' at the beginning, the following function call will return `null`
         */
        String f;
        if (filename.charAt(0) != '/') {
            f = '/' + filename;
        } else {
            f = filename;
        }

        /**
         * trying to open and read the file
         */
//        try {
//            java.nio.file.Path file = java.nio.file.Path.of(
//                    StudentResource.class.getResource(f)
//                            .toString()
//                            .substring(6));
//            return Files.readString(file);
//        } catch (IOException e) {
//            // something went wrong
//            return "Did you forget to create the file?\n" +
//                    "Is the file in the right location?\n" +
//                    e.toString();
//        }

        try {
            java.nio.file.Path filePath = java.nio.file.Path.of(SpamResource.class.getResource(f).toURI());
            return Files.readString(filePath);
        } catch (IOException | URISyntaxException e) {
            return "An error occurred while trying to read the file:\n" +
                    "1. Ensure that the file exists and is accessible.\n" +
                    "2. Confirm the file is in the correct location.\n" +
                    "3. Check for any issues with the file's content or format.\n" +
                    "Error Details: " + e.toString();
        }
    }



    @GET
    @Produces("application/json")
    public Response getSpamResults() {
//       TODO: return the test results list of TestFile, return in a Response object
        Response response = null;

        File mainDirectory = null;
        URL url = this.getClass().getClassLoader().getResource("/data");

        try{
            mainDirectory = new File(url.toURI());
        }catch(URISyntaxException e)
        {
            throw new RuntimeException(e);
        }

        try{
            response = Response.status(200)
                    .header("Access-Control-Allow-Origin", "http://localhost:63342")// allow client access
                    .header("Content-Type", "application/json")
//                    .entity(readFileContents("/test.json"))
                    .entity(objectMapper.writeValueAsString(detector.trainAndTest(mainDirectory)))

//                    .entity(objectMapper.writeValueAsString(testResult))
                    .build();
        }catch(JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
        return response;
    }

    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() {
//      TODO: return the accuracy of the detector, return in a Response object

        String accuracyResult = "";
        Response response = null;

        File mainHam = null;
        File mainSpam = null;
        File mainDirectory = null;

        URL url = this.getClass().getClassLoader().getResource("/data");
        URL urlHam = this.getClass().getClassLoader().getResource("/data/test/ham");
        URL urlSpam = this.getClass().getClassLoader().getResource("/data/test/spam");


        try{
            mainDirectory = new File(url.toURI());
            mainHam = new File(urlHam.toURI());
            mainSpam = new File(urlSpam.toURI());

        }catch(URISyntaxException e)
        {
            throw new RuntimeException(e);
        }

//        System.out.println(detector.accuracy(detector.trainAndTest(mainDirectory)));
        double hamLength = mainHam.listFiles().length;
        double spamLength = mainSpam.listFiles().length;

        try{
            response = Response.status(200)
                    .header("Access-Control-Allow-Origin", "http://localhost:63342")// allow client access
                    .header("Content-Type", "application/json")
                    .entity(objectMapper.writeValueAsString(detector.accuracy(detector.trainAndTest(mainDirectory),(hamLength+spamLength))))

//                    .entity(objectMapper.writeValueAsString(detector.accuracy(detector.trainAndTest(mainDirectory))))
                    .build();
        }catch(JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
        return response;
    }

    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() {
       //      TODO: return the precision of the detector, return in a Response object

        String precisionResult = "";
        Response response = null;

        File mainDirectory = null;
        URL url = this.getClass().getClassLoader().getResource("/data");

        try{
            mainDirectory = new File(url.toURI());
        }catch(URISyntaxException e)
        {
            throw new RuntimeException(e);
        }


        try{
            response = Response.status(200)
                    .header("Access-Control-Allow-Origin", "http://localhost:63342")// allow client access
                    .header("Content-Type", "application/json")
                    .entity(objectMapper.writeValueAsString(detector.precision(detector.trainAndTest(mainDirectory))))
                    .build();
        }catch(JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
        return response;
    }

    private List<TestFile> trainAndTest()  {
        if (this.detector==null){
            this.detector = new SpamDetector();
        }

//        TODO: load the main directory "data" here from the Resources folder
        File mainDirectory = null;
        URL url = this.getClass().getClassLoader().getResource("/data");

        try{
            mainDirectory = new File(url.toURI());
        }catch(URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
        return this.detector.trainAndTest(mainDirectory);
    }
}