grep -r serialVersionUID *  | grep -v target | grep static | sed -e 's/^\([^:]*\):.*= \(.*\)L/\1 \2/' | sort -n -k 2 | uniq -f 1 -D
