#!/bin/bash 
main() {
    user=$1
    ipaddress=$2
    applicationPath=$3
    applicationBackup=$4
    aws ssm get-parameters-by-path --path /coinsfast/test --recursive --with-decryption --output text   --query "Parameters[].[Name,Value]" | sed  -e 's/\/coinsfast\///' | sed -e 's/test\///' | sed -e 's/$/"/g' | sed -r 's/\s+/="/g' > .env
    current_file="/env-file/test/.env"
    new_file=".env"
    if cmp -s "$current_file" "$new_file"; 
        then
            printf 'The file "%s" is the same as "%s"\n' $current_file $new_file
        else
        cp $new_file $current_file
        printf 'New .env file generated'
        printf 'Moving New .env to the main server'
        scp -i /var/lib/jenkins/testServer.pem .env ${user}@${ipaddress}:${applicationPath}
	    printf 'New File Moved'
    fi
    ssh -i /var/lib/jenkins/testServer.pem ${user}@${ipaddress} "cp -prf ${applicationPath}* ${applicationBackup}"
}
user=$1
ipaddress=$2
applicationPath=$3
applicationBackup=$4
main ${user} ${ipaddress} ${applicationPath} ${applicationBackup}
