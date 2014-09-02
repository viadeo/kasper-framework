#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR/..

if [[ $1 == "new" ]]; then
	web_path="/var/www/kasper-doc"
else
	web_path="/var/www"
fi

project=$(cat settings.gradle| grep 'rootProject.name' | cut -d "'" -f 2)
version=$(cat build.gradle| grep -E 'version.*=' | cut -d "'" -f 2)
#branch=$(git rev-parse --abbrev-ref HEAD | sed -e 's,/,,g' | sed -e 's/feature//')
#branch=$(git reflog show --all  | head -1 | cut -d ' ' -f 2 | cut -d '@' -f 1 | sed -e 's,.*/,,')
branch=$(git log --decorate --pretty=oneline --abbrev-commit | head -1 | sed -e 's/[^)]* \([^)]*\)).*/\1/' | sed -e 's/origin\///')

path="$web_path/$project/$version/$branch/"

echo $path
