#/bin/sh

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

mvn clean install -s settings.xml -Pperfdb exec:exec $PARAMS
