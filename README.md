# Golden Project

#### Docker for PostgreSQL
This is much easier to work with and maintain than running Postgres on bare metal. Install docker with apt.

    sudo apt-get install \                                                  
        apt-transport-https \
        ca-certificates \
        curl \
        gnupg-agent \
        software-properties-common

    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

    sudo add-apt-repository \                                                   
        "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
        $(lsb_release -cs) \
        stable"

    sudo apt update

    sudo apt install docker-ce docker-ce-cli containerd.io

Let's get rid of the need to sudo with docker.

    sudo groupadd docker

    sudo usermod -aG docker $USER

Log out and back in for the group change to take effect.

Now test if it is working.

    docker run hello-world

Installing PSQL

    sudo apt install postgres

Configuring PostgreSQL Docker Container (Linux/MacOS)
Create your postgres docker container in your dev folder. Note you cannot use bash variables in the absolute path, i.e. ${HOME} will not work

    docker run -d -it \
        --network host \
        --name postgres \
        -p 5432:5432 \
        -e POSTGRES_DB=postgres \
        -e POSTGRES_USER=postgres \
        -v <ABSOLUTE PATH TO DESIRED LOCATION>/.volumes/postgres:/var/lib/postgresql/data postgres:12.1-alpine \
        postgres \
        -c log_statement=all \
        -c log_destination=stderr

This last command should give you a container id. Use it below.

    docker exec -it <CONTAINER ID> bash

Now you should be inside the container.

    psql -U postgres

Now you should be inside the container postgres. Let's set up the necessary users here.

    create user golden_user createdb;
    alter user golden_user with superuser;
    create database golden;
    \c gitprime;
    grant all privileges on all tables in schema public to golden_user;
    \q
    exit