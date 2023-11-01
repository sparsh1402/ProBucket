package com.amdocs.codegen.Service;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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


    public String listFoldersInGitHubDirectory(String owner, String repo, String path) {
        String branchName = "main"; // Adjust the branch name if necessary
        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, path);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            // Parse the response JSON to extract folder names
            JSONArray responseArray = new JSONArray(response.getBody());
            List<String> folderNames = new ArrayList<>();
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject item = responseArray.getJSONObject(i);
                if (item.has("type") && item.getString("type").equals("dir")) {
                    folderNames.add(item.getString("name"));
                }
            }
            return String.join(", ", folderNames);
        } else {
            // Handle the error or log the response status and body
            return "Error: " + response.getStatusCode() + " - " + response.getBody();
        }
    }









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
