#!/usr/bin/env groovy
def call (Map stepsParams) 
{
    commonfile.cleanWorkspace()
    try
    {
        git.checkoutCode()
    }
    catch(Exception e)
    {
        echo "Unable to clone the Repository"
        echo e.toString()
        throw e
    }

    try 
    {
        config = commonfile.readPropertyFile(
            configFilePath: "${stepsParams.configFilePath}"
        )
    }
    catch (Exception e) 
    {
        echo "Sorry I'm unable to read Config file"
        echo e.toString()
        throw e
    }

    stage('Code Build')
    {
        try
        {
            sh 'composer install'
            sh "cd admin/ && composer install"
        }
        catch (Exception e)
        {
            echo "Failed while Code Build"
            echo e.toString()
            throw e
        }
    }

    stage('Checking Code Vulnerabilities') 
    {
        try
        {
            // dependencyCheck additionalArguments: '', odcInstallation: 'Default-Dependency-Check'
            // dependencyCheckPublisher pattern: 'dependency-check-report.xml'
        }
        catch(Exception e)
        {
            echo "Failed to Code Vulnerabilities"
            echo e.toString()
            throw e
        }
    }

    stage('Checking Code-Quality')
    {
        try{
            // sh 'sudo rm -rf .scannerwork'
            // def scannerHome = tool 'SonarQubeScanner';
            // withSonarQubeEnv('SONAR_HOST_URL')
            // {
            //     sh "${scannerHome}/sonar-scanner"
            // }
        } 
        catch(Exception e)
        {
            echo "Failed to check code quality "
            echo e.toString()
            throw e
        }
    }
    try
    {
        gitSecrets.provisionReporting(
            git_url: "${config.git_url}"
        )
    }
    catch(Exception e)
    {
        echo "Unable to Scan Credentials"
        echo e.toString()
        throw e
    }
    if("${config.environment}" == "production")
    {
        if ( "${Deploy}" == "true" )
        {
            stage('Taking Backup')
            {
                sh "bash file_comparison.sh ${config.user} ${config.ipaddress} ${config.applicationPath} ${config.applicationBackup}"
            }
            stage('Deploying to the Production Environment')
            {
                sh " rsync -avz --no-perms --exclude '.git' ${WORKSPACE}/* ${config.user}@${config.ipaddress}:${config.applicationPath}"
            }
        }

        if ( "${Rollback}" == "true" )
        {
            stage('Rollingback to the previous version')
            {
                sh " ssh ${stepsParams.user}@${stepsParams.ipaddress} 'cp -rf ${config.applicationBackup}* ${config.applicationPath} '"
            }
        }
    }

    if("${config.environment}" == "test")
    {
        if ( "${Deploy}" == "true" )
        {
            stage('Taking Backup')
            {
                sh "bash file_comparison.sh ${config.user} ${config.ipaddress} ${config.applicationPath} ${config.applicationBackup}"
            }
            stage('Deploying to the Test Environment')
            {
                sh " rsync -avz --no-perms --exclude '.git' ${WORKSPACE}/* ${config.user}@${config.ipaddress}:${config.applicationPath}"
            }
        }

        if ( "${Rollback}" == "true" )
        {
            stage('Rollingback to the previous version')
            {
                sh " ssh ${config.user}@${config.ipaddress} 'cp -rf ${config.applicationBackup}* ${config.applicationPath} '"
            }
        }
    }
}
