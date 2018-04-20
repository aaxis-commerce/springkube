pipeline {
  agent any
  stages {
    stage('Validate') {
      parallel {
        stage('Validate springkube') {
          agent {
            docker {
              image 'openjdk:8-jdk-alpine'
              args "-v ${env.JENKINS_HOME}/.m2:/mvn_repo"
            }
          }
          steps {
            fileExists 'README.md'
            echo 'Loading Properties'
            /*
            	The pipeline properties file is required to specify at least the following:
            	
            	springkube.dockerRepo
            	
            	Optional Properties:
            	mvn.local.repo - override may be needed in some environments to avoid permissions problems
            	alwaysPushImage - push to docker repo, even if this isn't master branch
            	
            	NOTE: This requires the pipeline-utility-steps plugin for the readProperties library method.
            */
            script {
                pipelineProperties = readProperties file: "${env.JENKINS_HOME}/kube-workspace/springkube-ci.properties"
                dockerRepo = pipelineProperties[ 'springkube.dockerRepo' ]
                assert dockerRepo != null
                assert dockerRepo != ''
                mvnRepo = '/mvn_repo/repository'
                mvnRepoOverride = pipelineProperties[ 'mvn.local.repo' ] 
                if (mvnRepoOverride) {
                       mvnRepo = mvnRepoOverride
                }

            }
            echo "Docker Repo: $dockerRepo"
            echo "MVN Repo: $mvnRepo"
            sh "./mvnw -Dmaven.repo.local=$mvnRepo validate"
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
          args "-v ${env.JENKINS_HOME}/.m2:/mvn_repo"
        }
        
      }
      steps {
        sh "./mvnw -Dmaven.repo.local=$mvnRepo --batch-mode -V -U -e clean compile test-compile -Dsurefire.useFile=false"
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
          args "-v ${env.JENKINS_HOME}/.m2:/mvn_repo"
        }
        
      }
      steps {
        echo 'Test Stage'
        unstash name:'springkube-target-build'
        //sh '''./mvnw -Dmaven.repo.local=$mvnRepo --batch-mode -V -U -e test -Dsurefire.useFile=false -Dmaven.test.failure.ignore=true'''
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
          args "-v ${env.JENKINS_HOME}/.m2:/mvn_repo"
        }
        
      }
      steps {
        echo 'Building Package'
        unstash name:'springkube-target-test'
        sh "./mvnw -Dmaven.repo.local=$mvnRepo --batch-mode -V -U -e package -DskipTests=true -Ddockerfile.skip"
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
          args "-v ${env.JENKINS_HOME}/.m2:/mvn_repo -v /var/run/docker.sock:/var/run/docker.sock $DOCKER_AGENT_DOCKERHOST_ARG"
        }
        
      }
      steps {
        unstash name: 'springkube-package'
        sh """./mvnw -Dmaven.repo.local=$mvnRepo --batch-mode -V -U -e dockerfile:build -DskipTests=true \
        -Ddocker.image.repository=${dockerRepo} \
        -Ddocker.image.tag='springkube-${BRANCH_NAME}-b${env.BUILD_NUMBER}'
        """
        archiveArtifacts artifacts: 'target/docker/**', fingerprint: true
        sh './mvnw clean'
      }

    }
    stage('Push docker image') {
      when {
        beforeAgent true
        anyOf {
          branch 'master'
          equals expected: "true", actual: pipelineProperties[ 'alwaysPushImage' ]
        }
      }
      steps {
        script {
          // Empty url uses public repository
          withDockerRegistry([credentialsId: 'docker-hub-creds', url: '']) {
            def myImage = docker.image("${dockerRepo}:springkube-${BRANCH_NAME}-b${env.BUILD_NUMBER}")
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