package org.jbpm.test.performance.scenario.load;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.UserStorage;
import org.jbpm.services.task.audit.TaskAuditServiceFactory;
import org.jbpm.services.task.audit.service.TaskAuditService;
import org.jbpm.services.task.audit.service.TaskAuditServiceImpl;
import org.kie.api.task.TaskService;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;
import org.kie.perf.SharedMetricRegistry;
import org.kie.perf.annotation.KPKLimit;
import org.kie.perf.scenario.IPerfTest;
import org.jbpm.test.performance.scenario.PrepareEngine;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

@KPKLimit(1000)
public class L1000HumanTasksQueryPagination implements IPerfTest {

    private JBPMController jc;

    private TaskService taskService;

    private List<AuditTask> tasks = new ArrayList<AuditTask>();

    @Override
    public void init() {
        jc = JBPMController.getInstance();
        jc.createRuntimeManager();

        taskService = jc.getRuntimeEngine().getTaskService();

        PrepareEngine.createNewTasks(false, 1000, taskService, jc.getRuntimeManagerIdentifier());
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        metrics.register(MetricRegistry.name(L1000HumanTasksQueryPagination.class, "scenario.tasks.query.page.size"), new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return tasks.size();
            }
        });
    }

    @Override
    public void execute() {
        TaskAuditService taskAuditService = TaskAuditServiceFactory.newTaskAuditServiceConfigurator().setTaskService(taskService).getTaskAuditService();
        tasks = taskAuditService.getAllAuditTasksByUser(UserStorage.PerfUser.getUserId(), new QueryFilter(0, 100));
    }

    @Override
    public void close() {
        jc.tearDown();
    }

}
