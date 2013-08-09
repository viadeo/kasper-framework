#!/bin/sh

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR/..

web_path="/var/www"

project=$(cat settings.gradle| grep 'rootProject.name' | cut -d "'" -f 2)
version=$(cat build.gradle| grep version | cut -d "'" -f 2)
#branch=$(git rev-parse --abbrev-ref HEAD | sed -e 's,/,,g' | sed -e 's/feature//')
branch=$(git branch --contains HEAD | sed -e 's/[ \*]*//g' | sed -e 's,.*/,,')

path="$web_path/$project/$version/$branch/"

echo $path
