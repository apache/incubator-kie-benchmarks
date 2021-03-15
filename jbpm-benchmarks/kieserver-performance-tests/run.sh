#!/bin/sh -x

PARAMS=""

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

if [ -n "$testUIDSuffix" ]
then
  PARAMS="$PARAMS -DtestUIDSuffix=$testUIDSuffix"
fi

if [ -n "$remoteAPI" ]
then
  PARAMS="$PARAMS -DremoteAPI=$remoteAPI"
fi

if [ -n "$kieserver_username" ]
then
  PARAMS="$PARAMS -Dkieserver.username=$kieserver_username"
fi

if [ -n "$kieserver_password" ]
then
  PARAMS="$PARAMS -Dkieserver.password=$kieserver_password"
fi

if [ -n "$kieserver_host" ]
then
  PARAMS="$PARAMS -Dkieserver.host=$kieserver_host"
fi

if [ -n "$kieserver_port" ]
then
  PARAMS="$PARAMS -Dkieserver.port=$kieserver_port"
fi

if [ -n "$workbench_name" ]
then
  PARAMS="$PARAMS -Dkieserver.name=$workbench_name"
fi

if [ -n "${datasource_jndi}" ]
then
  PARAMS="${PARAMS} -Ddatasource.jndi=${datasource_jndi}"
fi

if [ -n "${test_package}" ]
then
  PARAMS="${PARAMS} -Dorg.kie.perf.suite.test-package=${test_package}"
fi

if [ "$clean_db_between_scenarios" = true ]
then
  # If we are running only one scenario (so we won't fork run for each scenario as we do when nothing is specified), we want to run also DB cleaning
  if [ -n "$scenario" ] || [ -n "$1" ]
  then
    PARAMS="$PARAMS -Dperfdb"
  fi
fi

if [ -n "${maven_profiles}" ]
then
  MAVEN_PROFILES_LINE="-P${maven_profiles}"
fi

# Provide Nexus location, group and Maven local repository directory to settings.xml
PARAMS="$PARAMS -Dnexus.host=$LOCAL_NEXUS_IP -Dnexus.group=$NEXUS_GROUP -Dlocal.repo.dir=$WORKSPACE/maven-repo"
mvn -B clean install -s settings.xml $MAVEN_PROFILES_LINE exec:exec $PARAMS
