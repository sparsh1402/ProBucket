package com.amdocs.codegen.Controller;

import com.amdocs.codegen.Service.GitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class ProjectController {

    @Autowired
    private GitService gitService;

    // Get all project names
//    @GetMapping("/ALL")
//    public List<String> getAllProjects() {
//        List<String> projectNames = gitService.getAllProjects();
//        return projectNames;
//    }

    @GetMapping("/All")
    public String readGitHubData() {
//        String owner = "Pratyush2k1";
//        String repo = "DemoCode";
//        String path = "Projects/Airtel/theory.json";
        String owner = "sparsh1402";
        String repo = "codegenDatabase";
        String path = "Data";
        return gitService.listFoldersInGitHubDirectory(owner, repo, path);
//        return gitService.readJsonFileFromGitHub(owner,repo,path);
    }

    // Create a new project
    @PostMapping("/{projectName}/{fileName}/new")
    public String createFile(@PathVariable String projectName , @PathVariable String fileName) {
        try {
            gitService.createNewProject(projectName,fileName);
            return "Project '" + projectName + "' created successfully.";
        } catch (IOException e) {
            return "Failed to create project: " + e.getMessage();
        }
    }

    @PostMapping("/{projectName}/new")
    public String createNewProject(@PathVariable String projectName) {
        try {
            gitService.createNewProjectFolder(projectName);
            return "Project '" + projectName + "' folder created successfully.";
        } catch (IOException e) {
            return "Failed to create project folder: " + e.getMessage();
        }
    }

    // Post data to a project
    @PostMapping("/{projectName}/{fileName}/canvas")
    public String postDataToProject(
            @PathVariable String projectName,
            @PathVariable String fileName,
            @RequestBody String jsonData
    ) {
        System.out.println(jsonData);
        try {
            gitService.saveJsonData(projectName,fileName, jsonData);
            return "Data posted to project '" + projectName + "' successfully.";
        } catch (Exception e) {
            return "Failed to post data: " + e.getMessage();
        }
    }

    // Get data from a project by name
//    @GetMapping("/{projectName}")
//    public String getProjectByName(@PathVariable String projectName) {
//        try {
//            String jsonData = gitService.getJsonData(projectName, "data.json");
//            if (jsonData != null) {
//                return jsonData;
//            } else {
//                return "Data not found for project '" + projectName + "'.";
//            }
//        } catch (IOException e) {
//            return "Failed to retrieve data: " + e.getMessage();
//        }
//    }
}
