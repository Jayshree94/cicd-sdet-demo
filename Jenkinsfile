pipeline {

    agent any

    tools {
        maven 'Maven-3.9.9'
    }

    stages {

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

stage('Smoke Tests') {
    steps {
        sh 'pwd'
        sh 'find src/test -type f -print'
        sh 'mvn -version'
        sh 'mvn help:effective-pom'
        sh 'mvn clean test -Dgroups=smoke'
    }
}

        stage('Regression Tests') {
            steps {
                sh 'mvn test -Dgroups=regression'
            }
        }
    }

    post {

        always {
            echo 'CI pipeline completed'
        }

        success {
            echo 'All tests passed'
        }

        failure {
            echo 'Pipeline failed'
        }
    }
}