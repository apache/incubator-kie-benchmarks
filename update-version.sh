#!/usr/bin/env bash

if [[ $# != 2 || $1 = -h || $1 = --help ]]
then
  echo "Usage: $0 [OPTIONS] <new_kie_version> <new_optaplanner_version>"
  echo ""
  echo "Updates version of this project, including parents of individual modules."
  echo ""
  echo "Example:"
  echo ""
  echo "$0 7.26.0.redhat-000004 8.5.0.Final-redhat-00004"
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

NEW_KIE_VERSION="$1"
NEW_OPTAPLANNER_VERSION="$2"

OLD_KIE_VERSION=$(grep "^    <version>" -h pom.xml | head -n1 | sed 's:[^/]*>\(.*\)</.*:\1:')
OLD_OPTAPLANNER_VERSION=$(grep "^    <version.org.optaplanner>" -h ./optaplanner-8-benchmarks/pom.xml | head -n1 | sed 's:[^/]*>\(.*\)</.*:\1:')

replace_in_poms "$OLD_KIE_VERSION" "$NEW_KIE_VERSION"
replace_in_poms "$OLD_OPTAPLANNER_VERSION" "$NEW_OPTAPLANNER_VERSION"