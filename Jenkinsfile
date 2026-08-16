pipeline {

    agent any

    stages {

        stage('Docker Build') {
            steps {
                sh 'docker build -t cicd-sdet-demo:jenkins .'
            }
        }

        stage('Automated Tests') {

            parallel {

                stage('Smoke Tests') {
                    steps {
                        sh 'docker run --rm cicd-sdet-demo:jenkins mvn test -Dgroups=smoke'
                    }
                }

                stage('Regression Tests') {
                    steps {
                        sh 'docker run --rm cicd-sdet-demo:jenkins mvn test -Dgroups=regression'
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