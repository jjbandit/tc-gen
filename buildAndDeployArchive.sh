#! /bin/sh

zip tc-gen-archive.zip exampleTemplate.xlsx genericTemplate.xlsx target/tcGen-0.2.1-jar-with-dependencies.jar LICENSE.txt README.txt
cp tc-gen-archive.zip /home/scallywag/www/jHugs/public/tc-gen-archive.zip;
cd /home/scallywag/www/jHugs;
mup deploy;
