pipeline {
  // Authors: Justin Caabera, Matthew Howard, Ernest Kim, Jonathan Nunez
  agent {
    kubernetes {
      label 'build-agent'
      defaultContainer 'jnlp'
      yaml """
      apiVersion: v1
      kind: Pod
      metadata:
      lables:
      component: ci
      spec:
        containers:
        - name: jnlp
          image: ikenoxamos/jenkins-slave:latest
          workingDir: /home/jenkins
          env:
          - name: DOCKER_HOST
            value: tcp://localhost:2375
          resources:
            requests:
              memory: "500mi"
              cpu: "0.3"
            limits:
              memory: "800Mi"
              cpu: "0.5"
        - name: dind-daemon
          image: docker:18-dind
          workingDir: /var/lib/docker
          securityContext:
            privileged: true
          volumeMounts:
          - name: docker-storage
            mountPath: /var/lib/docker
          resources:
            requests:
              memory: "300Mi"
              cpu: "0.3"
            limits:
              memory: "500Mi"
              cpu: "0.5"
            
      """
    }
  }

  environment {
    DOCKER_IMAGE_NAME = 'eilonwy/blockbuster'
  }

  stages {

    stage('Build'){
      steps {
        sh 'docker build -t $DOCKER_IMAGE_NAME .'
        script{
          app = docker.image(DOCKER_IMAGE_NAME)
        }
        sh 'docker images'
      }
    }

    stage('Push Docker Image'){
      steps{
        script{
          docker.withRegistry('https://registry.hub.docker.com', 'docker-blockbuster-token')
          app.push('latest')
        }
      }
    }
  }
}