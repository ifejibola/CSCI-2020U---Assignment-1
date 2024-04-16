package com.spamdetector.util;

import com.spamdetector.domain.TestFile;

import java.io.*;
import java.util.*;


/**
 * TODO: This class will be implemented by you
 * You may create more methods to help you organize you strategy and make you code more readable
 */
public class SpamDetector {
    // public static void main(String[] args){         //test
    //     SpamDetector test = new SpamDetector();
    //    File file = new File("spamDetectorServer/src/main/resources/data");
        // test.trainAndTest(file);
        // for(TestFile thing : test.trainAndTest(file)){
        //     System.out.println(thing.getFilename()+":"+thing.getSpamProbability()+":"+thing.getActualClass());
        // }
        // File[] files = new File("spamDetectorServer/src/main/resources/data/test/spam").listFiles();
        // File[] files2 = new File("spamDetectorServer/src/main/resources/data/test/ham").listFiles();
        // double numFiles = files.length + files2.length;
        // System.out.println(test.accuracy(test.trainAndTest(file),numFiles));
        // System.out.println(test.precision(test.trainAndTest(file)));
    // }

    public static HashSet<String> dictionary(File file){        //method to turn dictionary file into HashSet.
        HashSet<String> dict = new HashSet<>();     //initiate HashSet.
        try{
            Scanner scanner = new Scanner(file);        //initiate scanner.
            while(scanner.hasNextLine()){       //while next word exists.
                String word = scanner.nextLine();       //word equals next line.
                dict.add(word);         //add word to HashSet.
            }
            scanner.close();        //close scanner.
        } catch(FileNotFoundException e){       //catch exceptions.
            System.out.println("An error has occurred.");
            e.printStackTrace();
        }
        return dict;        //return HashSet.
    }

    public static HashSet<String> readFile(File file, HashSet<String> dict){        //Read file method.
        HashSet<String> fileWordList = new HashSet<>();     //create HashSet to contain final set of words.
        try{
            Scanner scanner = new Scanner(file);        //initiate scanner.
            while(scanner.hasNextLine()){           //while next line in file exists.
                String line = scanner.nextLine();       //read line
                String[] words = line.split(" ");       //split line into words.
                for(String word : words){       //for each word in words list, add to final set of words.
                    if(dict.contains(word)){        //compare words in email with words in dictionary to cut down on gibberish.
                        fileWordList.add(word);     //add word to set of words.
                    }
                }
            }
            scanner.close();        //close scanner.
        } catch (FileNotFoundException e){          //catch exceptions.
            System.out.print("An error occurred.");
            e.printStackTrace();
        }
        return fileWordList;        //return set of words.
    }

     public static TreeMap<String, Double> PWS(TreeMap<String, Integer> freq, File[] spam){          //Probability that word is in spam method.
            int spamSize = spam.length;
            TreeMap<String, Double> wordInSpamProb = new TreeMap<>();
            for(String word : freq.keySet()){
                double prob = (double)freq.get(word) / spamSize;
                wordInSpamProb.put(word, prob);
            }
            return wordInSpamProb;
     }

    public static TreeMap<String, Double> PWH(TreeMap<String, Integer> freq, File[] ham, File[] ham2){      //Probability that word is in ham method.
        int hamSize = ham.length + ham2.length;
        TreeMap<String, Double> wordInHamProb = new TreeMap<>();
        for(String word : freq.keySet()){
            double prob = (double)freq.get(word) / hamSize;
            wordInHamProb.put(word, prob);
        }
        return wordInHamProb;
    }

    public static TreeMap<String, Double> PSW(TreeMap<String, Double> PWS, TreeMap<String, Double> PWH){        //Probability that file is spam given it contains word method.
        TreeMap<String, Double> spamProb = new TreeMap<>();
        for(String word : PWS.keySet()){
            double prob;
            if(PWH.get(word) == null){
                prob = PWS.get(word) / (PWS.get(word) + 0);
                spamProb.put(word, prob);
            }
            else{
                prob = PWS.get(word) / (PWS.get(word) + PWH.get(word));
                spamProb.put(word, prob);
            }
        }
        return spamProb;
    }

    public static double spamProbability(TreeMap<String, Double> PSW){      //Probability that file is spam method.
        double n = 0;
        for(String word : PSW.keySet()){
            n = n + Math.log(1 - PSW.get(word)) - Math.log(PSW.get(word));
        }
        double spamFileProb = 1 / (1 + Math.pow(Math.E, n));
        return spamFileProb;
    }

    public List<TestFile> trainAndTest(File mainDirectory) {
//        TODO: main method of loading the directories and files, training and testing the model
        TreeMap<String, Integer> trainHamFreq = new TreeMap<>();        //initiate frequency maps.
        TreeMap<String, Integer> trainSpamFreq = new TreeMap<>();
        File[] ham = new File(mainDirectory+"/train/ham").listFiles();        //load ham directory.
        File dictionary = new File(mainDirectory+"/train/Dictionary.txt");
        HashSet<String> dict = dictionary(dictionary);
        if(ham != null){        //if something is in directory, continue.
            for (File file : ham) {     //go through each file in directory.
                HashSet<String> wordList = new HashSet<>(readFile(file, dict));       //create wordlist using readFile method.
                for (String word : wordList) {      //go through each word in the wordlist.
                    if (trainHamFreq.containsKey(word)) {       //if key for word already exists, edit the frequency.
                        trainHamFreq.replace(word, trainHamFreq.get(word) + 1);
                    } else {        //else create new frequency for that word starting with one.
                        trainHamFreq.put(word, 1);
                    }
                }
            }
        }
        else{       //if no file in directory, do nothing and return message.
            System.out.print("No files in directory.");
        }
        File[] ham2 = new File(mainDirectory+"/train/ham2").listFiles();        //load ham2 directory.
        if(ham2 != null){        //if something is in directory, continue.
            for (File file : ham2) {     //go through each file in directory.
                HashSet<String> wordList = new HashSet<>(readFile(file, dict));       //create wordlist using readFile method.
                for (String word : wordList) {      //go through each word in the wordlist.
                    if (trainHamFreq.containsKey(word)) {       //if key for word already exists, edit the frequency.
                        trainHamFreq.replace(word, trainHamFreq.get(word) + 1);
                    } else {        //else create new frequency for that word starting with one.
                        trainHamFreq.put(word, 1);
                    }
                }
            }
        }
        else{       //if no file in directory, do nothing and return message.
            System.out.print("No files in directory.");
        }
        File[] spam = new File(mainDirectory+"/train/spam").listFiles();        //load spam directory.
        if(spam != null){        //if something is in directory, continue.
            for (File file : spam) {     //go through each file in directory.
                HashSet<String> wordList = new HashSet<>(readFile(file, dict));       //create wordlist using readFile method.
                for (String word : wordList) {      //go through each word in the wordlist.
                    if (trainSpamFreq.containsKey(word)) {       //if key for word already exists, edit the frequency.
                        trainSpamFreq.replace(word, trainSpamFreq.get(word) + 1);
                    } else {        //else create new frequency for that word starting with one.
                        trainSpamFreq.put(word, 1);
                    }
                }
            }
        }
        else{       //if no file in directory, do nothing and return message.
            System.out.print("No files in directory.");
        }

        HashSet<TestFile> testFiles = new HashSet<>();
        
        File[] testHam = new File(mainDirectory+"/test/ham").listFiles();       //Testing if files are spam     //work-in-progress
        if(testHam != null){
            for(File file : testHam){
                HashSet<String> fileWords = new HashSet<>(readFile(file, dict));
                TreeMap<String, Double> testingHamPSW = new TreeMap<>();
                TreeMap<String, Double> probSpamContainingWord = PSW(PWS(trainSpamFreq,spam), PWH(trainHamFreq,ham,ham2));
                for(String word : fileWords){
                    if(probSpamContainingWord.get(word) != null){
                        testingHamPSW.put(word, probSpamContainingWord.get(word));
                    }
                }
                testFiles.add(new TestFile(file.getName(), spamProbability(testingHamPSW), "ham"));
                // spamProbability(testingHamPSW);
                // System.out.println(spamProbability(testingHamPSW));      //test
                // System.out.println("---------------------------------");
            }
        }
        else{       //if no file in directory, do nothing and return message.
            System.out.println("No files in directory.");
        }

        File[] testSpam = new File(mainDirectory+"/test/spam").listFiles();       //Testing if files are spam     //work-in-progress
        if(testSpam != null){
            for(File file : testSpam){
                HashSet<String> fileWords = new HashSet<>(readFile(file, dict));
                TreeMap<String, Double> testingSpamPSW = new TreeMap<>();
                TreeMap<String, Double> probSpamContainingWord = PSW(PWS(trainSpamFreq,spam), PWH(trainHamFreq,ham,ham2));
                for(String word : fileWords){
                    if(probSpamContainingWord.get(word) != null){
                        testingSpamPSW.put(word, probSpamContainingWord.get(word));
                    }
                }
                testFiles.add(new TestFile(file.getName(), spamProbability(testingSpamPSW), "spam"));
                // spamProbability(testingSpamPSW);
                // System.out.println(spamProbability(testingSpamPSW));      //test
                // System.out.println("---------------------------------");
            }
        }
        else{       //if no file in directory, do nothing and return message.
            System.out.println("No files in directory.");
        }
        // for(TestFile thing : testFiles){        //test
        //     System.out.println(thing.getFilename()+":"+thing.getSpamProbability()+":"+thing.getActualClass());
        // }
        // System.out.println(testFiles.size());

        return new ArrayList<TestFile>(testFiles);
    }

    public double accuracy(List<TestFile> testFiles, double numFile){
        double numTruePositives = 0;
        double numTrueNegatives = 0;
        HashSet<TestFile> ham = new HashSet<>();
        HashSet<TestFile> spam = new HashSet<>();
        for(TestFile testFile : testFiles){
            if(testFile.getSpamProbability() > (0.0001)){
                spam.add(testFile);
            }
            else{
                ham.add(testFile);
            }
        }
        for(TestFile testFile : spam){
            if(testFile.getActualClass().equals("spam")){
                numTrueNegatives = numTrueNegatives + 1;
            }
        }
        for(TestFile testFile : ham){
            if(testFile.getActualClass() == "ham"){
                numTruePositives = numTruePositives + 1;
            }
        }
//        File[] files = new File("spamDetectorServer/src/main/resources/data/test/spam").listFiles();
//        File[] files2 = new File("spamDetectorServer/src/main/resources/data/test/ham").listFiles();
//        double numFiles = files.length + files2.length;

        return (numTruePositives+numTrueNegatives)/numFile;
        // return numFiles;
    }

    public double precision(List<TestFile> testFiles){
        double numTruePositives = 0;
        double numFalsePositives = 0;
        HashSet<TestFile> ham = new HashSet<>();
        HashSet<TestFile> spam = new HashSet<>();
        for(TestFile testFile : testFiles){
            if(testFile.getSpamProbability() < (0.0001)){
                ham.add(testFile);
            }
        }
        for(TestFile testFile : ham){
            if(testFile.getActualClass() == "ham"){
                numTruePositives = numTruePositives + 1;
            }
            else{
                numFalsePositives = numFalsePositives + 1;
            }
        }
        return numTruePositives / (numFalsePositives + numTruePositives);
    }


}
