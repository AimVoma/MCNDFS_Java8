import matplotlib.pyplot as plt
import numpy as np
import math

#Core Visualization Function for generated .csv files & Overall Performance file 
def Visualize(version_array, measure_array, nrWorkers=0, nrCycles=0, promFile=''):

	plt.rcdefaults()
	fig, ax = plt.subplots()
	
	y_pos = np.arange(len(version_array))
	
	#Normalize() the x_axis due to long float point values
	norm_x_axis = [int(math.floor(round(x))) for x in measure_array]
	#set up the subplots
	clr = ('blue', 'forestgreen', 'gold', 'red', 'purple', 'orange', 'lightblue')
	bar = ax.barh(y_pos, norm_x_axis, align='center', linewidth =0, color=clr)
	ax.set_yticks(y_pos)
	ax.set_yticklabels(version_array)
	ax.invert_yaxis() 

	ax.set_xlabel('Execution Time in Milliseconds(Lower/Higher)')

	#Print the Overall model performance or seperate performance for each .csv file
	if promFile != '':
		ax.set_ylabel('Models')
		ax.set_title('Graph: ' + promFile + '_Workers: [' + nrWorkers + ']' )
		fig_name = promFile +'[' + nrWorkers + ']' + '.png'
	else:
		ax.set_title('Overall Model Performance' )
		fig_name = 'Overall_Performance' + '.png'
	
	plt.legend(bar, version_array)
	plt.tight_layout()
	
	#instead of plotting save the image locally
	plt.savefig(fig_name, dpi=95)