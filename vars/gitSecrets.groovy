#!/usr/bin/env groovy
def provisionReporting(Map stepParams) 
{
    git_url = stepParams.git_url
    stage('Credentials Scanning')
    {
        split_url = git_url.split('/')
        repo_name = split_url[split_url.length-1]
        split_repo = repo_name.split('.git')
        project = split_repo[0]
        sh "echo $project"
            try
            {
                sh label: '', script: 'git secrets --install'
                sh label: '', script: 'git secrets --register-aws'
                sh label: '', script: '{ git secrets --scan; } 2> secretkeys.txt | true'
                sh 'sed -n \'/\\+/ , /\\[ERROR\\]/{ /\\+/! { /\\[ERROR\\]/! p } }\' secretkeys.txt > secrets.txt'
                //sh "cp secrets.txt ${project}.txt"
                sh "cat secrets.txt"
            }
            catch(Exception e)
            {
                echo "Failed while Credentials Scanning"
                echo e.toString()
                throw e
            }
    }
}
