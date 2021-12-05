FROM openjdk:17.0.1-buster

RUN curl "https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein" > /bin/lein
RUN chmod +x /bin/lein && lein version

WORKDIR /app

COPY project.clj .
RUN lein do install, test

COPY src src
COPY test test

RUN lein test
