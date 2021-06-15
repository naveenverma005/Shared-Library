#!/usr/bin/env groovy

def readPropertyFile(Map stepParams) {
    config = readProperties file: "${stepParams.configFilePath}"
    return config
}

def cleanWorkspace() {
    stage("Clean WorkSpace") {
        cleanWs()
    }
}
