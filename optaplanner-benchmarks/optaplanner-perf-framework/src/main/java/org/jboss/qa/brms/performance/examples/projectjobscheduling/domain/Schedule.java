/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.brms.performance.examples.projectjobscheduling.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.jboss.qa.brms.performance.examples.common.domain.AbstractPersistable;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.domain.resource.Resource;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.persistence.xstream.api.score.buildin.bendable.BendableScoreXStreamConverter;

import java.util.List;

@PlanningSolution
@XStreamAlias("PjsSchedule")
public class Schedule extends AbstractPersistable {

    @ProblemFactCollectionProperty
    private List<Project> projectList;
    @ProblemFactCollectionProperty
    private List<Job> jobList;
    @ProblemFactCollectionProperty
    private List<ExecutionMode> executionModeList;
    @ProblemFactCollectionProperty
    private List<Resource> resourceList;
    @ProblemFactCollectionProperty
    private List<ResourceRequirement> resourceRequirementList;

    @PlanningEntityCollectionProperty
    private List<Allocation> allocationList;

    @XStreamConverter(value = BendableScoreXStreamConverter.class, ints = {1, 2})
    @PlanningScore(bendableHardLevelsSize = 1, bendableSoftLevelsSize = 2)
    private BendableScore score;

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    public List<ExecutionMode> getExecutionModeList() {
        return executionModeList;
    }

    public void setExecutionModeList(List<ExecutionMode> executionModeList) {
        this.executionModeList = executionModeList;
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    public List<ResourceRequirement> getResourceRequirementList() {
        return resourceRequirementList;
    }

    public void setResourceRequirementList(List<ResourceRequirement> resourceRequirementList) {
        this.resourceRequirementList = resourceRequirementList;
    }

    public List<Allocation> getAllocationList() {
        return allocationList;
    }

    public void setAllocationList(List<Allocation> allocationList) {
        this.allocationList = allocationList;
    }

    public BendableScore getScore() {
        return score;
    }

    public void setScore(BendableScore score) {
        this.score = score;
    }

}
