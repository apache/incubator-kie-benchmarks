#/bin/sh

REPO="$JBOSS_HOME/bin/repositories/kie"

mvn clean deploy -s settings.xml -DKIESERVER_REPOSITORY=$REPO
