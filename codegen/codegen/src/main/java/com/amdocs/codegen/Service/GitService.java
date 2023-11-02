package com.amdocs.codegen.Service;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
@Service
public class GitService {

    private final String gitRepoPath = "C:/Java/myGitRepo/codegenDatabase";

    // Get a list of all project names
    public List<String> getAllProjects() {
        File projectsDirectory = Paths.get(gitRepoPath, "project").toFile();
        List<String> projectNames = new ArrayList<>();

        if (projectsDirectory.exists() && projectsDirectory.isDirectory()) {
            File[] projectFolders = projectsDirectory.listFiles(File::isDirectory);

            if (projectFolders != null) {
                for (File projectFolder : projectFolders) {
                    projectNames.add(projectFolder.getName());
                }
            }
        }

        return projectNames;
    }


    // Create a new project and a JSON file in the "Data" folder
    public void createNewProject(String projectName, String filename) throws IOException {
        File dataDirectory = Paths.get(gitRepoPath, "Data/"+projectName).toFile();
        File jsonFile = Paths.get(dataDirectory.toString(), filename + ".json").toFile();

        if (!jsonFile.exists()) {
            if (jsonFile.createNewFile()) {
                // Check if the .git directory already exists
                File gitDirectory = Paths.get(dataDirectory.toString(), ".git").toFile();
                if (!gitDirectory.exists()) {
                    try (Repository repository = FileRepositoryBuilder.create(gitDirectory)) {
                        repository.create();
                    }
                }
            }
        }
        updateMetadataFile("Data/"+projectName);
    }



    public void saveJsonData(String projectName,String fileName,String jsonData ) throws IOException {
        File dataDirectory = Paths.get(gitRepoPath, "Data/"+ projectName).toFile();
        File jsonFile = Paths.get(dataDirectory.toString(), fileName + ".json").toFile();

        try (FileWriter fileWriter = new FileWriter(jsonFile)) {
            fileWriter.write(jsonData);
        }
    }

    private RestTemplate restTemplate = new RestTemplate();
    public String readJsonFileFromGitHub(String owner, String repo, String path) {
        String branchName = "main"; // Adjust the branch name if necessary
        String url = String.format("https://raw.githubusercontent.com/%s/%s/%s/%s", owner, repo, branchName, path);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            // Handle the error or log the response status and body
            return "Error: " + response.getStatusCode() + " - " + response.getBody();
        }
    }


    // Helper method to update the metadata.json file with subfolder details and JSON files
//    private void updateMetadataFile(String path) {
//        // Get a list of all directories in the Data folder
//        File dataDirectory = Paths.get(gitRepoPath, path).toFile();
//        System.out.println(dataDirectory);
//        if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
//            // Handle the case when the Data folder does not exist
//            return;
//        }
//
//        // Create a JSON array to store folder details, including subfolders and JSON files
//        JSONArray folderDetails = new JSONArray();
//        updateMetadataFileRecursively(dataDirectory, folderDetails);
//
//        // Write the folder details to the metadata.json file
//        File metadataFile = Paths.get(dataDirectory.toString(), "metadata.json").toFile();
//        try (FileWriter fileWriter = new FileWriter(metadataFile)) {
//            fileWriter.write(folderDetails.toJSONString());
//        } catch (IOException e) {
//            // Handle the exception
//            e.printStackTrace();
//        }
//    }
//
//    // Recursive method to update metadata with subfolder details and JSON files
//    private void updateMetadataFileRecursively(File directory, JSONArray folderDetails) {
//        File[] folders = directory.listFiles(File::isDirectory);
//
//        if (folders != null) {
//            for (File folder : folders) {
//                JSONObject folderInfo = new JSONObject();
//                folderInfo.put("name", folder.getName());
//                // Add other folder details as needed
//
//                // Collect JSON files in this folder
//                JSONArray jsonFiles = new JSONArray();
//                File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
//                if (files != null) {
//                    for (File file : files) {
//                        jsonFiles.add(file.getName());
//                    }
//                }
//                folderInfo.put("jsonFiles", jsonFiles);
//
//                // Recursively add subfolder details and JSON files
//                JSONArray subfolderDetails = new JSONArray();
//                updateMetadataFileRecursively(folder, subfolderDetails);
//                folderInfo.put("subfolders", subfolderDetails);
//
//                // Add the folder info to the JSON array
//                folderDetails.add(folderInfo);
//            }
//        }
//    }


    private void updateMetadataFile(String path) {
        // Get a list of all directories in the Data folder
        File dataDirectory = Paths.get(gitRepoPath, path).toFile();
        if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
            // Handle the case when the Data folder does not exist
            return;
        }

        // Create a JSON array to store folder details, including subfolders and JSON files
        JSONArray folderDetails = new JSONArray();
        updateMetadataFileRecursively(dataDirectory, folderDetails);

        // Write the folder details to the metadata.json file
        File metadataFile = Paths.get(dataDirectory.toString(), "metadata.json").toFile();
        try (FileWriter fileWriter = new FileWriter(metadataFile)) {
            fileWriter.write(folderDetails.toJSONString());
        } catch (IOException e) {
            // Handle the exception
            e.printStackTrace();
        }
    }


    private void updateMetadataFileRecursively(File directory, JSONArray folderDetails) {
        File[] folders = directory.listFiles(file -> file.isDirectory() && !file.getName().startsWith("."));

        if (folders != null) {
            for (File folder : folders) {
                // Use a LinkedHashMap to maintain field order
                Map<String, Object> folderInfo = new LinkedHashMap<>();
                folderInfo.put("name", folder.getName());

                Map<String, Object> displayName = new LinkedHashMap<>();
                displayName.put("en-US", "Apollo");
                displayName.put("en-UK", "Account Dashboard");

                folderInfo.put("displayName", displayName);

                // Convert the LinkedHashMap to a JSONObject
                JSONObject folderInfoObject = new JSONObject(folderInfo);

                folderDetails.add(folderInfoObject);

                // Recursively add subfolder details and JSON files with data
                updateMetadataFileRecursively(folder, folderDetails);
            }
        }
    }


    public void createNewProjectFolder(String projectName) throws IOException {
        File dataDirectory = Paths.get(gitRepoPath, "Data").toFile();
        File projectFolder = Paths.get(dataDirectory.toString(), projectName).toFile();

        if (!projectFolder.exists()) {
            if (projectFolder.mkdirs()) {
                // You can also create a default JSON file if needed
            }
            updateMetadataFile("Data");
        }
    }



//    public String listFoldersInGitHubDirectory(String owner, String repo, String path) {
//        String branchName = "main"; // Adjust the branch name if necessary
//        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, path);
//        System.out.println(url);
//        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//        if (response.getStatusCode().is2xxSuccessful()) {
//            System.out.println("Yes");
//            // Parse the response JSON to extract folder names
//            String responseBody = response.getBody(); // Get the response content as a String
//
//            try {
//                JSONArray responseArray = (JSONArray) new JSONParser().parse(responseBody);
//
//                List<String> folderNames = new ArrayList<>();
//                for (int i = 0; i < responseArray.size(); i++) {
//                    JSONObject item = (JSONObject) responseArray.get(i);
//                    if (item.get("type").equals("dir")) {
//                        folderNames.add(item.get("name").toString());
//                    }
//                }
//                return String.join(", ", folderNames);
//            } catch (ParseException e) {
//                // Handle parsing errors
//                return "Error parsing JSON response: " + e.getMessage();
//            }
//        } else {
//            // Handle the error or log the response status and body
//            return "Error: " + response.getStatusCode() + " - " + response.getBody();
//        }
//    }






// ...

    public String listFoldersInGitHubDirectory(String owner, String repo, String path) {
        String branchName = "main"; // Adjust the branch name if necessary
        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, path);
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Yes");
            // Parse the response JSON to extract folder names
            String responseBody = response.getBody(); // Get the response content as a String

            try {
                JSONArray responseArray = (JSONArray) new JSONParser().parse(responseBody);

                JSONObject folderJson = new JSONObject(); // Create a JSON object to store folder and file data

                for (int i = 0; i < responseArray.size(); i++) {
                    JSONObject item = (JSONObject) responseArray.get(i);
                    if (item.get("type").equals("dir")) {
                        String folderName = item.get("name").toString();
                        String folderPath = item.get("path").toString(); // Path of the folder

                        // Call a method to fetch JSON files within this folder and store them in a JSONArray
                        JSONArray jsonFiles = getJsonFilesInFolder(owner, repo, folderPath);

                        // Add the folder name as the key and JSON files as the value in the folderJson
                        folderJson.put(folderName, jsonFiles);
                    }
                }
                return folderJson.toJSONString(); // Convert the JSON object to a JSON string
            } catch (ParseException e) {
                // Handle parsing errors
                return "Error parsing JSON response: " + e.getMessage();
            }
        } else {
            // Handle the error or log the response status and body
            return "Error: " + response.getStatusCode() + " - " + response.getBody();
        }
    }

    // Helper method to fetch JSON files within a folder
// Helper method to fetch JSON files within a folder
    private JSONArray getJsonFilesInFolder(String owner, String repo, String folderPath) {
        String branchName = "main"; // Adjust the branch name if necessary

        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, folderPath);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        JSONArray jsonFiles = new JSONArray();

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();

            try {
                JSONArray responseArray = (JSONArray) new JSONParser().parse(responseBody);

                for (int i = 0; i < responseArray.size(); i++) {
                    JSONObject item = (JSONObject) responseArray.get(i);
                    if (item.get("type").equals("file") && item.get("name").toString().toLowerCase().endsWith(".json")) {
                        // If the item is a JSON file (based on file extension), add its name to the jsonFiles array
                        jsonFiles.add(item.get("name"));
                    }
                }
            } catch (ParseException e) {
                // Handle parsing errors
                e.printStackTrace();
            }
        } else {
            // Handle the error or log the response status and body
            System.err.println("Error: " + response.getStatusCode() + " - " + response.getBody());
        }

        return jsonFiles;
    }






    // Get JSON data from a project's JSON file
    public String getJsonData(String projectName, String fileName) throws IOException {
        File projectDirectory = Paths.get(gitRepoPath, "project", projectName).toFile();
        File jsonFile = Paths.get(projectDirectory.toString(), fileName).toFile();

        // Implement code to read and return JSON data from the jsonFile

        return null; // Replace with actual implementation
    }
}
