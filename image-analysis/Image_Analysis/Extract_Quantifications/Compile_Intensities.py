'''

Kristen Witte
Glotzer Lab

Compile data from intensity measurements derived from "Measure_Intensities.txt".
 Best run in iPython Notebook.
'''


# Imports
import math
import numpy as np
import matplotlib.pyplot as plt
import os
import pandas as pd

# Functions

def extract_bckgrd(df, type_data):

    one = type_data+"_ROI_0"
    two = type_data+"_ROI_1"
    three = type_data+"_ROI_2"
    data = df[[one, two, three]]
    data['ave'] = data.mean(axis=1)
    data['sem'] = data.std(axis=1)/math.sqrt(3)

    bckgrd_ave = pd.Series.to_frame(data['ave'])
    bckgrd_sem = pd.Series.to_frame(data['sem'])

    return bckgrd_ave, bckgrd_sem

def compile_bckgrd(extract_bckgrd, df, type_data, back_ave, back_sem):

    bckgrd_ave, bckgrd_sem = extract_bckgrd(df, type_data)

    back_ave.append(bckgrd_ave)
    back_sem.append(bckgrd_sem)

    return back_ave, back_sem

def extract_aves(df, type_data):

    one = type_data+"_ROI_0"
    two = type_data+"_ROI_1"
    three = type_data+"_ROI_2"
    data = df[[one, two, three]]
    data['ave'] = data.mean(axis=1)
    data['sem'] = data.std(axis=1)/math.sqrt(3)

    ave_col = pd.Series.to_frame(data['ave'])
    initial = ave_col.iloc[0]
    ave_norm_to_initial = ave_col.divide(initial)
    ave_norm = np.asarray(ave_norm_to_initial.as_matrix)

    sem_col = pd.Series.to_frame(data['sem'])

    ave_np = np.asarray(data['ave'])
    sem_np = np.asarray(data['sem'])

    return ave_col, sem_col, ave_norm_to_initial, ave_norm, ave_np, sem_np

def compile_lists(extract_aves, df, type_data, ave, sem, ave_raw,
    sem_raw, ave_norm, ave_norm_np):

    ave_col, sem_col, ave_norm_to_initial, ave_np_norm, ave_np, sem_np = extract_aves(df, type_data)

    ave.append(ave_np)
    sem.append(sem_np)

    ave_norm.append(ave_norm_to_initial)
    ave_norm_np.append(ave_np_norm)

    ave_raw.append(ave_col)
    sem_raw.append(sem_col)

    return [ave, sem, ave_norm, ave_norm_np, ave_raw, sem_raw]

def finalize_ave_and_sem(ave_raw, ave_norm, curr_path, save_as):

    ave_norm_df = pd.concat(ave_norm, axis=1)

    df_dimensions = ave_norm_df.shape
    num_cells = df_dimensions[1]

    ave_norm_df['Final Ave'] = ave_norm_df.mean(axis=1)
    ave_norm_df['SEM'] = (ave_norm_df.std(axis=1))/math.sqrt(num_cells)

    save_path = os.path.join(curr_path, save_as)
    ave_norm_df.to_csv(save_path)

    final_norm_ave = np.asarray(ave_norm_df['Final Ave'])
    final_norm_sem = np.asarray(ave_norm_df['SEM'])

    return final_norm_ave, final_norm_sem

def normalize_to_background(ave_raw, bckgrd_ave, curr_path, save_as):

    ave_raw_df = pd.concat(ave_raw, axis=1)
    bckgrd_df = pd.concat(bckgrd_ave, axis=1)

    df_dimensions = ave_raw_df.shape
    num_cells = df_dimensions[1]

    norm_to_bckgrd = ave_raw_df.div(bckgrd_df)

    norm_to_bckgrd['Final Ave'] = norm_to_bckgrd.mean(axis=1)
    norm_to_bckgrd['SEM'] = (norm_to_bckgrd.std(axis=1))/math.sqrt(num_cells)

    save_path = os.path.join(curr_path, save_as)
    norm_to_bckgrd.to_csv(save_path)

    final_norm_ave = np.asarray(norm_to_bckgrd['Final Ave'])
    final_norm_sem = np.asarray(norm_to_bckgrd['SEM'])

    return final_norm_ave, final_norm_sem

# Main Processing

directory = 'Your Directory'
count = 0
mean_ave = []
mean_sem = []

mean_ave_raw = []
mean_sem_raw = []

mean_ave_norm = []
mean_ave_norm_np = []

max_ave = []
max_sem = []

max_ave_raw = []
max_sem_raw = []

max_ave_norm = []
max_ave_norm_np = []

back_ave = []
back_sem = []

for root, expt_dirs, files in os.walk(directory):
    for f in files:
        if f == 'intensities.csv' or f == "MotherCell_intensities.csv" or f == "Target_intensities.csv":
            current_file = os.path.join(root, f)
            data = pd.read_csv(current_file)

            compile_lists(extract_aves, data, 'Mean', mean_ave,
                mean_sem, mean_ave_raw, mean_sem_raw, mean_ave_norm,
                mean_ave_norm_np)

            compile_lists(extract_aves, data, 'Max', max_ave,
                max_sem, max_ave_raw, max_sem_raw, max_ave_norm,
                max_ave_norm_np)
        elif f == 'Background_intensities.csv':

            current_file = os.path.join(root, f)
            bckgrd = pd.read_csv(current_file)

            back_ave, back_sem = compile_bckgrd(extract_bckgrd, bckgrd, 'Mean', back_ave, back_sem)


final_mean_norm_ave, final_mean_norm_sem = normalize_to_background(mean_ave_raw, back_ave, directory, 'Mean_Intensities_Background.csv')
final_max_norm_ave, final_max_norm_sem = normalize_to_background(max_ave_raw, max_ave_norm, directory, 'Max_Intensities_Background.csv')

print "Done"
