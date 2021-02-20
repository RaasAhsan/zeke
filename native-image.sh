#!/bin/bash

native-image --macro:truffle --no-fallback --initialize-at-build-time -jar build/libs/zeke-uber.jar zeke
