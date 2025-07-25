pipeline {
  agent any

  environment {
    // Variables necesarias para Sonar y Gradle
    SONAR_HOST_URL = 'http://sonarqube:9000'
    SONAR_SCANNER_OPTS = "-Dsonar.projectKey=intcomex-api"
    DOCKER_IMAGE = 'intcomex-api'
    DOCKER_PORT = '8090'
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        sh './gradlew clean build jacocoTestReport --no-daemon'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('sonaqube-docker') {
          sh './gradlew sonarqube --info'
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        script {
          sh "docker build -t ${DOCKER_IMAGE} ."
        }
      }
    }

    stage('Run Container') {
      steps {
        script {
          // Stop previous container if running
          sh "docker rm -f ${DOCKER_IMAGE} || true"
          // Run new container on port 8090
          sh "docker run -d -p ${DOCKER_PORT}:${DOCKER_PORT} --name ${DOCKER_IMAGE} ${DOCKER_IMAGE}"
        }
      }
    }
  }

  post {
    failure {
      echo 'Pipeline failed.'
    }
    success {
      echo 'Pipeline completed successfully.'
    }
  }
}
