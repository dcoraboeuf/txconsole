#!/bin/bash

# Help function
function show_help {
	echo "TxConsole build script."
	echo ""
	echo "Available options are:"
	echo "General:"
	echo "    -h, --help                    Displays this help"
	echo "Settings:"                        
	echo "    -m,--mvn=<path>               Path to the Maven executable ('mvn' by default)"
	echo "    -ms,--mvn-settings=<path>     Path to some additional Maven settings file"
	echo "    -ri,--repo-id=<id>            ID of the Maven repository to use for the deployment of artifacts"
	echo "    -ru,--repo-url=<url>          URL of the Maven repository to use for the deployment of artifacts"
	echo "    --push                        Pushes to the remote Git"
	echo "    --deploy                      Uploads the artifacts to the repository"
	echo "Release numbering:"                         
	echo "    -v,--version=<release>        Version to prepare (by default extracted from the POM, by deleting the -SNAPSHOT prefix)"
	echo "    -nv,--next-version=<release>  Next version to prepare (by default, the prepared version where the last digit is incremented by 1)"
}

# Check function
function check {
	if [ "$1" == "" ]
	then
		echo $2
		exit 1
	fi
}

# General environment
export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=256m -Djava.net.preferIPv4Stack=true"

# Defaults
MVN=mvn
MVN_SETTINGS=
NEXUS_ID=dcoraboeuf-release
NEXUS_URL=dav:https://repository-dcoraboeuf.forge.cloudbees.com/release/
VERSION=
NEXT_VERSION=
GIT_PUSH=no
DEPLOY=no

# Command central
for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-m=*|--mvn=*)
			MVN=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-ms=*|--mvn-settings=*)
			MVN_SETTINGS=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-ri=*|--repo-id=*)
			NEXUS_ID=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-ru=*|--repo-url=*)
			NEXUS_URL=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-v=*|--version=*)
			VERSION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-nv=*|--next-version=*)
			NEXT_VERSION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		--push)
			GIT_PUSH=yes
			;;
		--deploy)
			DEPLOY=yes
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checks
check "$MVN" "Maven executable (--mvn) is required."
if [ "$DEPLOY" == "yes" ]
then
	check "$NEXUS_ID" "Nexus ID (--repo-id) is required."
	check "$NEXUS_URL" "Nexus URL (--repo-url) is required."
fi

# Preparation of the version

CURRENT_VERSION=`${MVN} help:evaluate -Dexpression=project.version $MVN_OPTIONS | grep -E "^[A-Za-z\.0-9]+-SNAPSHOT$" | sed -re 's/([A-Za-z\.0-9]+)\-SNAPSHOT/\1/'`

if [ "$VERSION" == "" ]
then
	# Gets the version number from the POM
	VERSION=${CURRENT_VERSION}
fi

# Preparation of the next version
if [ "$NEXT_VERSION" == "" ]
then
	VERSION_LAST_DIGIT=`echo $VERSION | sed -re 's/.*\.([0-9]+)$/\1/'`
	VERSION_PREFIX=`echo $VERSION | sed -re 's/(.*)\.[0-9]+$/\1/'`
	let "NEXT_VERSION_NUMBER=$VERSION_LAST_DIGIT+1"
	NEXT_VERSION="$VERSION_PREFIX.$NEXT_VERSION_NUMBER"
fi

# All variables
echo Maven settings:            ${MVN_SETTINGS}
echo Current version:           ${CURRENT_VERSION}
echo Version to build:          ${VERSION}
echo Next version to promote:   ${NEXT_VERSION}
echo Pushing to Git:            ${GIT_PUSH}
echo Deploying the artifacts:   ${DEPLOY}
if [ "$DEPLOY" == "yes" ]
then
	echo Repository ID:             ${NEXUS_ID}
	echo Repository URL:            ${NEXUS_URL}
fi
	
# Cleaning the environment
echo Cleaning the environment...
git checkout -- .

# Adding the version number in a property file
echo version=${VERSION} > version.properties

# Updating the versions
echo Updating versions to ${VERSION}
${MVN} --quiet versions:set -DnewVersion=${VERSION} -DgenerateBackupPoms=false

# Maven build
echo Launching build...
${MVN} clean install -P it -P it-jetty -DaltDeploymentRepository=${NEXUS_ID}::default::${NEXUS_URL}
if [ $? -ne 0 ]
then
	echo Build failed.
	exit 1
fi

# Deployment of artifacts
if [ "$DEPLOY" == "yes" ]
then
	# Settings file
	SETTINGS=
	if [ "$MVN_SETTINGS" != "" ]
	then
		SETTINGS="--settings ${MVN_SETTINGS}"
	fi
	# Actual deployment
	echo Deployment of artifacts...
	${MVN} ${SETTINGS} deploy:deploy-file -Dfile=txconsole-web/target/txconsole.war -DrepositoryId=${NEXUS_ID} -Durl=${NEXUS_URL} -Dpackaging=war -DgroupId=net.txconsole -DartifactId=txconsole-web -Dversion=${VERSION}
fi
	
# After the build is complete, create the tag

# Tag
TAG=txconsole-${VERSION}
echo Tagging to $TAG
git tag ${TAG}

# Increment the version number and commit
echo Changing to the next version: ${NEXT_VERSION}-SNAPSHOT

# Update the version locally
${MVN} --quiet versions:set -DnewVersion=${NEXT_VERSION}-SNAPSHOT -DgenerateBackupPoms=false

# Commits the update
echo Committing the next version changes
git commit -am "Starting development of ${NEXT_VERSION}"

# Pushing
if [ "$GIT_PUSH" == "yes" ]
then
	echo Pushing to the remote repository
	git push --verbose
	echo Pushing the tags to the remote repository
	git push --tags --verbose
fi

# End
echo Build done.
