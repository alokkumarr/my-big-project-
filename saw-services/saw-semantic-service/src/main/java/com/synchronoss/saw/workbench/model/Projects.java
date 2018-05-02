
package com.synchronoss.saw.workbench.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Projects {

    private List<Project> projects;

    /**
     * 
     * (Required)
     * 
     */
    public List<Project> getProjects() {
        return projects;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setProxy(List<Project> projects) {
        this.projects = projects;
    }


}
