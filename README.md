# Golden Project

## Installation & Deployment

#### Docker for PostgreSQL
This is much easier to work with and maintain than running Postgres on bare metal. Install docker with apt.

    sudo apt-get install apt-transport-https ca-certificates curl gnupg-agent software-properties-common

    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

    sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"

    sudo apt update

    sudo apt install docker-ce docker-ce-cli containerd.io

Get rid of the need to sudo with docker.

    sudo groupadd docker

    sudo usermod -aG docker $USER

Log out and back in for the group change to take effect.

Test if it is working.

    docker run hello-world

Installing PSQL

    sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
    
    curl -fsSL https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -

    sudo apt upgrade

    sudo apt-get -y install postgresql

#### Create docker network

    docker network create golden;

#### Configuring PostgreSQL Docker Container (Linux/MacOS)
Create your postgres docker container in your dev folder. Note you cannot use bash variables in the absolute path, i.e. ${HOME} will not work

    docker run -dit \
        --name postgres \
        --network golden \
        -p 5432:5432 \
        -e POSTGRES_DB=postgres \
        -e POSTGRES_USER=postgres \
        -v /home/haka6127/.volumes/postgres:/var/lib/postgresql/data postgres:12.1-alpine \
        postgres \
        -c log_statement=all \
        -c log_destination=stderr

Initialize database:

    cat init.sql | docker exec -i e5629c93da80 psql -U postgres -d postgres

Setup Python env:

    sudo apt install python3-pip

    sudo apt install direnv

    apt-get install python3-venv

Add the following to `.bashrc` and reload shell:

    eval "$(direnv hook bash)"

Back out of repo root and go back in. Install python dependencies and import data:

    direnv allow

    pip3 install --upgrade pip

    pip install -r requirements.txt

    python database_upload.py

Import metrics:

    .
    .
    .
    Processing 219 of 219 batch(es)
    Finished inserting 218604 rows in 81.87 seconds
    2669.89 per second inserted

#### Install Java & Maven

    https://adoptopenjdk.net/installation.html#linux-pkg

    sudo apt-get install adoptopenjdk-11-hotspot


#### Build and run the SpringBoot Application
    
    mvn clean package -U

    docker build --build-arg 'target/*.jar' -t golden/golden .

Note, if you get a free() pointer error when running the above run the following as sudo and try again
    
    cd ${HOME}
    wget https://github.com/docker/docker-credential-helpers/releases/download/v0.6.3/docker-credential-secretservice-v0.6.3-amd64.tar.gz
    tar -xf docker-credential-secretservice-v0.6.3-amd64.tar.gz
    chmod +x docker-credential-secretservice
    mv /usr/bin/docker-credential-secretservice /usr/bin/docker-credential-secretservice.bkp
    mv docker-credential-secretservice /usr/bin/

    docker run -dit --rm --name golden_app --network host golden/golden

#### Setup local NGINX

    sudo apt install nginx

    sudo vim /etc/nginx/nginx.conf

Ensure the following are added to the config under the existing headings. Make sure your nginx.conf file is exactly this one
https://github.com/nginx/nginx/blob/master/conf/nginx.conf

    .
    .
    .
    server {
        listen 80;
        .
        .
        .
        location / {
            proxy_pass http://localhost:8080;
        .
        .
        .
    
And restart NGINX
    
    sudo nginx -s reload

## Usage

Get all the neighborhoods here:

    http://35.202.71.208/neighborhoods

For neighborhood parameter use case insensitive neighborhood names. Mode is a case insensitive choice between `oldest`
and `newest`. Count is simply the number of elements you want returned. 100 was asked for specifically but I added dial.

    http://35.202.71.208/top?neighborhood=MISSION&mode=oldest&count=100

    http://35.202.71.208/top?neighborhood=MISSION&mode=newest&count=100

    http://35.202.71.208/neighborhood-geographic-center?neighborhood=MISSION

