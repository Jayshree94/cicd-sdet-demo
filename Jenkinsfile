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
                sh '''
                    docker build \
                        -t jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER} \
                        .

                    docker tag \
                        jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER} \
                        jayshreekharate/cicd-sdet-demo:latest
                '''
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
                        echo "$DOCKER_PASSWORD" | \
                            docker login \
                            -u "$DOCKER_USERNAME" \
                            --password-stdin

                        docker push \
                            jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER}

                        docker push \
                            jayshreekharate/cicd-sdet-demo:latest

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    echo "Pulling application image..."

                    docker pull \
                        jayshreekharate/cicd-sdet-demo:latest

                    echo "Removing previous application..."

                    docker rm -f cicd-app 2>/dev/null || true

                    echo "Starting new application..."

                    docker run -d \
                        --name cicd-app \
                        --network cicd-network \
                        -p 8081:8081 \
                        jayshreekharate/cicd-sdet-demo:latest

                    echo "Deployment started."
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    echo "Waiting for Spring Boot application..."
                    sleep 10

                    echo "Checking application..."

                    curl -f http://cicd-app:8081/hello

                    echo ""
                    echo "Deployment verification successful!"
                '''
            }
        }
    }

    post {

        always {
            echo 'CI/CD pipeline completed'
        }

        success {
            echo 'Build, tests, Docker push and deployment successful!'
        }

        failure {
            echo 'CI/CD pipeline failed!'
        }
    }
}