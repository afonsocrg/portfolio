#!/usr/bin/env python

# Deep Learning Homework 1

import argparse
import random
import os

import numpy as np
import matplotlib.pyplot as plt

import utils


def configure_seed(seed):
    os.environ["PYTHONHASHSEED"] = str(seed)
    random.seed(seed)
    np.random.seed(seed)


class LinearModel(object):
    def __init__(self, n_classes, n_features, **kwargs):
        self.W = np.zeros((n_classes, n_features))

    def update_weight(self, x_i, y_i, **kwargs):
        raise NotImplementedError

    def train_epoch(self, X, y, **kwargs):
        for x_i, y_i in zip(X, y):
            self.update_weight(x_i, y_i, **kwargs)

    def predict(self, X):
        """X (n_examples x n_features)"""
        scores = np.dot(self.W, X.T)  # (n_classes x n_examples)
        predicted_labels = scores.argmax(axis=0)  # (n_examples)
        return predicted_labels

    def evaluate(self, X, y):
        """
        X (n_examples x n_features):
        y (n_examples): gold labels
        """
        y_hat = self.predict(X)
        n_correct = (y == y_hat).sum()
        n_possible = y.shape[0]
        return n_correct / n_possible


class Perceptron(LinearModel):
    def update_weight(self, x_i, y_i, **kwargs):
        """
        x_i (n_features): a single training example
        y_i (scalar): the gold label for that example
        other arguments are ignored
        """
        # Q3.1a
        y_hat = np.dot(self.W, x_i).argmax()
        if y_hat != y_i:
            # update gold class
            self.W[y_i,:] += x_i

            # update incorrect class
            self.W[y_hat,:] -= x_i


class LogisticRegression(LinearModel):
    def update_weight(self, x_i, y_i, learning_rate=0.001):
        """
        x_i (n_features): a single training example
        y_i: the gold label for that example
        learning_rate (float): keep it at the default value for your plots
        """

        # Q3.1b
        z = np.dot(self.W, x_i)
        prob_vec = utils.softmax(z)

        # prob_vec - e_y
        prob_vec[y_i] -= 1
        grad = np.outer(prob_vec, x_i)

        self.W -= learning_rate * grad


class MLP(object):
    # Q3.2b. This MLP skeleton code allows the MLP to be used in place of the
    # linear models with no changes to the training loop or evaluation code
    # in main().
    def __init__(self, n_classes, n_features, hidden_size, n_layers=1):
        # TODO: convert hidden_size to an array of sizes

        # Initialize an MLP with a single hidden layer.
        self.n_layers = n_layers

        self.W_dims = [(hidden_size, n_features), (n_classes, hidden_size)]
        self.b_dims = [(hidden_size, 1), (n_classes, 1)]

        mu = 0.1
        sigma = 0.1
        self.W = []
        for dim in self.W_dims:
            self.W.append(np.random.normal(loc=mu, scale=sigma, size=dim))

        # b needs to be a matrix so we can broadcast it to multiple inputs
        self.b = []
        for dim in self.b_dims:
            self.b.append(np.zeros(dim))

    def relu(self, x):
        res = np.array(x, copy=True)
        # replace every entry that is less than 0 with a 0
        res[res < 0] = 0
        return res

    def relu_prime(self, x):
        res = np.array(x, copy=True)
        res[res < 0] = 0
        res[res > 0] = 1
        return res

    def output(self, h):
        """
        h: a (n_class x n_points) matrix

        returns a vector of size n_points
        each entry corresponds to the label with highest "score"
        """
        return np.argmax(h, axis=0)

    def predict(self, X):
        # Compute the forward pass of the network. At prediction time, there is
        # no need to save the values of hidden nodes, whereas this is required
        # at training time.
        h = X.T
        for i in range(self.n_layers):
            z = np.dot(self.W[i], h) + self.b[i]

            h = self.relu(z)

        h = np.dot(self.W[-1], h) + self.b[-1]

        return self.output(h)

    def evaluate(self, X, y):
        """
        X (n_examples x n_features)
        y (n_examples): gold labels
        """
        # Identical to LinearModel.evaluate()
        y_hat = self.predict(X)
        n_correct = (y == y_hat).sum()
        n_possible = y.shape[0]
        return n_correct / n_possible

    def train_epoch(self, X, y, learning_rate=0.001):
        for (x_i, y_i) in zip(X, y):
            hidden = []
            g_primes = []

            # f = o(W_1 h(W_0 X + b_0) + b_1)
            # Compute internal representation
            h = x_i.reshape((len(x_i), 1))
            hidden.append(h)

            for i in range(self.n_layers):
                # hidden layer i pre-activation
                z = np.dot(self.W[i], h) + self.b[i]

                # hidden layer i activation
                h = self.relu(z)

                # save copy of h for backpropagation
                hidden.append(h)
                g_primes.append(self.relu_prime(z))

            # output layer pre-activation
            h = np.dot(self.W[-1], h) + self.b[-1]

            # backpropagation (using mean squared error loss)
            # Compute output gradient
            grad_z_l = utils.softmax(h)
            grad_z_l[y_i] -= 1

            for l in range(self.n_layers, -1, -1):
                # Compute gradients of hidden layer parameters:
                grad_W_l = np.dot(grad_z_l, hidden[l].T)
                grad_b_l = grad_z_l

                # Compute gradient of previous layer
                # only update if there is previous layer
                if l > 0:
                    grad_h_l = np.dot(self.W[l].T, grad_z_l)
                    grad_z_l = grad_h_l * g_primes[l-1]

                # Apply gradients to weights and bias
                self.W[l] -= learning_rate * grad_W_l
                self.b[l] -= learning_rate * grad_b_l


def plot(epochs, valid_accs, test_accs):
    plt.suptitle("Accuracy per Epoch",fontsize=12)
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.xticks(range(0, epochs[-1] + 1, 100))
    plt.plot(epochs, valid_accs, label='validation')
    plt.plot(epochs, test_accs, label='test')
    plt.legend()
    plt.show()


def main():
    # TODO: parse multiple layer sizes in opt
    parser = argparse.ArgumentParser()
    parser.add_argument('model',
                        choices=['perceptron', 'logistic_regression', 'mlp'],
                        help="Which model should the script run?")
    parser.add_argument('-epochs', default=20, type=int,
                        help="""Number of epochs to train for. You should not
                        need to change this value for your plots.""")
    parser.add_argument('-hidden_size', type=int, default=200,
                        help="""Number of units in hidden layers (needed only
                        for MLP, not perceptron or logistic regression)""")
    parser.add_argument('-layers', type=int, default=1,
                        help="""Number of hidden layers (needed only for MLP,
                        not perceptron or logistic regression)""")
    parser.add_argument('-learning_rate', type=float, default=0.001,
                        help="""Learning rate for parameter updates (needed for
                        logistic regression and MLP, but not perceptron)""")
    opt = parser.parse_args()

    utils.configure_seed(seed=42)

    add_bias = opt.model != "mlp"
    data = utils.load_classification_data(bias=add_bias)
    train_X, train_y = data["train"]
    dev_X, dev_y = data["dev"]
    test_X, test_y = data["test"]

    n_classes = np.unique(train_y).size  # 10
    n_feats = train_X.shape[1]

    # initialize the model
    if opt.model == 'perceptron':
        model = Perceptron(n_classes, n_feats)
    elif opt.model == 'logistic_regression':
        model = LogisticRegression(n_classes, n_feats)
    else:
        # TODO: convert opt.hidden_size into an array of sizes
        model = MLP(n_classes, n_feats, opt.hidden_size, opt.layers)

    epochs = np.arange(1, opt.epochs + 1)
    valid_accs = []
    test_accs = []
    for i in epochs:
        print('Training epoch {}'.format(i))
        train_order = np.random.permutation(train_X.shape[0])
        train_X = train_X[train_order]
        train_y = train_y[train_order]
        model.train_epoch(
            train_X,
            train_y,
            learning_rate=opt.learning_rate
        )
        valid_accs.append(model.evaluate(dev_X, dev_y))
        test_accs.append(model.evaluate(test_X, test_y))
        print('Accuracy (validation): %.3f | Accuracy (test): %.3f' % (valid_accs[-1], test_accs[-1]))

    # plot
    plot(epochs, valid_accs, test_accs)


if __name__ == '__main__':
    main()
