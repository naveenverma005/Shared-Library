#!/bin/bash
createDirectory() {
    echo "Creating directories"
    sudo mkdir -p /var/www/html/
}

changinfDirectoryPermission() {
    echo "Changing directory ownership"
    sudo chown -R ubuntu:ubuntu /var/www
    echo "Changing directory permissions"
    sudo chmod -R 775 /var/www
}

installingPhp() {
    echo "Installing PhP based packages"
    sudo apt-get update
    sudo apt install php libapache2-mod-php -y
    sleep 30
    sudo apt-get install php7.4-gmp -y
    sleep 30
}

#installingPhpMyAdmin() {
#    sudo apt install phpmyadmin php-mbstring php-zip php-gd php-json php-curl -y
#    sleep 30
#    sudo apt install curl php-cli php-mbstring git unzip -y
#    sleep 30
#}

installingComposer() {
    echo "Installing Composer"
    cd ~
    curl -sS https://getcomposer.org/installer -o composer-setup.php
    HASH=756890a4488ce9024fc62c56153228907f1545c228516cbf63f885e036d37e9a59d27d63f46af1d4d07ee0f76181c7d3
    php -r "if (hash_file('SHA384', 'composer-setup.php') === '$HASH') { echo 'Installer verified'; } else { echo 'Installer corrupt'; unlink('composer-setup.php'); } echo PHP_EOL;"
    sudo php composer-setup.php --install-dir=/usr/local/bin --filename=composer
    sleep 30
}

createDirectory
changinfDirectoryPermission
installingPhp
#installingPhpMyAdmin
installingComposer







