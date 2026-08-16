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

        stage('Automated Tests:') {

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