#!/bin/bash
./gradlew publish
cp -rf build/repos/releases/com/PESTControl yagsl/repos/releases
