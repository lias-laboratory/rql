FROM node:20 AS build-node-stage

WORKDIR /work
ADD package.json /work
ADD package-lock.json /work
RUN ["npm", "install"]

ADD .eslintrc.js /work
ADD .postcssrc.js /work
ADD index.html /work
ADD static /work/static/
ADD config /work/config/
ADD build /work/build/
ADD src /work/src/
RUN ["npm", "run", "build"]

FROM nginx:latest

COPY --from=build-node-stage /work/dist/ /usr/share/nginx/html/
ADD default.conf /etc/nginx/conf.d

LABEL maintainer="Bilal REZKELLAH"