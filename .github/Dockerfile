FROM eclipse-temurin:25 AS build

RUN apt-get update && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/*

RUN curl "https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein" > /bin/lein
RUN chmod +x /bin/lein && lein version

WORKDIR /app

COPY project.clj .
RUN lein deps

COPY src src
COPY test test
COPY resources resources

RUN lein test && \
  lein cljsbuild once min

FROM scratch
COPY --from=build /app/resources/public/ .
