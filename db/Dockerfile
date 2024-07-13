FROM influxdb:2

ENV DOCKER_INFLUXDB_INIT_MODE setup
ENV DOCKER_INFLUXDB_INIT_USERNAME admin
ENV DOCKER_INFLUXDB_INIT_PASSWORD admin
ENV DOCKER_INFLUXDB_INIT_ORG me
ENV DOCKER_INFLUXDB_INIT_BUCKET_ID mine
ENV DOCKER_INFLUXDB_INIT_ADMIN_TOKEN mytoken
ENV V1_DB_NAME v1-db
ENV V1_RP_NAME v1-rp
ENV V1_AUTH_USERNAME v1-user
ENV V1_AUTH_PASSWORD v1-password

ADD --chown=root:root data /var/lib/influxdb2

ADD --chown=root:root config /etc/influxdb2

WORKDIR /scripts

ADD --chown=root:root scripts .

RUN chmod +x *

RUN ./setup-v1.sh