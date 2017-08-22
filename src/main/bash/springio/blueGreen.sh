#!/bin/bash
set -e

function logInToPaas() {
    local cfUsername="${CF_USERNAME}"
    local cfPassword="${CF_PASSWORD}"
    local cfOrg="${CF_ORG}"
    local cfSpace="${CF_SPACE}"
    local apiUrl="${CF_API:-api.run.pivotal.io}"
    local CF_INSTALLED="$( cf --version || echo "false" )"
    local CF_DOWNLOADED="$( test -r cf && echo "true" || echo "false" )"
    echo "CF Installed? [${CF_INSTALLED}], CF Downloaded? [${CF_DOWNLOADED}]"
    if [[ ${CF_INSTALLED} == "false" && ${CF_DOWNLOADED} == "false" ]]; then
        echo "Downloading Cloud Foundry"
        curl -L "https://cli.run.pivotal.io/stable?release=linux64-binary&source=github" --fail | tar -zx
        CF_DOWNLOADED="true"
    else
        echo "CF is already installed"
    fi

    if [[ ${CF_DOWNLOADED} == "true" ]]; then
        echo "Adding CF to PATH"
        PATH=${PATH}:`pwd`
        chmod +x cf
    fi

    echo "Cloud foundry version"
    cf --version

    echo "Logging in to CF to org [${cfOrg}], space [${cfSpace}]"
    if [[ "${cfUsername}" != "" ]]; then
        cf api --skip-ssl-validation "${apiUrl}"
        cf login -u "${cfUsername}" -p "${cfPassword}" -o "${cfOrg}" -s "${cfSpace}"
    else
        cf target -o "${cfOrg}" -s "${cfSpace}"
    fi

}

function whichAppIsServingProduction() {
    local blueName=${1:-start-blue}
    local greenName=${2:-start-green}
    local route=${3:-start.cfapps.io}
    local green="$( cf app "${greenName}" | grep routes | grep "${route}" && echo "green" || echo "" )"
    local blue="$( cf app "${blueName}" | grep routes | grep "${route}" && echo "blue" || echo "" )"
    local tailedGreen="$( echo "${green}" | tail -1 )"
    local tailedBlue="$( echo "${blue}" | tail -1 )"
    if [[ "${tailedGreen}" == "green" ]]; then
        echo "green"
    elif [[ "${tailedBlue}" == "blue" ]]; then
        echo "blue"
    else
        echo "none"
    fi
}

function pushApp() {
    local appName=${1:-start-blue}
    local hostname=${2:-start}
    local memory=${3:-}
    local jarLocation=${JAR_LOCATION}
    echo "Pushing app [${appName}] with jar location [${jarLocation}] and memory [${memory}]"
    if [[ "${memory}" != "" ]]; then
        cf push "${appName}" -p "${jarLocation}" -m "${memory}" -n "${hostname}"
    else
        cf push "${appName}" -p "${jarLocation}" -n "${hostname}"
    fi
}

function scaleApp() {
    local appName=${1:-start-blue}
    local instances=${2:-2}
    local memory=${3:-}
    echo "Scaling app [${appName}] with instances [${instances}] and memory [${memory}]"
    if [[ "${memory}" != "" ]]; then
        yes | cf scale "${appName}" -i "${instances}" -m "${memory}" || echo "Failed to scale the app. Continuing with the script"
    else
        yes | cf scale "${appName}" -i "${instances}" || echo "Failed to scale the app. Continuing with the script"
    fi
}

function mapRoute() {
    local appName=${1:-start-blue}
    local hostname=${2:-start-staging}
    local domain=${3:-cfapps.io}
    yes | cf map-route "${appName}" "${domain}" --hostname ${hostname}
}


function unMapRoute() {
    local appName=${1:-start-blue}
    local hostname=${2:-start-staging}
    local domain=${3:-cfapps.io}
    yes | cf unmap-route "${appName}" "${domain}" --hostname ${hostname}
}

function deploy() {
    local oldAppName=${1}
    local newAppName=${2}
    local newHostname=${3}
    local domain="${DOMAIN_NAME}"
    local routedHostname="${ROUTED_HOSTNAME}"
    local oldAppInstances="${OLD_APP_INSTANCES}"
    local newAppInstances="${NEW_APP_INSTANCES}"
    local oldAppMemory="${OLD_APP_MEMORY}"
    local newAppMemory="${NEW_APP_MEMORY}"
    echo "Will deploy the app. Old app is [${oldAppName}], new app is [${newAppName}] with url [${newHostname}.${domain}]"
    pushApp "${newAppName}" "${newHostname}" "${newAppMemory}"
    scaleApp "${newAppName}" "${newAppInstances}" "${newAppMemory}"
    mapRoute "${newAppName}" "${routedHostname}" "${domain}"
    unMapRoute "${oldAppName}" "${routedHostname}" "${domain}"
    scaleApp "${oldAppName}" "${oldAppInstances}" "${oldAppMemory}"
}

function print_usage() {
cat <<EOF

Performs blue / green deployment of an application to production.

ENVIRONMENTAL VARIABLES:

[BLUE_APP_NAME]: The name of the blue instance. Defaults to (start-blue)
[GREEN_APP_NAME]: The name of the green instance. Defaults to (start-green)
[ROUTED_HOSTNAME]: The hostname to which the "production" traffic gets routed. Defaults to (start-staging)
[DOMAIN_NAME]: Domain of the deployed application. Defaults to (cfapps.io)
[JAR_LOCATION]: Location of the JAR to be deployed. Defaults to (initializr-service/target/initializr-service.jar)
[OLD_APP_INSTANCES]: Number of instances of the old instance. Defaults to (1)
[NEW_APP_INSTANCES]: Number of instances of the new instance. Defaults to (2)
[OLD_APP_MEMORY]: Memory to be used by the old instance. (OPTIONAL)
[NEW_APP_MEMORY]: Memory to be used by the new instance. (OPTIONAL)
[CF_USERNAME]: Will reuse your current logged in user if not provided. (OPTIONAL)
[CF_PASSWORD]: Will reuse your current logged in user if not provided. (OPTIONAL)
[CF_ORG]: Cloud Foundry organization to which you would like to deploy the application. (REQUIRED)
[CF_SPACE]: Cloud Foundry space to which you would like to deploy the application. (REQUIRED)
[CF_API]: Cloud Foundry API of the installation to which you would like to deploy the application. Defaults to (initializr-service/target/initializr-service.jar)


EXAMPLE OF USAGE (running locally on a logged in CF client):

$ BLUE_APP_NAME=marcin-blue GREEN_APP_NAME=marcin-green ROUTED_HOSTNAME=marcin-sample DOMAIN_NAME=cfapps.io \
JAR_LOCATION=target/marcin-sample-0.0.1-SNAPSHOT.jar OLD_APP_INSTANCES=1 NEW_APP_INSTANCES=2 OLD_APP_MEMORY=1024m \
NEW_APP_MEMORY=1024m CF_ORG=SomeOrg CF_SPACE=SomeSpace ./blueGreen.sh

EOF
}

export BLUE_APP_NAME="${BLUE_APP_NAME:-start-blue}"
export GREEN_APP_NAME="${GREEN_APP_NAME:-start-green}"
export ROUTED_HOSTNAME="${ROUTED_HOSTNAME:-start-staging}"
export DOMAIN_NAME="${DOMAIN_NAME:-cfapps.io}"
export JAR_LOCATION="${JAR_LOCATION:-initializr-service/target/initializr-service.jar}"
export OLD_APP_INSTANCES=${OLD_APP_INSTANCES:-1}
export NEW_APP_INSTANCES=${NEW_APP_INSTANCES:-2}
export OLD_APP_MEMORY=${OLD_APP_MEMORY:-}
export NEW_APP_MEMORY=${NEW_APP_MEMORY:-}
export CF_USERNAME=${CF_USERNAME:-}
export CF_PASSWORD=${CF_PASSWORD:-}
export CF_ORG=${CF_ORG:-}
export CF_SPACE=${CF_SPACE:-}
export CF_API=${CF_API:-api.run.pivotal.io}

if [[ "${CF_ORG}" == "" ]]; then
    echo "REQUIRED ENV VAR NOT FOUND!!"
    echo "[CF_ORG] env var not passed!"
    print_usage
    exit 1
fi

if [[ "${CF_SPACE}" == "" ]]; then
    echo "REQUIRED ENV VAR NOT FOUND!!"
    echo "[CF_SPACE] env var not passed!"
    print_usage
    exit 1
fi

while [[ $# > 0 ]]
do
key="$1"
case ${key} in
    --help)
    print_usage
    exit 0
    ;;
esac
shift # past argument or value
done

logInToPaas
runningApp=$( whichAppIsServingProduction "${BLUE_APP_NAME}" "${GREEN_APP_NAME}" "${ROUTED_HOSTNAME}" )
echo "Found the following application running on production [${runningApp}]"
case ${runningApp} in
blue)
  deploy "${BLUE_APP_NAME}" "${GREEN_APP_NAME}" "${GREEN_APP_NAME}"
  ;;
*)
  deploy "${GREEN_APP_NAME}" "${BLUE_APP_NAME}" "${BLUE_APP_NAME}"
  ;;
esac
