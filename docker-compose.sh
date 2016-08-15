#!/bin/bash
cat <<EOF
version: '2'
services:
  ${SERVICE_NAME}:
    image: ${SERVICE_IMAGE_NAME}:${SERVICE_IMAGE_TAG}
    volumes:
      - .:/code
      - $SETTINGSXML_HOST_PATH:$SETTINGSXML_CONT_PATH
    working_dir: /code
    command: --db.jdbc.url=jdbc:postgresql://mst_db:5432/${SERVICE_NAME} --db.username=postgres --db.password=postgres
    depends_on:
      - mst_db

  mst_db:
     image: postgres:9.6
     container_name: mst_postgres
     ports:
       - "5432:5432"
     environment:
          POSTGRES_PASSWORD: postgres
          POSTGRES_USER: postgres
          POSTGRES_DB: magista

networks:
  default:
    driver: bridge
    driver_opts:
      com.docker.network.enable_ipv6: "true"
      com.docker.network.bridge.enable_ip_masquerade: "false"
EOF
