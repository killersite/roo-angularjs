package com.killersite.roo.addon.ang;

import org.springframework.roo.model.JavaType;

/**
 * Interface of operations this add-on offers. Typically used by a command type or an external add-on.
 *
 * @since 1.1
 */
public interface AngOperations {

    /**
     * Indicate commands should be available
     * 
     * @return true if it should be available, otherwise false
     */
    boolean isCommandAvailable();

    /**
     * Annotate all Java types with the trigger of this add-on
     */
    void annotateAll();
    
    /**
     * Setup all add-on artifacts (dependencies in this case)
     */
    void setup();
}