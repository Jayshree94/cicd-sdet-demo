pipeline {

    agent any

    tools {
        maven 'Maven-3.9.9'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Source code checked out from GitHub'
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn -version'
                sh 'mvn clean test'
            }
        }

    }
}
