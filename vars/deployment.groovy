#!/usr/bin/env groovy
def deployment(Map stepParams) 
{
    if ( "${deploy}" == "true" )
    {
        stage('Taking Backup')
        {
            codePath: "${config.CODE_BASE_PATH}"
            //sh "aws ssm get-parameters-by-path --path /coinsfast/dev --recursive --with-decryption --output text   --query 'Parameters[].[Name,Value]' | sed  -e \'s/\\/coinsfast\\///\' | sed -e \'s/dev\\///\' | sed -e \'s/$/"/g\' | sed -r \'s/\\s+/="/g\' > .env"
            sh 'aws ssm get-parameters-by-path --path /coinsfast/dev --recursive --with-decryption --output text   --query "Parameters[].[Name,Value]" | sed  -e \'s/\\/coinsfast\\///\' | sed -e \'s/dev\\///\' | sed -e \'s/$/"/g\' | sed -r \'s/\\s+/="/g\' > .env'
            sh "cat .env"
            sh 'ssh -i ${stepsParams.credentials} ${stepsParams.user}@${stepsParams.ipaddress_1} \'rm ${stepsParams.applicationPath}.env\''
            
            sh 'echo "passed"'
            sh 'scp -i ${stepsParams.credentials} .env ${stepsParams.user}@${stepsParams.ipaddress_1}:${stepsParams.applicationPath}'
            sh 'ssh -i ${stepsParams.credentials} ${stepsParams.user}@${stepsParams.ipaddress_1} \'cp -prf ${stepsParams.applicationPath}* ${stepsParams.applicationBackup}\''
            sh 'ssh -i ${stepsParams.credentials} ${stepsParams.user}@${stepsParams.ipaddress_2} \'rm ${stepsParams.applicationPath}.env\''
            sh 'scp -i ${stepsParams.credentials} .env ${stepsParams.user}@${stepsParams.ipaddress_2}:${stepsParams.applicationPath}'
            sh 'ssh -i ${stepsParams.credentials} ${stepsParams.user}@${stepsParams.ipaddress_2} \'cp -prf ${stepsParams.applicationPath}* ${stepsParams.applicationBackup}\''
        }
        stage('Deploy to the Test environment')
        {
            sh 'rsync -avz --no-perms --exclude \'.git\' ${WORKSPACE}/* ${stepsParams.user}@${stepsParams.ipaddress_1}:${stepsParams.applicationPath}'
            sh 'rsync -avz --no-perms --exclude \'.git\' ${WORKSPACE}/* ${stepsParams.user}@${stepsParams.ipaddress_2}:${stepsParams.applicationPath}'
        }
    }

    if ( "${rollback}" == "true" )
    {
        stage('Rollback to Previous version')
        {
            sh 'ssh ${stepsParams.user}@${stepsParams.ipaddress_1} \'cp -rf ${stepsParams.applicationBackup}* ${stepsParams.applicationPath}\''
            sh 'ssh ${stepsParams.user}@${stepsParams.ipaddress_2} \'cp -rf ${stepsParams.applicationBackup}* ${stepsParams.applicationPath}\''
        }
    }
}

def deleteDir()
{
    stage('Delete Directory')
    {
        deleteDir()
    }
}

def call(Map stepParams) {
    try 
    {
        gitCheckout.checkoutCode()
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
        deployment(
            codeBasePath: "${config.CODE_BASE_PATH}"
        )
    }
    catch (Exception e)
    {
        echo "Something went wrong! Please check"
        echo e.toString()
        throw e
    }

    deleteDir()
}
