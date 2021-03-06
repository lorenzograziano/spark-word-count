pipeline {

  agent none

  stages {

    stage('build') {
        steps {
            node('master') {
              script {
                  sh("cd /home/lorenzo/IdeaProjects/spark-word-cnt/")
                  sh("sbt compile")
                  }
            }
        }
    }

    stage('codeCoverage') {
     steps {
             node('master') {
               script {
                   sh("cd /home/lorenzo/IdeaProjects/spark-word-cnt/")
                   sh(script: 'sbt clean coverage test coverageReport', returnStatus: false)
                   }
             }
           }
    }

    stage('SYSTEM TEST') {
      steps {
        node('master') {
          script {

              sh("cd /home/lorenzo/IdeaProjects/spark-word-cnt/")
              sh("sbt clean package")
              sh('/home/lorenzo/apps/spark-2.2.0-bin-hadoop2.7/bin/spark-submit --class "org.spark.wordcount.WordCount" --master local[4] /home/lorenzo/IdeaProjects/spark-word-cnt/target/scala-2.11/spark-word-count_2.11-1.0.jar' )
          }
        }
      }
    }

    stage('GIT TAG') {
      input {
          message 'Check the result of the System Integration test, do you want to continue the pipeline execution??'
          id 'Yes'
          parameters {
            string(name: 'CONTINUE', defaultValue: 'false')
          }
      }
      steps {
        node('master') {
           script{
            if (env.CONTINUE == 'false') {
               echo 'Exit from jenkins pipeline'
               exit 1
            } else {

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
    }

    stage('Deploy') {
        steps {
                node('master') {
                  script {
                      sh("cp /home/lorenzo/IdeaProjects/spark-word-cnt/target/scala-2.11/spark-word-count_2.11-1.0.jar /home/lorenzo/Documenti")
                      }
                }
              }

    }

  }
}

