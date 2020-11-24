package org.jbpm.test.performance.jbpm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dashbuilder.DataSetCore;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.AdvanceRuntimeDataService;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.test.performance.jbpm.services.CustomIdentityProvider;
import org.jbpm.test.performance.jbpm.services.CustomUserGroupCallback;
import org.jbpm.test.services.AbstractKieServicesTest;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.test.util.db.DataSourceFactory;
import org.kie.test.util.db.PoolingDataSourceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JBPMKieServicesController extends AbstractKieServicesTest {

    protected static final Logger log = LoggerFactory.getLogger(JBPMKieServicesController.class);

    protected static final String ARTIFACT_ID = "performance-module";
    protected static final String GROUP_ID = "org.jbpm.performance";
    protected static final String VERSION = "1.0.0";
    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";

    protected List<String> processDefinitionFiles = new ArrayList<>();

    public static final String DEPLOYMENT_ID = GROUP_ID + ":" + ARTIFACT_ID + ":" + VERSION;

    private static JBPMKieServicesController instance;

    private JBPMKieServicesController(List<String> processes, String puName) throws Exception {
        super(new CustomIdentityProvider(), new CustomUserGroupCallback("classpath:/usergroups.properties"));
        processDefinitionFiles.addAll(processes);
        setPuName(puName);
        super.setUp();
    }

    public static JBPMKieServicesController getInstance(List<String> processes, String puName) throws Exception {
        if (instance == null) {
            instance = new JBPMKieServicesController(processes, puName);
        }
        return instance;
    }

    public static JBPMKieServicesController getInstance(List<String> processes) throws Exception {
        return getInstance(processes, null);
    }

    private RuntimeStrategy getRuntimeStrategy(JBPMController.Strategy strategy){
        switch (strategy){
            case SINGLETON:
                return RuntimeStrategy.SINGLETON;
            case PERREQUEST:
                return RuntimeStrategy.PER_REQUEST;
            case PERPROCESSINSTANCE:
                return RuntimeStrategy.PER_PROCESS_INSTANCE;
            default:
                return RuntimeStrategy.PER_PROCESS_INSTANCE;
        }
    }

    @Override
    public DeploymentUnit prepareDeploymentUnit() throws Exception {
        return createAndDeployUnit(GROUP_ID, ARTIFACT_ID, VERSION);
    }

    @Override
    protected List<String> getProcessDefinitionFiles() {
        return processDefinitionFiles;
    }

    public void tearDown(List<Long> processIds) {
        abortProcessInstances(processIds);
        undeployProcess();
    }

    public void abortProcessInstances(List<Long> processIds) {
        for (Long processInstanceId : processIds) {
            try {
                // let's abort process instance to leave the system in clear state
                processService.abortProcessInstance(processInstanceId);
            } catch (ProcessInstanceNotFoundException e) {
                // ignore it as it was already completed/aborted
            }
        }
    }

    public void undeployProcess() {
        DataSetCore.set(null);
        super.tearDown();
    }

    @Override
    protected void buildDatasource() {
        this.ds = setupPoolingDataSource();
    }

    /**
     * This sets up a PoolingDataSourceWrapper of a Tomcat DBCP 2 BasicManagedDataSource with Narayana
     * used as a transaction manager.
     *
     * @return PoolingDataSourceWrapper that represents the datasource
     */
    protected PoolingDataSourceWrapper setupPoolingDataSource() {
        Properties dsProps = getDatasourceProperties();
        // We cannot use url directly because of sql-maven-plugin, since url Maven property always contains project url
        dsProps.put("url", dsProps.getProperty("connectionUrl"));
        return DataSourceFactory.setupPoolingDataSource(getJndiDatasourceName(), dsProps, dsProps);
    }

    /**
     * Return the default database/datasource properties - These properties use
     * an in-memory H2 database
     *
     * @return Properties containing the default properties
     */
    protected static Properties getDefaultProperties() {
        Properties defaultProperties;
        String[] keyArr = { "serverName", "portNumber", "databaseName", "connectionUrl", "user", "password", "driverClassName",
                "className", "initialSize", "minIdle", "maxTotal" };
        String[] defaultPropArr = { "", "", "", "jdbc:h2:mem:test;MVCC=true", "sa", "", "org.h2.Driver",
                "org.h2.jdbcx.JdbcDataSource", "16", "16", "16" };
        if (keyArr.length != defaultPropArr.length) {
            throw new RuntimeException("Unequal number of keys for default properties");
        }
        defaultProperties = new Properties();
        for (int i = 0; i < keyArr.length; ++i) {
            defaultProperties.put(keyArr[i], defaultPropArr[i]);
        }

        return defaultProperties;
    }

    /**
     * This reads in the (maven filtered) datasource properties from the
     * resource directory.
     *
     * @return Properties containing the datasource properties.
     */
    protected static Properties getDatasourceProperties() {
        String propertiesNotFoundMessage = "Unable to load datasource properties [" + DATASOURCE_PROPERTIES + "]";
        boolean propertiesNotLoaded = false;

        // Central place to set additional H2 properties
        System.setProperty("h2.lobInDatabase", "true");

        InputStream propsInputStream = JBPMController.class.getResourceAsStream(DATASOURCE_PROPERTIES);
        if (propsInputStream == null) {
            throw new RuntimeException(propertiesNotFoundMessage);
        }
        Properties props = new Properties();
        try {
            props.load(propsInputStream);
        } catch (IOException ioe) {
            propertiesNotLoaded = true;
            log.warn("Unable to load properties, using default H2 properties: {}", ioe.getMessage());
            log.warn("Stacktrace:", ioe);
        }

        String password = props.getProperty("password");
        if ("${maven.jdbc.password}".equals(password) || propertiesNotLoaded) {
            props = getDefaultProperties();
        }

        return props;
    }

    @Override
    protected DeploymentUnit createDeploymentUnit(String groupId, String artifactid, String version) throws Exception {
        DeploymentUnit unit = super.createDeploymentUnit(groupId, artifactid, version);
        ((KModuleDeploymentUnit) unit).setStrategy(
                getRuntimeStrategy(JBPMController.Strategy.valueOf(JBPMTestConfig.getInstance().getRuntimeManagerStrategy().toUpperCase())));
        return unit;
    }

    @Override
    protected boolean createDescriptor() {
        return true;
    }

    @Override
    protected List<NamedObjectModel> getEnvironmentEntries() {
        List<NamedObjectModel> environmentEntries = super.getEnvironmentEntries();
        environmentEntries.add(new NamedObjectModel(EnvironmentName.USE_PESSIMISTIC_LOCKING, "java.lang.Boolean", new Object[]{String.valueOf(JBPMTestConfig.getInstance()
                .isPessimisticLocking())}));
        return environmentEntries;
    }

    public ProcessService getProcessService(){
        return this.processService;
    }

    public DefinitionService getDefinitionService(){
        return this.bpmn2Service;
    }

    public RuntimeDataService getRuntimeDataService(){
        return this.runtimeDataService;
    }

    public DeploymentUnit getDeploymentUnit(){
        return this.deploymentUnit;
    }

    public UserTaskService getUserTaskService(){
        return this.userTaskService;
    }

    public AdvanceRuntimeDataService getAdvanceRuntimeDataService(){
        return this.advanceVariableDataService;
    }
}
