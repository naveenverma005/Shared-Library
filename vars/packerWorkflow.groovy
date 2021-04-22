#!/usr/bin/env groovy
def validatePacker(Map stepParams) 
{
    stage("Packer Validation") 
    {
        packerAction.executeAction(
            codePath: "${config.CODE_BASE_PATH}",
            operation: "validate"
            varfile: "${config.variableFile}"
            packerfile: "${config.packerBuilderFile}"
        )
    }
}

def buildPacker(Map stepParams) 
{
    stage("Packer Build") 
    {
        packerAction.executeAction(
            codePath: "${config.CODE_BASE_PATH}",
            operation: "build"
            varfile: "${config.variableFile}"
            packerfile: "${config.packerBuilderFile}"
        )
    }
}
