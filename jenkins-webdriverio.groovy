def BRANCH_NAME
def CUCUMBER_TAG

pipeline {
    environment {
        reportUrl = "localhost:8080/job/$env.JOB_NAME/$env.BUILD_NUMBER/allure-results"
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
                        credentialsId: 'jenkins-webdriverio',
                        url: 'https://github.com/arisfatahillah/cucumber-webdriverio',
                        refspec: '+refs/heads/'+params.BRANCH_NAME+':refs/remotes/origin/'+params.BRANCH_NAME
                      ]]
                    ])
                    
                }
            }
        }
      
        stage('Running Automation WEB') {
            steps {
                script {

                    CUCUMBER_TAG = params.CUCUMBER_TAG
                    BRANCH_NAME = params.BRANCH_NAME

                    // go to directory
                    bat '''
                       whoami
                       echo $PATH
                       cd ${WORKSPACE}
                       npm i
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
                    bat '''
                      npm run test @register
                    '''

                }
            }
        }

    }

}
