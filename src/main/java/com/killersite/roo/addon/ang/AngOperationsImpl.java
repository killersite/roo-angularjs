package com.killersite.roo.addon.ang;

//import freemarker.template.Template;
//import freemarker.template.TemplateException;
//import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.addon.json.JsonOperations;
import org.springframework.roo.addon.web.mvc.controller.WebMvcOperations;
import org.springframework.roo.addon.web.mvc.controller.json.WebJsonOperations;
import org.springframework.roo.addon.web.mvc.jsp.JspOperations;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.classpath.TypeManagementService;
import org.springframework.roo.classpath.details.MemberFindingUtils;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetailsBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.operations.AbstractOperations;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaPackage;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.*;
import org.springframework.roo.support.osgi.OSGiUtils;
import org.springframework.roo.support.util.FileUtils;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.roo.model.RooJavaType.*;
import static org.springframework.roo.model.SpringJavaType.*;

/**
 * Implementation of angOperations this add-on offers.
 *
 * @since 1.1
 */
@Component // Use these Apache Felix annotations to register your commands class in the Roo container
@Service
public class AngOperationsImpl extends AbstractOperations implements AngOperations {

    private static final String WEB_XML = "WEB-INF/web.xml";
    private static final String WEB_APP_XPATH = "/web-app/";
    private static final String WHITESPACE = "[ \t\r\n]";

    @Reference private WebMvcOperations webMvcOperations;
    @Reference private JspOperations jspOperations;
    @Reference private JsonOperations jsonOperations;
    @Reference private WebJsonOperations webJsonOperations;
    @Reference private PathResolver pathResolver;
    @Reference private FileManager fileManager;
    @Reference private MetadataService metadataService;

    protected ComponentContext context;

    /**
     * Use ProjectOperations to install new dependencies, plugins, properties, etc into the project configuration
     */
    @Reference ProjectOperations projectOperations;

    /**
     * Use TypeLocationService to find types which are annotated with a given annotation in the project
     */
    @Reference private TypeLocationService typeLocationService;
    
    /**
     * Use TypeManagementService to change types
     */
    @Reference private TypeManagementService typeManagementService;

    /** {@inheritDoc} */
    @Override
    public boolean isAngularInstallationPossible () {
        // Check if a project has been created
        return projectOperations.isFocusedProjectAvailable()
        		&& jspOperations.isMvcInstallationPossible()
        		// FIXME check that MVC addon is available
//                && projectOperations.isFeatureInstalledInFocusedModule(FeatureNames.MVC)
                ;
    }

    @Override
    public void annotateAllJpaWithRooJson() {
        if (jsonOperations.isJsonInstallationPossible()) {
            for (final JavaType type : typeLocationService.findTypesWithAnnotation(ROO_JAVA_BEAN)) {
                jsonOperations.annotateAll(false, false);
//                annotateType(type);
            }
        }
    }

    public void annotateJpaWithRooJson(JavaType type) {
        jsonOperations.annotateType(type, "", false, false);
    }

    @Override
    public void doMvcJsonAll(JavaPackage javaPackage) {
        if (javaPackage == null) {
            javaPackage = projectOperations
                    .getTopLevelPackage(projectOperations
                            .getFocusedModuleName());
        }
        webJsonOperations.annotateAll(javaPackage);
    }

    @Override
    public void doMvcJsonSetup() {
        if (webJsonOperations.isWebJsonInstallationPossible()) {
            webJsonOperations.setup();
        }
    }

    private void updateRepositories() {
        // Parse the configuration.xml file
        final Element configuration = XmlUtils.getConfiguration(getClass());

        final List<Repository> repositories = new ArrayList<Repository>();

        final List<Element> jpaRepositories = XmlUtils
                .findElements(
                        "/configuration/persistence/provider[@id='JPA']/repositories/repository",
                        configuration);
        for (final Element repositoryElement : jpaRepositories) {
            repositories.add(new Repository(repositoryElement));
        }

        // Add all new repositories to pom.xml
        String moduleName = projectOperations.getFocusedModuleName();
        projectOperations.addRepositories(moduleName, repositories);
    }

    private void updateDependencies() {
        // Parse the configuration.xml file
        final Element configuration = XmlUtils.getConfiguration(getClass());

        final List<Dependency> requiredDependencies = new ArrayList<Dependency>();

        final List<Element> springDependencies = XmlUtils.findElements(
                "/configuration/spring/dependencies/dependency", configuration);
        for (final Element dependencyElement : springDependencies) {
            requiredDependencies.add(new Dependency(dependencyElement));
        }

        // Update the POM
        String moduleName = projectOperations.getFocusedModuleName();
        projectOperations.addDependencies(moduleName, requiredDependencies);
    }

    @Override
    public void addThisAddonDependancyToPom() {
        List<Dependency> dependencies = new ArrayList<Dependency>();

        // Install the dependency on the add-on jar (
        dependencies.add(new Dependency("com.killersite.roo.addon.ang", "com.killersite.roo.addon.ang", "0.1.0.BUILD-SNAPSHOT", DependencyType.JAR, DependencyScope.PROVIDED));

        // Install dependencies defined in external XML file
//        for (Element dependencyElement : XmlUtils.findElements("/configuration/batch/dependencies/dependency", XmlUtils.getConfiguration(getClass()))) {
//            dependencies.add(new Dependency(dependencyElement));
//        }

        // Add all new dependencies to pom.xml
        String moduleName = projectOperations.getFocusedModuleName();
        projectOperations.addDependencies(moduleName, dependencies);
    }

    /** {@inheritDoc} */
    @Override
    public void annotateAll() {
        // Use the TypeLocationService to scan project for all types with a specific annotation
        for (final JavaType type: typeLocationService.findTypesWithAnnotation(CONTROLLER, ROO_WEB_JSON)) {
            annotateType(type);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void annotateType(JavaType javaType) {
        // Use Roo's Assert type for null checks
        Validate.notNull(javaType, "Java type required");

        // Obtain ClassOrInterfaceTypeDetails for this java type
        ClassOrInterfaceTypeDetails existing = typeLocationService.getTypeDetails(javaType);

        // Test if the annotation already exists on the target type
        if (existing != null && MemberFindingUtils.getAnnotationOfType(existing.getAnnotations(), new JavaType(AngularEndpoint.class.getName())) == null) {

            ClassOrInterfaceTypeDetailsBuilder classOrInterfaceTypeDetailsBuilder = new ClassOrInterfaceTypeDetailsBuilder(existing);
            
            // Create JavaType instance for the add-ons trigger annotation
            JavaType rooRooAng = new JavaType(AngularEndpoint.class.getName());

            // Create Annotation metadata
            AnnotationMetadataBuilder annotationBuilder = new AnnotationMetadataBuilder(rooRooAng);
            
            // Add annotation to target type
            classOrInterfaceTypeDetailsBuilder.addAnnotation(annotationBuilder.build());
            
            // Save changes to disk
            typeManagementService.createOrUpdateTypeOnDisk(classOrInterfaceTypeDetailsBuilder.build());
        }
    }

    public void installAllWebMvcArtifacts() {
//        installMinimalWebArtifacts();
//        manageWebXml();
//        updateConfiguration();
    }

    private boolean isProjectAvailable() {
        return projectOperations.isFocusedProjectAvailable();
    }

    /** {@inheritDoc} */
    @Override
    public void setupAngularFiles() {
        // Use PathResolver to get canonical resource names for a given artifact
//        PathResolver pathResolver = projectOperations.getPathResolver();

        // this just sets up the MVC
//        jspOperations.installCommonViewArtefacts();

        Validate.isTrue(isProjectAvailable(), "Project metadata required");
        String moduleName = projectOperations.getFocusedModuleName();
        final LogicalPath webappPath = Path.SRC_MAIN_WEBAPP.getModulePathId(moduleName);

        // Install styles
        copyDirectoryContents("scripts/*.*", pathResolver.getIdentifier(webappPath, "scripts"), false);
        copyDirectoryContents("scripts/directives/*.*", pathResolver.getIdentifier(webappPath, "scripts/directives"), false);
        copyDirectoryContents("scripts/model/*.*", pathResolver.getIdentifier(webappPath, "scripts/model"), false);
        copyDirectoryContents("scripts/pages/homepage/*.*", pathResolver.getIdentifier(webappPath, "scripts/pages/homepage"), false);
        copyDirectoryContents("scripts/pages/login/*.*", pathResolver.getIdentifier(webappPath, "scripts/pages/login"), false);
        copyDirectoryContents("scripts/services/*.*", pathResolver.getIdentifier(webappPath, "scripts/services"), false);

/*
    	// from mvc-addon. setup all artifacts and listeners
    	webMvcOperations.installAllWebMvcArtifacts();

        // Install the add-on Google code repository needed to get the annotation
        projectOperations.addRepository("", new Repository("Ang Roo add-on repository", "Ang Roo add-on repository", "https://angularjs-roo.googlecode.com/svn/repo"));

        List<Dependency> dependencies = new ArrayList<Dependency>();

        // Install the dependency on the add-on jar (
        dependencies.add(
            new Dependency("com.killersite.roo.addon.ang", "com.killersite.roo.addon.ang", "0.1.0.BUILD-SNAPSHOT", DependencyType.JAR, DependencyScope.PROVIDED));

        // Install dependencies defined in external XML file
        for (Element dependencyElement : XmlUtils.findElements("/configuration/batch/dependencies/dependency", XmlUtils.getConfiguration(getClass()))) {
            dependencies.add(new Dependency(dependencyElement));
        }

        // Add all new dependencies to pom.xml
        projectOperations.addDependencies("", dependencies);
 */
    }

    /**
     * to use on valid XML documents
     */
    private void testXmlTemplate() {
        Document document;
        try {
            document = getDocumentTemplate("index-template.jspx");
            XmlUtils.findRequiredElement("/div/message",
                    document.getDocumentElement()).setAttribute("code", "label");
        } catch (final Exception e) {
            throw new IllegalStateException("Encountered an error during copying of resources for controller class.", e);
        }

        final String viewFile = pathResolver.getFocusedIdentifier(
                Path.SRC_MAIN_WEBAPP, "WEB-INF/test.jspx"
        );
        fileManager.createOrUpdateTextFileIfRequired(viewFile, XmlUtils.nodeToString(document), false);
    }

    /**
     * might be easier then XML. to use on text files - CSS
     */
//    private static void freemarkerTest() {
//        //Freemarker configuration object
//        freemarker.template.Configuration cfg = new freemarker.template.Configuration();
//        try {
//            //Load template from source folder
//            Template template = cfg.getTemplate("src/helloworld.ftl");
//
//            // Build the data-model
//            Map<String, Object> data = new HashMap<String, Object>();
//            data.put("message", "Hello World!");
//
//            //List parsing
//            List<String> countries = new ArrayList<String>();
//            countries.add("India");
//            countries.add("United States");
//            countries.add("Germany");
//            countries.add("France");
//
//            data.put("countries", countries);
//
//            // Console output
//            Writer out = new OutputStreamWriter(System.out);
//            template.process(data, out);
//            out.flush();
//
//            // File output
//            Writer file = new FileWriter(new File("C:\\FTL_helloworld.txt"));
//            template.process(data, file);
//            file.flush();
//            file.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TemplateException e) {
//            e.printStackTrace();
//        }
//    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~ following for manipulating XML doc ~~~~~~~~~~~~~~~~~~~~

    /**
     * make url_patern go to /api
     */
    private void manageWebXml() {
        // Use PathResolver to get canonical resource names for a given artifact
        PathResolver pathResolver = projectOperations.getPathResolver();

        Validate.isTrue(projectOperations.isFocusedProjectAvailable(), "Project metadata required");

        // Verify that the web.xml already exists
        final String webXmlPath = pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, WEB_XML);
        Validate.isTrue(fileManager.exists(webXmlPath), "'%s' does not exist", webXmlPath);

        final Document document = XmlUtils.readXml(fileManager.getInputStream(webXmlPath));

        Element sessionConfigElement = XmlUtils.findFirstElement(WEB_APP_XPATH
                + "session-config", document.getDocumentElement());
        if (sessionConfigElement == null) {
            sessionConfigElement = document.createElement("session-config");
            insertBetween(sessionConfigElement, "servlet-mapping[last()]",
                    "welcome-file-list", document);
        }
//        WebXmlUtils.setSessionTimeout(10, document, null);

        fileManager.createOrUpdateTextFileIfRequired(webXmlPath,
                XmlUtils.nodeToString(document), false);
    }

    private static void insertBetween(final Element element,
                                      final String afterElementName, final String beforeElementName,
                                      final Document document) {
        final Element beforeElement = XmlUtils.findFirstElement(WEB_APP_XPATH
                + beforeElementName, document.getDocumentElement());
        if (beforeElement != null) {
            document.getDocumentElement().insertBefore(element, beforeElement);
            addLineBreakBefore(element, document);
            addLineBreakBefore(element, document);
            return;
        }

        final Element afterElement = XmlUtils.findFirstElement(WEB_APP_XPATH
                + afterElementName, document.getDocumentElement());
        if (afterElement != null && afterElement.getNextSibling() != null
                && afterElement.getNextSibling() instanceof Element) {
            document.getDocumentElement().insertBefore(element,
                    afterElement.getNextSibling());
            addLineBreakBefore(element, document);
            addLineBreakBefore(element, document);
            return;
        }

        document.getDocumentElement().appendChild(element);
        addLineBreakBefore(element, document);
        addLineBreakBefore(element, document);
    }

    private static void insertAfter(final Element element,
                                    final String afterElementName, final Document document) {
        final Element afterElement = XmlUtils.findFirstElement(WEB_APP_XPATH
                + afterElementName, document.getDocumentElement());
        if (afterElement != null && afterElement.getNextSibling() != null
                && afterElement.getNextSibling() instanceof Element) {
            document.getDocumentElement().insertBefore(element,
                    afterElement.getNextSibling());
            addLineBreakBefore(element, document);
            addLineBreakBefore(element, document);
            return;
        }
        document.getDocumentElement().appendChild(element);
        addLineBreakBefore(element, document);
        addLineBreakBefore(element, document);
    }
    private static void addLineBreakBefore(final Element element,
                                           final Document document) {
        document.getDocumentElement().insertBefore(
                document.createTextNode("\n    "), element);
    }
    /**
     * Adds the given child to the given parent if it's not already there
     *
     * @param parent the parent to which to add a child (required)
     * @param child the child to add if not present (required)
     */
    private static void appendChildIfNotPresent(final Node parent,
                                                final Element child) {
        final NodeList existingChildren = parent.getChildNodes();
        for (int i = 0; i < existingChildren.getLength(); i++) {
            final Node existingChild = existingChildren.item(i);
            if (existingChild instanceof Element) {
                // Attempt matching of possibly nested structures by using of
                // 'getTextContent' as 'isEqualNode' does not match due to line
                // returns, etc
                // Note, this does not work if child nodes are appearing in a
                // different order than expected
                if (existingChild.getNodeName().equals(child.getNodeName())
                        && existingChild
                        .getTextContent()
                        .replaceAll(WHITESPACE, "")
                        .trim()
                        .equals(child.getTextContent().replaceAll(
                                WHITESPACE, ""))) {
                    // If we found a match, there is no need to append the child
                    // element
                    return;
                }
            }
        }
        parent.appendChild(child);
    }

}