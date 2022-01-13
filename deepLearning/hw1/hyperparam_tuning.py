
"""
This program will output the several configurations we want to run

"""

lr=[0.01, 0.001, 0.1]
hz=[200, 100]
dropout=[0.3, 0.5]
activation=["relu", "tanh"]
optimizer=["sgd", "adam"]

params = [lr, hz, dropout, activation, optimizer]

names = ["learning_rate", "hidden_sizes", "dropout", "activation", "optimizer"]



layers=[1, 2, 3] # name: layers
# layers = [1]
for layer in layers:
    # run default config
    print(f"python hw-q4.py mlp -layers {layer}")

    # run tune config
    for tune_idx in range(len(params)):
        # print(f"[ ] tuning {names[tune_idx]}")
        for p in params[tune_idx][1:]:
            print(f"python hw-q4.py mlp -{names[tune_idx]} {p} -layers {layer}")

