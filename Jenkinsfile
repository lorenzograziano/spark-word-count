pipeline {

  agent none

  stages {

    stage('build') {
      agent {
        docker {
          image 'hseeberger/scala-sbt'
          args '-v docker-sbt-home:/docker-java-home'
        }
      }
      steps {
        sh 'sbt compile'
      }
    }

    stage('codeCoverage') {
     agent {
        docker {
          image 'hseeberger/scala-sbt'
          args '-v docker-sbt-home:/docker-java-home'
        }
     }
     steps {
        script{
          //launch coverage test
          sh(script: 'sbt clean coverage test coverageReport', returnStatus: false)
        }
     }

    }

    stage('SYSTEM TEST') {
      steps {
        node('master') {
          script {

              sh("cd /home/lorenzo/IdeaProjects/spark-word-cnt/")
              sh("sbt package")

              sh("/home/lorenzo/apps/spark-2.2.0-bin-hadoop2.7/bin/spark-submit --class "org.spark.wordcount.WordCount" --master local[4] /home/lorenzo/IdeaProjects/spark-word-cnt/target/scala-2.11/spark-word-count_2.11-1.0.jar")
          }
        }
      }
    }

    stage('GIT TAG') {
      steps {
        node('master') {
           script{
              repositoryCommiterEmail = 'lorenzo.graziano@agilelab.it'
              repositoryCommiterUsername = 'lorenzo'
              repositoryCommiterPassword = 'password'
              repo = 'spark-testing-base.git'
              repositoryUrl = 'https://github.com/lorenzograziano90/spark-word-count.git'

              checkout scm
              sh "echo done"
              if (env.BRANCH_NAME == 'master') {
                  stage 'tagging'

                  // GIT CONFIG
                  sh("git config user.email ${repositoryCommiterEmail}")
                  sh("git config user.name '${repositoryCommiterUsername}'")
                  sh("git remote set-url origin '${repositoryUrl}'")

                  // deletes current snapshot tag
                  sh "git tag -d snapshot || true"

                  // tags current changeset
                  sh "git tag -a snapshot -m \"passed CI\""

                  // deletes tag on remote in order not to fail pushing the new one
                  sh "git push origin :refs/tags/snapshot"

                  // pushes the tags
                  sh "git push --tags"
              }
           }

        }
      }
    }
    stage('NEXUS DEPLOY') {
      agent {
            docker {
              image 'sonatype/nexus3'
              args '-p 8081:8081 --name nexus'
            }
         }
      steps {
        script {
           sh 'sbt publish'
        }
      }
    }


/*
    stage('DEPLOY') {
      steps {
        script {


        }
      }
    }


    */


  }
}

