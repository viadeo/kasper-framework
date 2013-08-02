#!/bin/sh

set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR/..

VERSION="$1"
VERSION_NOSS=$(echo $VERSION | sed -e 's/-SNAPSHOT//')

tmp=$(mktemp)

cat build.gradle | sed -e "s/version = '.*'/version = '$VERSION'/" > $tmp
mv $tmp build.gradle

cat doc/source/conf.py | sed -e "s/version = '.*'/version = '$VERSION_NOSS'/" -e "s/release = '.*'/release = '$VERSION'/" > $tmp
mv $tmp doc/source/conf.py

