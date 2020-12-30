# Golden Project

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

    cat init.sql | docker exec -i <CONTAINER> psql -U postgres -d postgres

Import data into database:

    pip install -r requirements.txt

    python database_upload.py

Import metrics:

    .
    .
    .
    Processing 219 of 219 batch(es)
    Finished inserting 218604 rows in 81.87748599052429 seconds
    2669.8914525208934 per second inserted

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

Ensure the following are added to the config under the existing headings

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