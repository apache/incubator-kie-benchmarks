package org.jbpm.test.performance.scenario;

import org.jbpm.test.performance.jbpm.constant.UserStorage;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class PrepareEngine {

    public static List<Long> createNewTasks(boolean start, int count, TaskService taskService, String runtimeManagerIdentifier) {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { deploymentId = '" + runtimeManagerIdentifier + "' } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('" + UserStorage.PerfUser.getUserId()
                + "')], businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'perf-sample-task')] })";
        List<Long> taskIds = new ArrayList<Long>();
        while (count > 0) {
            Task task = TaskFactory.evalTask(new StringReader(str));
            long taskId = taskService.addTask(task, null);
            taskIds.add(taskId);
            if (start) {
                taskService.start(taskId, UserStorage.PerfUser.getUserId());
            }
            count--;
        }
        return taskIds;
    }

}
