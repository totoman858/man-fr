#!/bin/sh

for number in 1 2 3 4 5 6 7 8
do
   for i in `find ./htmlman$number/ -name '*'`; do 
      #echo "recoding french man in latin1 may be necessary..."
      recode -d utf8..latin1 $i
      #echo "convert $i to txt format..."
      #man $i > $i.txt
      echo "convert $i to html format..."
      man2html < $i > $i.html
   done
done
