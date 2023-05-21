import os

from matplotlib import pyplot as plt
import numpy as np
import csv

"""
    This script aims to plot all the xy charts in `.csv` files in the `IntegrationTestOutput` using matplotlib.
"""


PATH = 'src/jvmTest/kotlin/com/kstabilty/IntegrationTestOutput/'


def read_csv(file_path: str) -> tuple[np.array, np.array]:
    with open(PATH + file_path) as file:
        raw_data = list(csv.reader(file, delimiter=','))[1:]  # ignores header

        data = np.array(  # converts the raw data to a matrix
            list(map(lambda row:
                     list(map(lambda i:
                              float(i), row)),  # converts every string in a float
                     raw_data))
        )

        return data[:, 0], data[:, 1]  # slice x and y values


def prepare_figure(x: np.array, y: np.array, name: str):
    plt.figure()

    plt.style.use('classic')
    plt.grid(True)
    plt.title(name)
    plt.plot(x, y)


def plot_all_output():
    for file_name in os.listdir(PATH):
        prepare_figure(*read_csv(file_name), file_name.split('.')[0])
    plt.show()


if __name__ == '__main__':
    plot_all_output()
