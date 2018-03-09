import sys
import os
import re
import csv
from vis import Visualize
import collections
from collections import OrderedDict


def Traverse():
	"""
	Visualize the total performance of graph models
	"""
	try:
		directory = os.path.normpath(os.getcwd() + "/Log_Data")
	except IOerror:
		print"IOError Occured"
	"""
	CSV WRITER PRINT ORDER: dos.println("MCNDFS\tTime(ms)\t");
	"""
	# vis_dict = collections.OrderedDict.fromkeys(['naive_1', 'ext_2', 'imprv_3_relock', 'imprv_4_rerwlock', 'imprv_5_cncrtmap', 'naive_6_nprmttn'])
	versions = ['naive_1', 'ext_2', 'imprv_3_relock', 'imprv_4_rerwlock', 'imprv_5_cncrtmap', 'naive_6_nprmttn']
	
	vis_dict = OrderedDict((version, 0.0) for version in versions)	

	# vis_dict = { 'naive_1': 0.0, 'ext_2': 0.0, 'imprv_3_relock' : 0.0, 'imprv_4_rerwlock' : 0.0, 'imprv_5_cncrtmap' : 0.0, 'naive_6_nprmttn' : 0.0}
	measure_array = []
	#Traverse the whole directory of sample files
	for subdir, dirs, files in os.walk(directory):
		for file_counter,file in enumerate(files):
			if file.endswith(".csv"):
				
				tmp_file = directory + '/' + file
				#Accumulate samples/measures in order to calculate the mean value
				with open(tmp_file) as csvfile:
					readCSV = csv.reader(csvfile, delimiter='\t')
					for csv_counter, row in enumerate(readCSV):
						measure_array.append(row[1])

				vis_dict['naive_1'] += float(measure_array[0])
				vis_dict['ext_2'] += float(measure_array[1])
				vis_dict['imprv_3_relock'] += float(measure_array[2])
				vis_dict['imprv_4_rerwlock'] += float(measure_array[3])
				vis_dict['imprv_5_cncrtmap'] += float(measure_array[4])
				vis_dict['naive_6_nprmttn'] += float(measure_array[5])
	
	# Normalize values based on totall number of samples/files
	vis_dict.update( (k, v / float(file_counter + 1)) for k,v in vis_dict.iteritems())

	#Call visualization function to plot the total performance
	Visualize(vis_dict.keys(), vis_dict.values())


if __name__ == "__main__":
    # execute only if run as a script
    Traverse()
