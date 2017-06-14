#!groovy

properties([
        buildDiscarder(logRotator(numToKeepStr: '15'))
])

node {
    stage('Checkout SCM') {
        checkout scm
    }
    stage('Build') {
        echo "Building #${env.BUILD_ID} ..."
        if (env.BRANCH_NAME == 'develop') {
            bat 'mvnw build-helper:parse-version versions:set -DnewVersion=' +
                    '${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}' +
                    "-${env.BUILD_ID} versions:commit"
            }
        bat 'mvnw clean package -DskipTests'
    }
    stage('Unit Tests') {
        echo 'Unit Testing...'
        bat 'mvnw test'
        if (env.BRANCH_NAME == 'develop' && (env.BUILD_ID as int) % 10 == 0) {
            bat 'mvnw sonar:sonar -Dsonar.sources=src/main/scala,src/main/java'
        }
        junit 'target/surefire-reports/*.xml'
        step([ $class: 'JacocoPublisher' ])
    }
    stage('Deploy') {
        if (env.BRANCH_NAME == 'master') {
            echo 'Deploying to the prod...'
            bat 'mvnw deploy -DskipTests'
        } else {
            echo 'Skipping remaining stages...'
            bat 'exit 0'
        }
    }
    stage('Archive Artifact') {
        if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'develop') {
            echo 'Archiving Artifact...'
            archiveArtifacts artifacts: 'target/*.tar'
        }
    }
}
