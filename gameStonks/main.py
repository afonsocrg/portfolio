#!/usr/bin/env python3
from typing import List
from StockMarket import StockMarket

from Agent import Agent
from CooperativeAgent import CooperativeAgent
from StrongIsolatedAgent import StrongIsolatedAgent
from WeakIsolatedAgent import WeakIsolatedAgent
from Listener import listener
import math
import random
import pickle
from pathlib import Path

numTicks: int = 15000

nWeak = 200
percentCoop = 0.95
percentCoop = 0
stocksPerAgent = 10
moneyPerAgent = 1000
nCAs: int = math.floor(nWeak * percentCoop)
nWIAs: int = nWeak - nCAs

initialStockPrice = 100

random.seed(2)

BEST_Q_FILE = Path("bestQ.pickle").resolve()


def extractPolicy(Q):
    for state, actions in Q.items():
        keys = list(actions.keys())
        values = list(actions.values())
        bestValue = max(values)
        bestAction = keys[values.index(bestValue)]

        print(state, bestAction, bestValue)

def go() -> None:

    Q = {}
    if BEST_Q_FILE.exists():
        print("Importing best q values")
        with open(BEST_Q_FILE, 'rb') as qfile:
            Q = pickle.load(qfile)
    else:
        print("QFile does not exist, creating new one")

    # Populate
    market: StockMarket = StockMarket(initialStockPrice, nWIAs*stocksPerAgent)
    agents: List[Agent] = []
    for i in range(nWIAs):
        agents.append(WeakIsolatedAgent(str(i), market, moneyPerAgent, stocksPerAgent))

    cooperativeAgents: List[CooperativeAgent] = []
    for i in range(nCAs):
        print("Creating CA")
        ca = CooperativeAgent(str(i), market, moneyPerAgent, stocksPerAgent, numTicks, cooperativeAgents, Q)
        cooperativeAgents.append(ca)


    # agents.append(StrongIsolatedAgent("0", market, moneyPerAgent * nWIAs, 0, numTicks))

    # Action loop
    for tick in range(numTicks):

        random.shuffle(cooperativeAgents)

        for a in agents + cooperativeAgents:
            a.act()

        market.matchOrders()

        for a in agents + cooperativeAgents:
            listener.addAgentWealth(a.getName(), a.getWealth())
            listener.addAgentWorth(a.getName(), a.getWorth())
        listener.newTick()
    
    listener.spitData()

    bestAgent = False
    maxWorth = -math.inf
    for ca in cooperativeAgents:
        w = ca.getWorth()
        if w > maxWorth:
            maxWorth = w
            bestAgent = ca
    
    if not bestAgent:
        return
    with open(BEST_Q_FILE, 'wb') as bqf:
        pickle.dump(bestAgent.Q, bqf)

go()
