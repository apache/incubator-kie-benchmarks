/*
* This file is describing all the Jenkins jobs in the DSL format (see https://plugins.jenkins.io/job-dsl/)
* needed by the Kogito pipelines.
*
* The main part of Jenkins job generation is defined into the https://github.com/apache/incubator-kie-kogito-pipelines repository.
*
* This file is making use of shared libraries defined in
* https://github.com/apache/incubator-kie-kogito-pipelines/tree/main/dsl/seed/src/main/groovy/org/kie/jenkins/jobdsl.
*/

import org.kie.jenkins.jobdsl.model.JobType
import org.kie.jenkins.jobdsl.utils.JobParamsUtils
import org.kie.jenkins.jobdsl.KogitoJobTemplate
import org.kie.jenkins.jobdsl.Utils

jenkins_path = '.ci/jenkins'

///////////////////////////////////////////////////////////////////////////////////////////
// kie-benchmarks jobs
///////////////////////////////////////////////////////////////////////////////////////////

// update drools/optaplanner version in kie-benchmarks
setupUpdateDependencyJob('optaplanner')
setupUpdateDependencyJob('drools')

void setupUpdateDependencyJob(String updateRepoName) {
    def jobParams = JobParamsUtils.getBasicJobParams(this, "kie-benchmarks-update-${updateRepoName}", JobType.TOOLS, "${jenkins_path}/Jenkinsfile.bump-up-version", "${updateRepoName} bump up version")
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_BRANCH_NAME: "${GIT_BRANCH}",

        UPDATE_REPO_NAME: "${updateRepoName}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('NEW_VERSION', '', "${updateRepoName} version")
        }
    }
}
