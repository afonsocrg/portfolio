import argparse

import numpy as np
import matplotlib.pyplot as plt

import utils


def distance(analytic_solution, model_params):
    return np.linalg.norm(analytic_solution - model_params)


def solve_analytically(X, y):
    """
    X (n_points x n_features)
    y (vector of size n_points)

    Q2.1: given X and y, compute the exact, analytic linear regression solution.
    This function should return a vector of size n_features (the same size as
    the weight vector in the LinearRegression class).
    """
    m = np.dot(X.T, X)
    # 1e-4 provided in piazza by professor
    n = m + 1e-4 * np.identity(m.shape[0])
    a = np.linalg.inv(n)
    b = np.dot(a, X.T)
    return np.dot(b, y)


class _RegressionModel:
    """
    Base class that allows evaluation code to be shared between the
    LinearRegression and NeuralRegression classes. You should not need to alter
    this class!
    """
    def train_epoch(self, X, y, **kwargs):
        """
        Iterate over (x, y) pairs, compute the weight update for each of them.
        Keyword arguments are passed to update_weight
        """
        for x_i, y_i in zip(X, y):
            self.update_weight(x_i, y_i, **kwargs)

    def evaluate(self, X, y):
        """
        return the mean squared error between the model's predictions for X
        and the ground truth y values
        """
        yhat = self.predict(X)
        error = yhat - y
        squared_error = np.dot(error, error)
        mean_squared_error = squared_error / y.shape[0]
        return np.sqrt(mean_squared_error)

class LinearRegression(_RegressionModel):
    def __init__(self, n_features, **kwargs):
        self.w = np.zeros((n_features))

    def update_weight(self, x_i, y_i, learning_rate=0.001):
        """
        Q2.2a

        x_i, y_i: a single training example

        This function makes an update to the model weights (in other words,
        self.w).
        """

        """
        # This corresponds to the derivative of the loss function
        # calculated in the evaluate() function.
        # The steps to reach this formula are detailed in the report

        n_feat = x_i.shape[0]

        # calculate gradient (see report)
        # making W X and Y being matrices to ease calculations
        W = self.w.reshape((n_feat, 1))
        X = x_i.reshape(1, n_feat)
        Y = y_i.reshape(1, 1)

        error = np.dot(X, W) - Y
        norm = np.linalg.norm(error)

        XTXW = np.dot(np.dot(X.T, X), W)
        XTY = np.dot(X.T, Y)
        
        gradient = ((XTXW - XTY) / norm)

        # convert back to vector
        gradient = gradient.reshape(n_feat)

        self.w = self.w - learning_rate * gradient
        """

        # This is equivalent of the above calculations
        # except we don't divide the gradient with the norm
        # of the error (yields better results)
        yhat = self.predict(x_i)
        error = yhat - y_i
        gradient = x_i.dot(error)
        self.w = self.w - learning_rate * gradient

    def predict(self, X):
        return np.dot(X, self.w)


class NeuralRegression(_RegressionModel):
    """
    Q2.2b
    """
    def __init__(self, n_features, hidden):
        """
        In this __init__, you should define the weights of the neural
        regression model (for example, there will probably be one weight
        matrix per layer of the model).
        W1: n_hidden x n_features
        """
        self.n_layers = 1

        self.W_dims = [(hidden, n_features), (1, hidden)]
        self.b_dims = [(hidden, 1), (1, 1)]

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
        

    def output(self, x):
        """
        x: a (1 x n_points) matrix

        o(u) = u for regression

        returns a vector of size n_points
        """

        # transform (1 x n_points) into (n_points)
        return x.reshape(x.shape[1])

    def update_weight(self, x_i, y_i, learning_rate=0.001):
        """
        x_i, y_i: a single training example

        This function makes an update to the model weights
        """

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
        grad_z_l = h - np.array([[y_i]])

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


    def predict(self, X):
        """
        X: a (n_points x n_feats) matrix.

        This function runs the forward pass of the model, returning yhat, a
        vector of size n_points that contains the predicted values for X.

        This function will be called by evaluate(), which is called at the end
        of each epoch in the training loop. It should not be used by
        update_weight because it returns only the final output of the network,
        not any of the intermediate values needed to do backpropagation.
        """
        # f = o(W_1 h(W_0 X + b_0) + b_1)
        # Compute internal representation
        h = X.T
        for i in range(self.n_layers):
            # hidden layer i pre-activation
            z = np.dot(self.W[i], h) + self.b[i]

            # hidden layer i activation
            h = self.relu(z)

        # output layer pre-activation
        h = np.dot(self.W[-1], h) + self.b[-1]

        # output activation
        return self.output(h)


def plot(epochs, train_loss, test_loss):
    print(f'Min loss: {min(train_loss)}, {min(test_loss)}')
    plt.suptitle("Train and Test Loss per Epoch", fontsize=12)
    plt.title("(Learning Rate = 1e-3)", fontsize=8)

    plt.xlabel('Epoch')
    plt.xticks(np.arange(0, epochs[-1] + 1, step=100))

    plt.ylabel('Loss')

    plt.plot(epochs, train_loss, label='Train')
    plt.plot(epochs, test_loss, label='Test')

    plt.legend()
    plt.show()


def plot_dist_from_analytic(epochs, dist):
    plt.suptitle("Distance to analytic solution per Epoch", fontsize=12)
    plt.title("(Learning Rate = 1e-3)", fontsize=8)

    plt.xlabel('Epoch')
    plt.xticks(np.arange(0, epochs[-1] + 1, step=100))

    plt.ylabel('Distance')

    plt.plot(epochs, dist, label='dist')

    plt.legend()
    plt.show()

def plot_analytic(X, y):
    linear_regression = LinearRegression(X.shape[0])
    linear_regression.w = solve_analytically(X, y)

    p = np.argsort(y)
    sorted_X = X[p,:]
    sorted_y = y[p]
    y_hat = linear_regression.predict(sorted_X)

    plt.xlabel('whatever')
    plt.ylabel('Y')
    plt.scatter(np.arange(y_hat.size), y_hat, label='prediction', color='b')
    plt.scatter(np.arange(y.size), sorted_y, label='target', color='r')
    plt.legend()
    plt.show()

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('model', choices=['linear_regression', 'nn'],
                        help="Which model should the script run?")
    parser.add_argument('-epochs', default=150, type=int,
                        help="""Number of epochs to train for. You should not
                        need to change this value for your plots.""")
    parser.add_argument('-hidden_size', type=int, default=150)
    parser.add_argument('-learning_rate', type=float, default=0.001)
    opt = parser.parse_args()

    utils.configure_seed(seed=42)

    add_bias = opt.model != 'nn'
    data = utils.load_regression_data(bias=add_bias)
    train_X, train_y = data["train"]
    test_X, test_y = data["test"]

    n_points, n_feats = train_X.shape

    # Linear regression has an exact, analytic solution. Implement it in
    # the solve_analytically function defined above.
    if opt.model == "linear_regression":
        analytic_solution = solve_analytically(train_X, train_y)
    else:
        analytic_solution = None

    # initialize the model
    if opt.model == "linear_regression":
        model = LinearRegression(n_feats)
    else:
        model = NeuralRegression(n_feats, opt.hidden_size)

    # training loop
    epochs = np.arange(1, opt.epochs + 1)
    train_losses = []
    test_losses = []
    dist_opt = []
    for epoch in epochs:
        print('Epoch %i... ' % epoch)
        train_order = np.random.permutation(train_X.shape[0])
        train_X = train_X[train_order]
        train_y = train_y[train_order]
        model.train_epoch(train_X, train_y, learning_rate=opt.learning_rate)

        # Evaluate on the train and test data.
        train_losses.append(model.evaluate(train_X, train_y))
        test_losses.append(model.evaluate(test_X, test_y))

        if analytic_solution is not None:
            model_params = model.w
            dist_opt.append(distance(analytic_solution, model_params))

        print('Loss (train): %.3f | Loss (test): %.3f' % (train_losses[-1], test_losses[-1]))

    plot(epochs, train_losses, test_losses)
    if analytic_solution is not None:
        plot_dist_from_analytic(epochs, dist_opt)


if __name__ == '__main__':
    main()
