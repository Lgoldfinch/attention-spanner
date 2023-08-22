# Stage 1
FROM hseeberger/scala-sbt:11.0.10_1.4.9_2.12.13 as builder

LABEL authors="lg"

ARG SBT_VERSION="1.8.2"
ARG SBT_HOME="/usr/local/sbt"
ARG SBT_CACHE_DIR="sbt-cache"
ARG SBT_OPTS="-Dsbt.ci=true -Dsbt.coursier.home=$SBT_CACHE_DIR/coursier -Dsbt.global.base=$SBT_CACHE_DIR/.sbt -Dsbt.boot.directory=$SBT_CACHE_DIR/.sbt/boot -Dsbt.ivy.home=$SBT_CACHE_DIR/.ivy2"
WORKDIR /build
COPY . ./
RUN rm -rf ~/.sbt/boot/ ~/.sbt/preloaded/ ~/.ivy2 ~/.cache/coursier/

# Build the project with SBT
#RUN sbt 'universal:stage'

# Stage 2
FROM hseeberger/scala-sbt:11.0.10_1.4.9_2.12.13

WORKDIR /attention-spanner
COPY --from=builder /build/target/universal/stage /attention-spanner
ENTRYPOINT ["/bin/sh", "-c", "/attention-spanner/bin/attention-spanner"]
