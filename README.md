# RW5toCSV
Convert a subset of RW5 data into a CSV file and optional to a text file
``````
Usage: java -jar RW5toCSV.jar args
 -c,--CSV output file <arg>      csv file to write data, optional. If not specified the output is <file-name>.csv in the same directory as the input file
 -d,--default limits             prints the default values of configurable limits
 -ec,--extra comment             output extra comment in log file
 -el,--easting limit <arg>       easting limit
 -elel,--elevation limit <arg>   erlrvation limit
 -l,--LOG output file <arg>      log file to write comments, optional. If not specified the output is <file-name>.log in the same directory as the input file
 -nl,--norhing limit <arg>       norhing limit
 -r,--RW5 input file <arg>       RW5 file to extract data
 -v,--version of the program     prints the version number of the program

Examples:
java -jar RW5toCSV.jar -r BERNECK.rw5 -l BERNECK.log -c BERNECK.csv
java -jar RW5toCSV.jar -r dir1/dir2/BERNECK.rw5

``````
