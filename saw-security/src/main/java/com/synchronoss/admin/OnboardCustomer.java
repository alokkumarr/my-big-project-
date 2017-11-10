package com.synchronoss.admin;


import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * Created by pawan on 03/11/17.
 */


@ShellComponent
public class OnboardCustomer {

    @ShellMethod("Create user")
    public String userCreate(){
        return "Pawan";
    }
}
