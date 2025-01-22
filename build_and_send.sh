#!/bin/bash
sudo rm -rf /srv/http/app-debug.apk
./gradlew assembleDebug
sudo cp app/build/outputs/apk/debug/app-debug.apk /srv/http/app.apk
