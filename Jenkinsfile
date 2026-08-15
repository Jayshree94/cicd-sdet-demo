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
                sh 'mvn test -Dgroups=smoke'
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

        always {
            echo 'CI pipeline completed'
        }
    }
}