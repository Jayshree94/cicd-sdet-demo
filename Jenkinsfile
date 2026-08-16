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

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {
                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                        docker push jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER}
                        docker push jayshreekharate/cicd-sdet-demo:latest
                        docker logout
                    '''
                }
            }
        }
    }

    post {

        always {
            echo 'CI/CD pipeline completed'
        }

        success {
            echo 'Build, testss and Docker image pushed successfully'
        }

        failure {
            echo 'Pipeline failed'
        }
    }
}