pipeline {

    agent any

    environment {
        IMAGE_NAME = "shivam1022/demo-app"
        IMAGE_TAG  = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Check Branch') {
            steps {
                script {
                    env.GIT_BRANCH_NAME = sh(
                        script: "git rev-parse --abbrev-ref HEAD",
                        returnStdout: true
                    ).trim()

                    echo "Current Branch: ${env.GIT_BRANCH_NAME}"
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            when {
                expression {
                    env.GIT_BRANCH_NAME == "develop"
                }
            }
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=demo-app'
                }
            }
        }

        stage('Docker Build') {
            when {
                expression {
                    env.GIT_BRANCH_NAME == "develop"
                }
            }
            steps {
                sh """
                    docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                """
            }
        }

        stage('Docker Push') {
            when {
                expression {
                    env.GIT_BRANCH_NAME == "develop"
                }
            }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'USER',
                    passwordVariable: 'PASS'
                )]) {

                    sh """
                    echo \$PASS | docker login -u \$USER --password-stdin
                    docker push ${IMAGE_NAME}:${IMAGE_TAG}
                    """
                }
            }
        }

        stage('Deploy UAT') {
            when {
                expression {
                    env.GIT_BRANCH_NAME == "develop"
                }
            }
            steps {
                sh """
                    sed -i 's|IMAGE_PLACEHOLDER|${IMAGE_NAME}:${IMAGE_TAG}|' k8s/uat-deployment.yaml
                    kubectl apply -f k8s/uat-deployment.yaml
                """
            }
        }

        stage('Deploy PROD') {
            when {
                expression {
                    env.GIT_BRANCH_NAME == "main"
                }
            }
            steps {
                sh """
                    sed -i 's|IMAGE_PLACEHOLDER|${IMAGE_NAME}:${IMAGE_TAG}|' k8s/prod-deployment.yaml
                    kubectl apply -f k8s/prod-deployment.yaml
                """
            }
        }
    }

    post {
        always {
            cleanWs()
        }

        success {
            echo "Pipeline completed successfully."
        }

        failure {
            echo "Pipeline failed."
        }
    }
}