# RW5toCSV
Convert a subset of RW5 data into a CSV file and optional to a text file
``````
usage: java -jar RW5toCSV.jar args
 -c,--CSV output file <arg>   CSV to write data
 -r,--RW5 input file <arg>    RW5 file to extract data
 -t,--TXT output file <arg>   optional text file to write comments
 
Example:
 java -jar RW5toCSV.jar -r BERNECK.rw5 -t BERNECK.text -c BERNECK.csv

``````

ToDo
* more robust RW5 parsing
* validate results
* write some test cases