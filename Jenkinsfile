pipeline {
    agent any

    stages {
        stage('Clean') {
            steps {
                cleanWs()
            }
        }
        stage('Clone') {
            steps {
                git branch: 'master', url: 'https://github.com/clagomess/pirilampo.git'
            }
        }
        stage('Build') {
            steps {
                sh '''docker run --rm \
                -v ~/.m2:/root/.m2 \
                -v .:/opt/pirilampo \
                -w /opt/pirilampo \
                amazoncorretto:8 /bin/bash ./mvnw clean compile package
                '''
            }
            post {
                success {
                    archiveArtifacts '**/target/*-jar-with-dependencies.jar,**/target/*.exe'
                }
            }
        }
    }

    post {
        always {
            sh "sudo chown -R jenkins:jenkins ."
            junit allowEmptyResults: true, skipMarkingBuildUnstable: true, testResults: '**/target/surefire-reports/*.xml'
        }
    }
}
