#!/bin/bash

native-image --macro:truffle --no-fallback --initialize-at-build-time -jar build/libs/tera-uber.jar tera
