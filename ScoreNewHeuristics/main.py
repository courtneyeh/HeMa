import pandas as pd


def print_scores(csv_filename):
    dataset = pd.read_csv(csv_filename, sep='\s*[,]\s*', header=0, engine="python")

    totals = dataset.groupby("Predictor Type").sum()
    tp = totals['True Positive']
    fp = totals['False Positive']
    fn = totals['False Negative']

    precision = tp / (tp + fp)
    recall = tp / (tp + fn)
    f1 = 2 * precision * recall / (precision + recall)

    print("OVERRIDDEN")
    print("Precision: ", precision['OVERRIDDEN'])
    print("Recall: ", recall['OVERRIDDEN'])
    print("F1: ", f1['OVERRIDDEN'])

    print("TEST")
    print("Precision: ", precision['TEST'])
    print("Recall: ", recall['TEST'])
    print("F1: ", f1['TEST'])


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    print('-----Small-----')
    print_scores('data/small_hema_test_override.csv')

    print('-----Medium-----')
    print_scores('data/med_hema_test_override.csv')

    print('-----Large-----')
    print_scores('data/large_hema_test_override.csv')

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
