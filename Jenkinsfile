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

        stage('Automated Tests') {

            parallel {

                stage('Smoke Tests') {
                    steps {
                        sh 'mvn clean test -Dgroups=smoke'
                    }
                }

                stage('Regression Tests') {
                    steps {
                        sh 'mvn test -Dgroups=regression'
                    }
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER} .'
                sh 'docker tag jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER} jayshreekharate/cicd-sdet-demo:latest'
            }
        }
    }

    post {

        always {
            echo 'CI/CD pipeline completed'
        }

        success {
            echo 'Build, tests and Docker image creation successful'
        }

        failure {
            echo 'Pipeline failed'
        }
    }
}