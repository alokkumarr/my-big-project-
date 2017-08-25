# How can I run a command inside a recently started Docker container?

Execute the following command to get a shell inside the container:

        docker exec -it $(docker ps -ql) bash
