import sys
import os
import re
import csv
from vis import Visualize

def Traverse():
	"""
	Traverse & Visualize .csv files
	"""
	try:
		directory = os.path.normpath(os.getcwd() + "/Log_Data")
	except IOerror:
		print"IOError Occured"
	"""
	CSV WRITER PRINT ORDER: dos.println("MCNDFS\tTime(ms)\t");
	"""
	
	#Traverse directory and pick .csv files 
	for subdir, dirs, files in os.walk(directory):
		for file in files:
			version_array = []
			measure_array = []
			if file.endswith(".csv"):
				#Post-Process files based on specific format[T:][C:] Threads/Cycles
				tmp_file = directory + '/' + file
				tmp_ = re.sub(r'_*', '', file).strip('.csv').split('][')
				nrWorkers = tmp_[0].rsplit(':')[1].strip(']').strip('[')
				nrCycles =  tmp_[1].rsplit(':')[1].strip(']').strip('[')
				promFile = re.sub(r'_.+', '', file)
				#Read .csv file and extract Versions and measures(ms)
				with open(tmp_file) as csvfile:
					readCSV = csv.reader(csvfile, delimiter='\t')
					for row in readCSV:
						version_array.append(row[0])
						measure_array.append(float(row[1]))
				#Call the visualization function
				Visualize(version_array, measure_array, nrWorkers, nrCycles, promFile)

if __name__ == "__main__":
    # execute only if run as a script
    Traverse()
