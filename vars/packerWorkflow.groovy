#!/usr/bin/env groovy
def validatePacker(Map stepParams) 
{
    stage("Packer Validation") 
    {
        packerAction.executeAction(
            codePath: "${config.CODE_BASE_PATH}",
            operation: "validate"
            varfile: "${config.variableFile}",
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
            varfile: "${config.variableFile}",
            packerfile: "${config.packerBuilderFile}"
        )
    }
}

def call(Map stepParams) {
    
    try 
    {
        git.checkoutCode()
    } 
    catch (Exception e) 
    {
        echo "Unable to clone CodeBase"
        echo e.toString()
        throw e
    }

    try 
    {
        config = commonfile.readPropertyFile(
            configFilePath: "${stepParams.configFilePath}"
        )
    }
    catch (Exception e) 
    {
        echo "Sorry I'm unable to read Config file"
        echo e.toString()
        throw e
    }

    try 
    {
        validatePacker(
            codeBasePath: "${config.CODE_BASE_PATH}",
        )
    } 
    catch (Exception e) 
    {
        echo "Unable to Validate Packer Scripts"
        echo e.toString()
        throw e
    }

    try 
    {
        buildPacker(
            codeBasePath: "${config.CODE_BASE_PATH}",
        )
    } 
    catch (Exception e) 
    {
        echo "Unable to Build Packer"
        echo e.toString()
        throw e
    }
}
