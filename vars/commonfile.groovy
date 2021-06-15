#!/usr/bin/env groovy

def readPropertyFile(Map stepsParams) {
    config = readProperties file: "${stepsParams.configFilePath}"
    return config
}

def cleanWorkspace() {
    stage("Clean WorkSpace") {
        cleanWs()
    }
}
