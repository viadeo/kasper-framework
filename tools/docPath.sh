#!/bin/sh

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR/..

web_path="/var/www"

project=$(cat settings.gradle| grep 'rootProject.name' | cut -d "'" -f 2)
version=$(cat build.gradle| grep version | cut -d "'" -f 2)
#branch=$(git rev-parse --abbrev-ref HEAD | sed -e 's,/,,g' | sed -e 's/feature//')
#branch=$(git reflog show --all  | head -1 | cut -d ' ' -f 2 | cut -d '@' -f 1 | sed -e 's,.*/,,')
branch=$(git log --decorate --pretty=oneline --abbrev-commit | head -1 | sed -e 's/[^)]* \([^)]*\)).*/\1/' | sed -e 's/origin\///')

path="$web_path/$project/$version/$branch/"

echo $path
