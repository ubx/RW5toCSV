# RW5toCSV
Convert a subset of RW5 data into a CSV file and optional to a text file
``````
usage: java -jar RW5toCSV.jar args
 -c,--CSV output file <arg>   CSV file to write data, optional.
      If not specified the output is <file-name>.csv in the same directory as the input file
 -r,--RW5 input file <arg>    RW5 file to extract data
 -t,--TXT output file <arg>   text file to write comments, optional.
      If not specified the output is <file-name>.txt in the same directory as the input file
 
 
Examples:
 java -jar RW5toCSV.jar -r BERNECK.rw5 -t BERNECK.text -c BERNECK.csv
 java -jar RW5toCSV.jar -r dir1/dir2/BERNECK.rw5

``````
