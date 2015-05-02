#! /bin/sh

# Copy and rename the packed jar to the top level
cp /home/scallywag/workspace/tc-gen/target/tcGen-*-jar-with-dependencies.jar ./timecardGenerator.jar

# Create the archive
zip tc-gen-archive.zip ./exampleTemplate.xlsx ./genericTemplate.xlsx ./timecardGenerator.jar ./LICENSE.txt ./README.txt

# Remove the renamed jar
rm timecardGenerator.jar

# Move the new archive to the deploy location
mv tc-gen-archive.zip /home/scallywag/www/jHugs/public/tc-gen-archive.zip;

# And deploy
cd /home/scallywag/www/jHugs;
mup deploy;
