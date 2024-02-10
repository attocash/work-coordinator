FROM gcr.io/distroless/static:debug

COPY ./build/native/work-coordinator/nativeCompile /app/work-coordinator

WORKDIR /app

USER nonroot:nonroot

ENTRYPOINT ["./work-coordinator"]