#!/bin/bash

set -ex

mvn clean
mvn compile
mvn exec:java -Dexec.mainClass=chess.application.ChessRunner