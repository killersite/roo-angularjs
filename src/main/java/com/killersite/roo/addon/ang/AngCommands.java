package com.killersite.roo.addon.ang;

import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.addon.web.mvc.controller.ControllerOperations;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaPackage;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;
import org.springframework.roo.support.logging.HandlerUtils;

import static org.springframework.roo.shell.OptionContexts.UPDATE;

@Component // Use these Apache Felix annotations to register your commands class in the Roo container
@Service
public class AngCommands implements CommandMarker { // All command types must implement the CommandMarker interface

    private static Logger LOGGER = HandlerUtils.getLogger(AngCommands.class);

    @Reference
    private ControllerOperations controllerOperations;
    @Reference
    private MetadataService metadataService;
    @Reference
    private ProjectOperations projectOperations;
    @Reference
    private TypeLocationService typeLocationService;
    @Reference
    AngOperations angOperations;

    @CliAvailabilityIndicator({"angular setup", "angular all", "angular test"})
    public boolean isCommandAvailable() {
        return angOperations.isAngularInstallationPossible();
    }

    @CliCommand(value = "angular test", help = "testing command for angular add-on")
    public void test(
            @CliOption(key = "package",
                    mandatory = false,
                    optionContext = UPDATE,
                    help = "The package in which new controllers will be placed") final JavaPackage javaPackage) {
        // the project's POM needs the dependency for the Annotations
        angOperations.addThisAddonDependancyToPom();

        //json all
        angOperations.annotateAllJpaWithRooJson();

        //web mvc json setup
        angOperations.doMvcJsonSetup();

        //web mvc json all --package ~.web
        angOperations.doMvcJsonAll(javaPackage);

        // annotate all @controllers
        angOperations.annotateAll();

        angOperations.setupAngularFiles();
    }

    @CliCommand(value = "angular all", help = "Generate Route, Model, Controller, and View for all Persistent Entities")
    public void all() {
    }

    @CliCommand(value = "angular setup", help = "Initial setup of AngularJs dependencies and files")
    public void setup() {
    }

    @CliCommand(value = "angular entity", help = "Generate Angular resources for specified Entity")
    public void addEntity() {
    }

    @CliCommand(value = "angular controller", help = "Generate a stub controller, route, and view")
    public void addController() {
    }

    @CliCommand(value = "angular route", help = "Generate a stub route file")
    public void addRoute() {
    }

    @CliCommand(value = "angular service")
    public void addService() {
    }

}

//~~~~~~~~~~~~~~~~~~~~~~~~
//~~~ SAMPLE CODE ~~~~~~~~

// generates controllers from all persistant entities
//        controllerOperations.generateAll(javaPackage);
