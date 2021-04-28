#!/usr/bin/env groovy

def checkoutCode() {
    stage("Checking out code repository") 
    {
        $class: 'GitSCM',
        branches: [[name:  stageParams.branch ]],
        userRemoteConfigs: [[ url: stageParams.url ]]
    }
}
