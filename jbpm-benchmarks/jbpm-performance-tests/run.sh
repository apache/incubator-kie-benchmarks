#/bin/sh

PARAMS=""

if [ -n "$version" ]
then
  PARAMS="$PARAMS -Dversion.org.kie=$version"
fi

if [ -n "$suite" ]
then
  PARAMS="$PARAMS -Dsuite=$suite"
fi

if [ -n "$scenario" ]
then
  PARAMS="$PARAMS -Dscenario=$scenario"
elif [ -n "$1" ]
then
  PARAMS="$PARAMS -Dscenario=$1"
fi

if [ -n "$startScriptLocation" ]
then
  PARAMS="$PARAMS -DstartScriptLocation=$startScriptLocation"
fi

if [ -n "$runType" ]
then
  PARAMS="$PARAMS -DrunType=$runType"
fi

if [ -n "$duration" ]
then
  PARAMS="$PARAMS -Dduration=$duration"
fi

if [ -n "$iterations" ]
then
  PARAMS="$PARAMS -Diterations=$iterations"
fi

if [ -n "$expectedRate" ]
then
  PARAMS="$PARAMS -DexpectedRate=$expectedRate"
fi

if [ -n "$warmUp" ]
then
  PARAMS="$PARAMS -DwarmUp=$warmUp"
fi

if [ -n "$warmUpCount" ]
then
  PARAMS="$PARAMS -DwarmUpCount=$warmUpCount"
fi

if [ -n "$warmUpTime" ]
then
  PARAMS="$PARAMS -DwarmUpTime=$warmUpTime"
fi

if [ -n "$auditLogging" ]
then
  PARAMS="$PARAMS -DauditLogging=$auditLogging"
fi

if [ -n "$threads" ]
then
  PARAMS="$PARAMS -Dthreads=$threads"
fi

if [ -n "$reporterType" ]
then
  PARAMS="$PARAMS -DreporterType=$reporterType"
fi

if [ -n "$periodicity" ]
then
  PARAMS="$PARAMS -Dperiodicity=$periodicity"
fi

if [ -n "$reportDataLocation" ]
then
  PARAMS="$PARAMS -DreportDataLocation=$reportDataLocation"
fi

if [ -n "$perfRepo_host" ]
then
  PARAMS="$PARAMS -DperfRepo.host=$perfRepo_host"
fi

if [ -n "$perfRepo_urlPath" ]
then
  PARAMS="$PARAMS -DperfRepo.urlPath=$perfRepo_urlPath"
fi

if [ -n "$perfRepo_username" ]
then
  PARAMS="$PARAMS -DperfRepo.username=$perfRepo_username"
fi

if [ -n "$perfRepo_password" ]
then
  PARAMS="$PARAMS -DperfRepo.password=$perfRepo_password"
fi

if [ -n "$jbpm_runtimeManagerStrategy" ]
then
  PARAMS="$PARAMS -Djbpm.runtimeManagerStrategy=$jbpm_runtimeManagerStrategy"
fi

if [ -n "$jbpm_persistence" ]
then
  PARAMS="$PARAMS -Djbpm.persistence=$jbpm_persistence"
fi

if [ -n "$jbpm_concurrentUsersCount" ]
then
  PARAMS="$PARAMS -Djbpm.concurrentUsersCount=$jbpm_concurrentUsersCount"
fi

if [ -n "$jbpm_locking" ]
then
  PARAMS="$PARAMS -Djbpm.locking=$jbpm_locking"
fi

if [ -n "$jbpm_ht_eager" ]
then
  PARAMS="$PARAMS -Djbpm.ht.eager=$jbpm_ht_eager"
fi

# If we are running only one scenario (so we won't fork run for each scenario as we do when nothing is specified), we want to run also DB cleaning
if [ -n "$scenario" ] || [ -n "$1" ]
then
  ACTIVATE_DB_PROFILE="-Dperfdb"
fi

# Provide Nexus location, group and Maven local repository directory to settings.xml
PARAMS="$PARAMS -Dnexus.host=$LOCAL_NEXUS_IP -Dnexus.group=$NEXUS_GROUP -Dlocal.repo.dir=$WORKSPACE/maven-repo"

mvn clean install -s settings.xml $ACTIVATE_DB_PROFILE exec:exec $PARAMS
