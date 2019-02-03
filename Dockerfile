FROM entony/scala-mxnet-cuda-cudnn:2.12-1.3.1-9-7-builder AS builder
RUN mkdir /tmp/source /tmp/source/dependencies
COPY project /tmp/source/project
COPY src /tmp/source/src
COPY build.sbt /tmp/source/build.sbt
RUN ln -s /usr/local/share/mxnet/scala/linux-x86_64-gpu/mxnet-full_2.12-linux-x86_64-gpu-1.3.1-SNAPSHOT.jar /tmp/source/dependencies/mxnet-full_2.12-linux-x86_64-gpu-1.3.1-SNAPSHOT.jar && \
    cd /tmp/source/ && sbt pack

FROM entony/scala-mxnet-cuda-cudnn:2.12-1.3.1-9-7-runtime
ENV LD_LIBRARY_PATH /usr/local/cuda-9.0/targets/x86_64-linux/lib/stubs:/usr/local/share/OpenCV/java
ENV MODEL_PREFIX "/opt/app/models/resnet50_ssd_model"
RUN mkdir -p /opt/app
COPY --from=builder --chown=root:root /tmp/source/target/pack /opt/app
COPY models /opt/app/models
ENTRYPOINT /opt/app/bin/simple-predictor