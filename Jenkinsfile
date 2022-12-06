def BRANCH_NAME
def CUCUMBER_TAG

pipeline {
    environment {
        reportUrl = "localhost:8080/job/$env.JOB_NAME/$env.BUILD_NUMBER/cucumber-html-reports/overview-features.html"
    }

    agent any

    parameters {
        string name: 'BRANCH_NAME', defaultValue: 'main', description: 'describe your branch to run the test (default is main branch)'
        string name: 'CUCUMBER_TAG', defaultValue: '', description: 'describe your cucumber tag to run the automation test'
    }

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Pull_request') {
            steps {
                script {
                    checkout([
                      $class: 'GitSCM', 
                      branches: [[name: params.BRANCH_NAME]], 
                      doGenerateSubmoduleConfigurations: false, 
                      extensions: [[$class: 'CleanCheckout']], 
                      submoduleCfg: [], 
                      userRemoteConfigs: [[
                        credentialsId: 'git-repo-api',
                        url: 'git@github.com:arisfatahillah/api-web-automation.git',
                        refspec: '+refs/heads/'+params.BRANCH_NAME+':refs/remotes/origin/'+params.BRANCH_NAME
                      ]]
                    ])
                    
                }
            }
        }
      
        stage('Running Automation API') {
            steps {
                script {

                    BROWSER = params.BROWSER
                    ENVIRONMENT = params.ENVIRONMENT
                    CUCUMBER_TAG = params.CUCUMBER_TAG
                    BRANCH_NAME = params.BRANCH_NAME
                    BROWSER = params.BROWSER

                    // go to directory
                    sh '''#!/bin/bash -l
                       whoami
                       echo $PATH
                       cd ${WORKSPACE}
                       gem install bundler
                       bundle install
                    '''
                 

                     println '''
                        ######################################################
                                              PARAMETERS
                        ######################################################
                        BRANCH              : ''' + BRANCH_NAME + '''
                        CUCUMBER TAG        : ''' + CUCUMBER_TAG + '''
                        ######################################################
                        '''
                    
                    // Running automation using cucumber tag
                    sh '''#!/bin/bash -l 
                      bundle exec cucumber -p api --tag ${CUCUMBER_TAG}
                    '''

                }
            }
        }

    }

    post {
  	     failure {
  	        echo "Test failed"
                      cucumber buildStatus: 'FAIL',
                                   failedFeaturesNumber: 1,
                                   failedScenariosNumber: 1,
                                   skippedStepsNumber: 1,
                                   failedStepsNumber: 1,
                                   fileIncludePattern: '**/*.json',
                                   sortingMethod: 'ALPHABETICAL'
  	     }

  	      success {  
            echo "Test succeeded"
                     cucumber buildStatus: 'SUCCESS',
                                   failedFeaturesNumber: 0,
                                   failedScenariosNumber: 0,
                                   skippedStepsNumber: 0,
                                   failedStepsNumber: 0,
                                   fileIncludePattern: '**/*.json',
                                   sortingMethod: 'ALPHABETICAL'

  
          }
  } 
}
