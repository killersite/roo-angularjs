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

/**
 * Sample of a command class. The command class is registered by the Roo shell following an
 * automatic classpath scan. You can provide simple user presentation-related logic in this
 * class. You can return any objects from each method, or use the logger directly if you'd
 * like to emit messages of different severity (and therefore different colours on 
 * non-Windows systems).
 * 
 * @since 1.1
 */
@Component // Use these Apache Felix annotations to register your commands class in the Roo container
@Service
public class AngCommands implements CommandMarker { // All command types must implement the CommandMarker interface
    
    private static Logger LOGGER = HandlerUtils.getLogger(AngCommands.class);

    @Reference private ControllerOperations controllerOperations;
    @Reference private MetadataService metadataService;
    @Reference private ProjectOperations projectOperations;
    @Reference private TypeLocationService typeLocationService;

	/**
     * Get a reference to the AngOperations from the underlying OSGi container
     */
    @Reference private AngOperations operations;
    
    /**
     * This method is optional. It allows automatic command hiding in situations when the command should not be visible.
     * For example the 'entity' command will not be made available before the user has defined his persistence settings 
     * in the Roo shell or directly in the project.
     * 
     * You can define multiple methods annotated with {@link CliAvailabilityIndicator} if your commands have differing
     * visibility requirements.
     * 
     * @return true (default) if the command should be visible at this stage, false otherwise
     */
    @CliAvailabilityIndicator({ "web angularjs setup", "web angularjs all" })
    public boolean isCommandAvailable() {
        return operations.isCommandAvailable();
    }
    
    /**
     * TODO
add twitter bootstrap files
add items to pom.xml
	jackson
check & setup MVC requirements
setup spring MVC controllers for entities
setup main view index page
create partials for each persistant entity
	create form fields for each field
setup ang resources/routes for each entity

     */
    @CliCommand(value = "web angularjs all", help = "Setup or refresh the AngularJs view on all Entities")
    public void all(
    		@CliOption(key = "package", mandatory = true, optionContext = "update", help = "The package in which new controllers will be placed") final JavaPackage javaPackage
    		) {
//        operations.annotateAll();

        // FIXME: from mvc-all operation
        if (!javaPackage.getFullyQualifiedPackageName().startsWith(
                	projectOperations.getTopLevelPackage( projectOperations.getFocusedModuleName() ).getFullyQualifiedPackageName()
                )) {
            LOGGER.warning("Your controller was created outside of the project's top level package and is therefore not included in the preconfigured component scanning. Please adjust your component scanning manually in webmvc-config.xml");
        }
        
        // FIXME extract the relevant portions from this addon
        controllerOperations.generateAll(javaPackage);
    }
    
    /**
     * This method registers a command with the Roo shell. It has no command attribute.
     * 
     */
    @CliCommand(value = "web angularjs setup", help = "Initial setup of AngularJs dependancies")
    public void setup() {
        operations.setup();
    }
}