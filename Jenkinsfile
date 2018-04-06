pipeline {
  agent any
  stages {
    stage('Validate') {
      parallel {
        stage('Validate springkube') {
          agent {
            docker {
              image 'openjdk:8-jdk-alpine'
              args '-u 0:0 -v $HOME/.m2:/root/.m2'
            }
          }
          steps {
            fileExists 'README.md'
            sh './mvnw validate'
          }
        }
        stage('Validate pipeline') {
          steps {
            validateDeclarativePipeline 'Jenkinsfile'
          }
        }
      }
    }
    stage('Build springkube') {
      agent {
        docker {
          image 'openjdk:8-jdk-alpine'
          args '-u 0:0 -v $HOME/.m2:/root/.m2'
        }
        
      }
      steps {
        sh '''./mvnw --batch-mode -V -U -e clean compile test-compile -Dsurefire.useFile=false'''
        stash name: 'springkube-target-build', includes: 'target/**'
      }
    }
    stage('Test springkube') {
      agent {
        docker {
          image 'openjdk:8-jdk-alpine'
          args '-u 0:0 -v $HOME/.m2:/root/.m2'
        }
        
      }
      steps {
        echo 'Test Stage'
        unstash name:'springkube-target-build'
        //sh '''./mvnw --batch-mode -V -U -e test -Dsurefire.useFile=false -Dmaven.test.failure.ignore=true'''
        //stash name: 'testResults', includes: '**/target/surefire-reports/**/*.xml'
        echo 'No tests to run'
        stash name: 'springkube-target-test', includes: 'target/**'
      }
    }
    stage('Record springkube Tests') {
      agent any
      steps {
        //unstash name: 'testResults'
        //sh 'touch target/surefire-reports/*.xml'
        //junit 'target/surefire-reports/*.xml'
        echo 'No tests at this time'
      }
    }
    stage('Package springkube') {
      agent {
        docker {
          image 'openjdk:8-jdk-alpine'
          args '-u 0:0 -v $HOME/.m2:/root/.m2'
        }
        
      }
      steps {
        echo 'Building Package'
        unstash name:'springkube-target-test'
        sh '''./mvnw --batch-mode -V -U -e package -DskipTests=true -Ddockerfile.skip'''
        stash(name: 'springkube-package', includes: 'target/**')
      }
    }
    stage('Deploy springkube') {
      /* when {
        branch 'production'
      } */
      steps {
        timeout(time: 5, unit: 'MINUTES') {
          input(message: 'Deploy to repository?', ok: 'Approve')
        }
        
        echo 'Deploying..'
      }
    }
    stage('Build docker image') {
      agent any
      steps {
        unstash name: 'springkube-package'
        sh """./mvnw --batch-mode -V -U -e dockerfile:build -DskipTests=true \
        -Ddocker.image.repository=tfleisher/k8s-repo \
        -Ddocker.image.tag='spring-springkube-${BRANCH_NAME}-b${env.BUILD_NUMBER}'
        """
      }

    }
    stage('Push docker image') {
      agent any
      when {
          branch 'master'
      }
      steps {
        script {
          // Empty url uses public repository
          withDockerRegistry([credentialsId: 'docker-hub-creds', url: '']) {
            def myImage = docker.image("tfleisher/k8s-repo:spring-springkube-${BRANCH_NAME}-b${env.BUILD_NUMBER}")
            myImage.push()
          }
        }
      }
    }
  }
}