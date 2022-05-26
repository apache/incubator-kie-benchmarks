#!/usr/bin/env bash

if [[ $# != 1 || $1 = -h || $1 = --help ]]
then
  echo "Usage: $0 [OPTIONS] <new_version>"
  echo ""
  echo "Updates version of this project, including parents of individual modules."
  echo ""
  echo "Example:"
  echo ""
  echo "$0 7.26.0.redhat-000004"
  echo ""
  echo "OPTIONS"
  echo " -h, --help      display this help"
  echo ""
  exit 1
fi

function replace_in_poms() {
  if [[ $1 == "$2" ]]
  then
    echo "WARNING: Old version ‘$1’ is same as new version ‘$2’. Skipping."
  else
    find . -name 'pom*.xml' -exec sed -i "s/$1/$2/" {} \;
  fi
}

NEW_VERSION="$1"
OLD_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.6.0:exec)


replace_in_poms "$OLD_VERSION" "$NEW_VERSION"
