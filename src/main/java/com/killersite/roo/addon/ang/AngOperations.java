package com.killersite.roo.addon.ang;

import org.springframework.roo.model.JavaPackage;
import org.springframework.roo.model.JavaType;

/**
 * Interface of angOperations this add-on offers. Typically used by a command type or an external add-on.
 *
 * @since 1.1
 */
public interface AngOperations {

    /**
     * Indicate commands should be available
     * 
     * @return true if it should be available, otherwise false
     */
    boolean isAngularInstallationPossible();

    /**
     * annotate all entities with @RooJson
     */
    void annotateAllJpaWithRooJson();

    //    web mvc json setup
    void doMvcJsonAll(JavaPackage javaPackage);

    //    web mvc json all --package com.ex.web
    void doMvcJsonSetup();

    void addThisAddonDependancyToPom();

    /**
     * Annotate all JpaBeans with @Angular
     */
    void annotateAll();

    /**
     * annotate a JpaBean with @Anguar
     * @param javaType
     */
    void annotateType(JavaType javaType);
    
    /**
     * Setup all add-on artifacts (dependencies in this case)
     */
    void setupAngularFiles();

}