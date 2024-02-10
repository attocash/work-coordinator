FROM gcr.io/distroless/static:debug

COPY ./build/native/work-coordinator/nativeCompile/work-coordinator /app/work-coordinator

WORKDIR /app

USER nonroot:nonroot

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["./work-coordinator"]