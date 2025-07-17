#!/usr/bin/env sh

# This is a wrapper script for running gradle
DIR="$(cd "$(dirname "$0")" && pwd)"
exec "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@" 
