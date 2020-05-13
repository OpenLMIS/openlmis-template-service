#!/usr/bin/env groovy
import hudson.tasks.test.AbstractTestResultAction

properties([
  [
    $class: 'ThrottleJobProperty',
    categories: ['pipeline'],
    throttleEnabled: true,
    throttleOption: 'category'
  ]
])
pipeline {
    agent none
    options {
        buildDiscarder(logRotator(
            numToKeepStr: env.BRANCH_NAME.equals("master") ? '15' : '3',
            daysToKeepStr: env.BRANCH_NAME.equals("master") || env.BRANCH_NAME.startsWith("rel-") ? '' : '7',
            artifactDaysToKeepStr: env.BRANCH_NAME.equals("master") || env.BRANCH_NAME.startsWith("rel-") ? '' : '3',
            artifactNumToKeepStr: env.BRANCH_NAME.equals("master") || env.BRANCH_NAME.startsWith("rel-") ? '' : '1'
        ))
        disableConcurrentBuilds()
        skipStagesAfterUnstable()
    }
    environment {
        COMPOSE_PROJECT_NAME = "template${BRANCH_NAME}"
    }
    stages {
        stage('Preparation') {
            agent any
            steps {
                withCredentials([usernamePassword(
                  credentialsId: "cad2f741-7b1e-4ddd-b5ca-2959d40f62c2",
                  usernameVariable: "USER",
                  passwordVariable: "PASS"
                )]) {
                    sh 'set +x'
                    sh 'docker login -u $USER -p $PASS'
                }
                script {
                    CURRENT_BRANCH = env.GIT_BRANCH // needed for agent-less stages
                    def properties = readProperties file: 'gradle.properties'
                    if (!properties.serviceVersion) {
                        error("serviceVersion property not found")
                    }
                    VERSION = properties.serviceVersion
                    STAGING_VERSION = properties.serviceVersion
                    if (CURRENT_BRANCH != 'master' || (CURRENT_BRANCH == 'master' && !VERSION.endsWith("SNAPSHOT"))) {
                        STAGING_VERSION += "-STAGING"
                    }
                    currentBuild.displayName += " - " + VERSION
                }
            }
            post {
                failure {
                    script {
                        notifyAfterFailure()
                    }
                }
            }
        }
        stage('Build') {
            agent any
            environment {
                PATH = "/usr/local/bin/:$PATH"
                STAGING_VERSION = "${STAGING_VERSION}"
            }
            steps {
                withCredentials([file(credentialsId: '8da5ba56-8ebb-4a6a-bdb5-43c9d0efb120', variable: 'ENV_FILE')]) {
                    script {
                        try {
                            sh( script: "./ci-buildImage.sh" )
                            currentBuild.result = processTestResults('SUCCESS')
                        }
                        catch (exc) {
                            currentBuild.result = processTestResults('FAILURE')
                            if (currentBuild.result == 'FAILURE') {
                                error(exc.toString())
                            }
                        }
                    }
                }
            }
            post {
                success {
                    archive 'build/libs/*.jar,build/resources/main/api-definition.html, build/resources/main/  version.properties'
                }
                unstable {
                    script {
                        notifyAfterFailure()
                    }
                }
                failure {
                    script {
                        notifyAfterFailure()
                    }
                }
                cleanup {
                    script {
                        sh "sudo rm -rf ${WORKSPACE}/{*,.*} || true"
                    }
                }
            }
        }
        stage('Sonar analysis') {
            agent any
            environment {
                PATH = "/usr/local/bin/:$PATH"
            }
            steps {
                withSonarQubeEnv('Sonar OpenLMIS') {
                    withCredentials([string(credentialsId: 'SONAR_LOGIN', variable: 'SONAR_LOGIN'), string(credentialsId: 'SONAR_PASSWORD', variable: 'SONAR_PASSWORD')]) {
                        script {
                            sh(script: "./ci-sonarAnalysis.sh")

                            // workaround: Sonar plugin retrieves the path directly from the output
                            sh 'echo "Working dir: ${WORKSPACE}/build/sonar"'
                        }
                    }
                }
                timeout(time: 1, unit: 'HOURS') {
                    script {
                        def gate = waitForQualityGate()
                        if (gate.status != 'OK') {
                            echo 'Quality Gate FAILED'
                            currentBuild.result = 'UNSTABLE'
                        }
                    }
                }
            }
            post {
                unstable {
                    script {
                        notifyAfterFailure()
                    }
                }
                failure {
                    script {
                        notifyAfterFailure()
                    }
                }
                cleanup {
                    script {
                        sh "sudo rm -rf ${WORKSPACE}/{*,.*} || true"
                    }
                }
            }
        }
    }
    post {
        fixed {
            script {
                BRANCH = "${env.GIT_BRANCH}".trim()
                if (BRANCH.equals("master") || BRANCH.startsWith("rel-")) {
                    slackSend color: 'good', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Back to normal"
                }
            }
        }
    }
}

def notifyAfterFailure() {
    messageColor = 'danger'
    if (currentBuild.result == 'UNSTABLE') {
        messageColor = 'warning'
    }
    BRANCH = "${env.GIT_BRANCH}".trim()
    if (BRANCH.equals("master") || BRANCH.startsWith("rel-")) {
        slackSend color: "${messageColor}", message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} ${env.STAGE_NAME} ${currentBuild.result} (<${env.BUILD_URL}|Open>)"
    }
    emailext subject: "${env.JOB_NAME} - #${env.BUILD_NUMBER} ${env.STAGE_NAME} ${currentBuild.result}",
        body: """<p>${env.JOB_NAME} - #${env.BUILD_NUMBER} ${env.STAGE_NAME} ${currentBuild.result}</p><p>Check console <a href="${env.BUILD_URL}">output</a> to view the results.</p>""",
        recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']]
}

def processTestResults(status) {
    checkstyle pattern: '**/build/reports/checkstyle/*.xml'
    pmd pattern: '**/build/reports/pmd/*.xml'
    junit '**/build/test-results/*/*.xml'

    AbstractTestResultAction testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    if (testResultAction != null) {
        failuresCount = testResultAction.failCount
        echo "Failed tests count: ${failuresCount}"
        if (failuresCount > 0) {
            echo "Setting build unstable due to test failures"
            status = 'UNSTABLE'
        }
    }

    return status
}
