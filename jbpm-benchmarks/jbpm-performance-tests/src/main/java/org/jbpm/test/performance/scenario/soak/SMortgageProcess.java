package org.jbpm.test.performance.scenario.soak;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.kie.perf.scenario.IPerfTest;

import com.redhat.bpms.examples.mortgage.Applicant;
import com.redhat.bpms.examples.mortgage.Application;
import com.redhat.bpms.examples.mortgage.Appraisal;
import com.redhat.bpms.examples.mortgage.Property;

public class SMortgageProcess implements IPerfTest {
    
    private static final String RULES_LOCATION = "rules/mortgage/";

    private JBPMController jc;

    @Override
    public void init() {
        jc = JBPMController.getInstance();
        jc.clear();
        
        Map<String, ResourceType> res = new HashMap<String, ResourceType>();
        res.put(ProcessStorage.MortgageApplication.getPath(), ResourceType.BPMN2);
        res.put(RULES_LOCATION + "Jumbo Mortgage.rdrl", ResourceType.RDRL);
        res.put(RULES_LOCATION + "Low Down Payment based on Appraisal.rdrl", ResourceType.RDRL);
        res.put(RULES_LOCATION + "Low Down Payment before Appraisal.rdrl", ResourceType.RDRL);
        res.put(RULES_LOCATION + "Mortgage Calculation.drl", ResourceType.DRL);
        res.put(RULES_LOCATION + "Retract Facts After Calculation.drl", ResourceType.DRL);
        res.put(RULES_LOCATION + "Retract Facts After Validation.drl", ResourceType.DRL);
        res.put(RULES_LOCATION + "Validate Amortization.rdrl", ResourceType.RDRL);
        res.put(RULES_LOCATION + "Validate Down Payment.rdrl", ResourceType.RDRL);
        res.put(RULES_LOCATION + "Validate Income.rdrl", ResourceType.RDRL);
        res.put(RULES_LOCATION + "Validate Property Price.rdrl", ResourceType.RDRL);
        res.put(RULES_LOCATION + "Validate SSN.rdrl", ResourceType.RDRL);
        jc.createRuntimeManager(res);
    }

    @Override
    public void initMetrics() {

    }

    @Override
    public void execute() {
        RuntimeEngine runtimeEngine = jc.getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        Map<String, Object> params = getProcessArgs( "Amy", "12301 Wilshire", 333224449, 100000, 500000, 100000, 30 );
        ksession.startProcess(ProcessStorage.MortgageApplication.getProcessDefinitionId(), params);
    }

    @Override
    public void close() {
        jc.tearDown();
    }
    
    private static Map<String, Object> getProcessArgs(String name, String address, int ssn, int income, int price, int downPayment, int amortization)
    {
        Map<String, Object> processVariables = new HashMap<String, Object>();
        Applicant applicant = new Applicant( name, ssn, income, null );
        Property property = new Property( address, price );
        Appraisal appraisal = new Appraisal(property, new Date(System.currentTimeMillis()), price + 10000);
        Application application = new Application( applicant, property, appraisal, downPayment, amortization, null, null, null );
        processVariables.put( "application", application );
        return processVariables;
    }

}
