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

    docker run -dit \
        --name postgres \
        --network golden \
        -p 5432:5432 \
        -e POSTGRES_DB=postgres \
        -e POSTGRES_USER=postgres \
        -v /Users/rkmac/Projects/.volumes/postgres:/var/lib/postgresql/data postgres:12.1-alpine \
        postgres \
        -c log_statement=all \
        -c log_destination=stderr

Now you should be inside the container postgres. Let's initialize the database

    cat init.sql| docker exec -i 9a3abbadf096ba91a8412d1e707bb263132bc7d4b822e77ab4b89bc1f0430bf4 psql -U postgres -d postgres

Now lets build and run the backend application
    
    mvn clean package -U

    docker build --build-arg 'target/*.jar' -t golden/golden .

Note if you get a free() pointer error when running the above run the following as sudo and try again
    
    cd ${HOME}
    wget https://github.com/docker/docker-credential-helpers/releases/download/v0.6.3/docker-credential-secretservice-v0.6.3-amd64.tar.gz
    tar -xf docker-credential-secretservice-v0.6.3-amd64.tar.gz
    chmod +x docker-credential-secretservice
    mv /usr/bin/docker-credential-secretservice /usr/bin/docker-credential-secretservice.bkp
    mv docker-credential-secretservice /usr/bin/

    docker run -it --name golden_app --network golden golden/golden -p 8080:8080 

And finally lets setup an NGINX container so we can access the api from the outside world

    docker run -it --rm -d -p 8080:80 --name web --network golden nginx