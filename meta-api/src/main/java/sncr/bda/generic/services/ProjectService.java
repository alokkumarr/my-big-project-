package sncr.bda.generic.services;

import com.google.gson.JsonElement;
import org.apache.log4j.Logger;
import org.ojai.Document;
import sncr.bda.metastore.ProjectStore;

public class ProjectService {

    private static final Logger logger = Logger.getLogger(ProjectService.class);
    private ProjectStore prStore = null;


    public ProjectService(String prRoot){
        try {
            prStore = new ProjectStore(prRoot);
        }
        catch(Exception e){
            logger.error("Could not initialize project store object: ", e);
            prStore = null;
        }
    }

    public int createProject(String name, String src){
        try {
            prStore.createProjectRecord(name, src);
            return 0;
        } catch (Exception e) {
            logger.error("Could not create projects record: ", e);
            return -1;
        }
    }

    public int updateProject(String name, String src){
        try {
            prStore.updateProjectRecord(name, src);
            return 0;
        } catch (Exception e) {
            logger.error("Could not create projects record: ", e);
            return -1;
        }
    }

    public String readProject(String name){

        if (prStore == null) return "";
        if (name == null || name.isEmpty()){
            logger.error("Project name is empty.");
            return "";
        }
        try {
            Document prj = prStore.getTable().findById(name);
            return prj.asJsonString();
        } catch (Exception e) {
            logger.error("Could not read project data: ", e);
        }
        return "";
    }



}
