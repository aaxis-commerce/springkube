pipeline {
  agent any
  stages {
    stage('Validate') {
      parallel {
        stage('Validate springkube') {
          agent {
            docker {
              image 'openjdk:8-jdk-alpine'
              args '-v $HOME/.m2:/mvn_repo'
            }
          }
          steps {
            fileExists 'README.md'
            sh './mvnw -Dmaven.repo.local=/mvn_repo/repository validate'
            //sh './mvnw clean'
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
          args '-v $HOME/.m2:/mvn_repo'
        }
        
      }
      steps {
        sh '''./mvnw -Dmaven.repo.local=/mvn_repo/repository --batch-mode -V -U -e clean compile test-compile -Dsurefire.useFile=false'''
        stash name: 'springkube-target-build', includes: 'target/**'
        //sh './mvnw clean'
      }
      post {
        always {
          archiveArtifacts artifacts: 'target/classes/**', fingerprint: true
        }
      }
    }
    stage('Test springkube') {
      agent {
        docker {
          image 'openjdk:8-jdk-alpine'
          args '-v $HOME/.m2:/mvn_repo'
        }
        
      }
      steps {
        echo 'Test Stage'
        unstash name:'springkube-target-build'
        //sh '''./mvnw -Dmaven.repo.local=/mvn_repo/repository --batch-mode -V -U -e test -Dsurefire.useFile=false -Dmaven.test.failure.ignore=true'''
        //stash name: 'testResults', includes: '**/target/surefire-reports/**/*.xml'
        echo 'No tests to run'
        stash name: 'springkube-target-test', includes: 'target/**'
        //sh './mvnw clean'
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
          args '-v $HOME/.m2:/mvn_repo'
        }
        
      }
      steps {
        echo 'Building Package'
        unstash name:'springkube-target-test'
        sh '''./mvnw -Dmaven.repo.local=/mvn_repo/repository --batch-mode -V -U -e package -DskipTests=true -Ddockerfile.skip'''
        stash(name: 'springkube-package', includes: 'target/**')
        archiveArtifacts artifacts: 'target/**.jar', fingerprint: true
        //sh './mvnw clean'
      }
    }
    stage('Build docker image') {
      agent {
        docker {
          image 'openjdk:8-jdk-alpine'
          // XXX: This only works if DOCKER_AGENT_DOCKERHOST_ARG is set as a global environment variable in jenkins.
          // There must be a better way to handle environment differences in docker args.
          args '-u 0:0 -v $HOME/.m2:/mvn_repo -v /var/run/docker.sock:/var/run/docker.sock $DOCKER_AGENT_DOCKERHOST_ARG'
        }
        
      }
      steps {
        unstash name: 'springkube-package'
        sh """./mvnw -Dmaven.repo.local=/mvn_repo/repository --batch-mode -V -U -e dockerfile:build -DskipTests=true \
        -Ddocker.image.repository=tfleisher/k8s-repo \
        -Ddocker.image.tag='springkube-${BRANCH_NAME}-b${env.BUILD_NUMBER}'
        """
        archiveArtifacts artifacts: 'target/docker/**', fingerprint: true
        sh './mvnw clean'
      }

    }
    stage('Push docker image') {
      when {
          branch 'master'
      }
      steps {
        script {
          // Empty url uses public repository
          withDockerRegistry([credentialsId: 'docker-hub-creds', url: '']) {
            def myImage = docker.image("tfleisher/k8s-repo:springkube-${BRANCH_NAME}-b${env.BUILD_NUMBER}")
            myImage.push()
          }
        }
      }
    }
    /*
    stage('Deploy springkube') {
      when {
        branch 'production'
      }
      steps {
        timeout(time: 5, unit: 'MINUTES') {
          input(message: 'Deploy to repository?', ok: 'Approve')
        }
        
        echo 'Some step to trigger deployment...'
      }
    } 
    */
  }
}