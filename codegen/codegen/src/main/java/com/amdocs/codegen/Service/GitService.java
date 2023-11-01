package com.amdocs.codegen.Service;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

    // Create a new project
//    public void createNewProject(String projectName) throws IOException {
//        File projectDirectory = Paths.get(gitRepoPath, "project", projectName).toFile();
//
//        if (!projectDirectory.exists()) {
//            if (projectDirectory.mkdirs()) {
//                try (Repository repository = FileRepositoryBuilder.create(new File(projectDirectory, ".git"))) {
//                    repository.create();
//                }
//            }
//        }
//    }

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
    }


//    public void createNewProject(String projectName) throws IOException {
//        File dataDirectory = Paths.get(gitRepoPath, "Data").toFile();
//        File jsonFile = Paths.get(dataDirectory.toString(), projectName + ".json").toFile();
//
//        if (!jsonFile.exists()) {
//            if (jsonFile.createNewFile()) {
//                try (Repository repository = FileRepositoryBuilder.create(new File(dataDirectory, ".git"))) {
//                    repository.create();
//                }
//            }
//        }
//    }
//
//    public void createNewProject(String projectName) throws IOException {
//        System.out.println("In create");
//        File projectDirectory = Paths.get(gitRepoPath, "Data", projectName).toFile();
//
//        if (!projectDirectory.exists()) {
//            if (projectDirectory.mkdirs()) {
//                // Create an empty JSON file with the same name as the project
//                File jsonFile = Paths.get(projectDirectory.toString(), projectName + ".json").toFile();
//                if (jsonFile.createNewFile()) {
//                    try (Repository repository = FileRepositoryBuilder.create(new File(projectDirectory, ".git"))) {
//                        repository.create();
//                    }
//                }
//            }
//        }
//    }

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


    // Create a project-specific folder in the "Data" directory
    public void createNewProjectFolder(String projectName) throws IOException {
        File dataDirectory = Paths.get(gitRepoPath, "Data").toFile();
        File projectFolder = Paths.get(dataDirectory.toString(), projectName).toFile();

        if (!projectFolder.exists()) {
            if (projectFolder.mkdirs()) {
                // You can also create a default JSON file if needed
            }
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


//    public String listFoldersInGitHubDirectory(String owner, String repo, String path) {
//        String branchName = "main"; // Adjust the branch name if necessary
//        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, path);
//
//        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//        if (response.getStatusCode().is2xxSuccessful()) {
//            // Parse the response JSON to extract folder names
//            JSONArray responseArray = new JSONArray();
//
//            List<String> folderNames = new ArrayList<>();
//            for (int i = 0; i < responseArray.size(); i++) {
//                JSONObject item = responseArray.getJSONObject(i);
//                if (item.has("type") && item.getString("type").equals("dir")) {
//                    folderNames.add(item.getString("name"));
//                }
//            }
//            return String.join(", ", folderNames);
//        } else {
//            // Handle the error or log the response status and body
//            return "Error: " + response.getStatusCode() + " - " + response.getBody();
//        }
//    }









    // Save JSON data to a project's JSON file
//    public void saveJsonData(String projectName, String fileName, String jsonData) throws Exception {
//        File projectDirectory = Paths.get(gitRepoPath, "project", projectName).toFile();
//        File jsonFile = Paths.get(projectDirectory.toString(), fileName).toFile();
//
//        // Implement code to write jsonData to the jsonFile
//    }

    // Get JSON data from a project's JSON file
    public String getJsonData(String projectName, String fileName) throws IOException {
        File projectDirectory = Paths.get(gitRepoPath, "project", projectName).toFile();
        File jsonFile = Paths.get(projectDirectory.toString(), fileName).toFile();

        // Implement code to read and return JSON data from the jsonFile

        return null; // Replace with actual implementation
    }
}
