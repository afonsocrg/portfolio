
"""
Generate all combinations of options

"""

lr=[0.01, 0.001, 0.1]
hz=[200, 100]
dropout=[0.3, 0.5]
activation=["relu", "tanh"]
optimizer=["sgd", "adam"]

layers=[1, 2, 3]
for layer in layers:
    for l in lr:
        for h in hz:
            for d in dropout:
                for a in activation:
                    for o in optimizer:
                        print(f"python hw1-q4.py mlp -learning_rate {l} -hidden_sizes {h} -dropout {d} -activation {a} -optimizer {o} -layers {layer}")

