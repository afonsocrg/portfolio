from matplotlib import pyplot as plt
import argparse

def get_data(filename):
    with open(filename) as f:
        data = list(map(lambda x: float(x.strip()),
                    f.readlines()))
    return data

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('-epochs', default=20, type=int)
    parser.add_argument('files', nargs='+')
    parser.add_argument('-labels', nargs='+', type=str, default=[])
    parser.add_argument('-o', default='graph_comparison.png',
                        dest='output')
    parser.add_argument('-ylabel', default='data')
    parser.add_argument('-title', default='data')
    parser.add_argument('-subtitle', default='')
    parser.add_argument('-show', action='store_true')
    opt = parser.parse_args()

    plt.xlabel('Epoch')
    plt.ylabel(opt.ylabel)

    epochs = list(range(1, opt.epochs+1))
    for f in opt.files:
        plt.plot(epochs, get_data(f))
    plt.xticks([x for x in epochs if x % 5 == 0])
    if len(opt.labels) > 0:
        plt.legend(opt.labels)
    plt.suptitle(opt.title, fontsize=12)
    if len(opt.subtitle) > 0:
        plt.title(opt.subtitle, fontsize=8)
    plt.savefig(opt.output)
    if opt.show:
        plt.show()

if __name__ == "__main__":
    main()
