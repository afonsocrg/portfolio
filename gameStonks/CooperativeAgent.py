import typing
from typing import List, Dict

from Agent import Agent
from Action import Action
from Listener import listener
import random
import math

# help mypy find symbol
if typing.TYPE_CHECKING:
    from StockMarket import StockMarket

class Desire:
    def __init__(self, action, strength):
        self.action = action
        self.strength = strength

class CooperativeAgent(Agent):


    def __init__(self, name: str, market: 'StockMarket', balance: float, stocks: int, maxTicks: int, peers: List['CooperativeAgent'], Q = {}):
        super().__init__(f'ca_{name}', market, balance, stocks)
        self.__probAct: float = 0.8
        self.__probCoop: float = 0.5

        self.__peers = peers

        # stores the number of agents performing a specific action
        # for each agent action
        self.__peerActions: Dict[Action, int] = {
            Action.BUY: 0,
            Action.HOLD: 0,
            Action.SELL: 0,
            Action.SKIP: 0
        }

        # Beliefs
        self.__stockPrice = 0
        self.__stockHistory = []

        self.__curTick = 0
        self.__siaTicksToRebuy: int = 1000
        self.__maxTicks: int = maxTicks

        # Learning logic

        self.__curState = False
        self.__eps = 0.05
        self.__alpha = 0.1
        self.__gamma = 0.99

        self.__prevState = False
        self.__prevAction = False
        self.__prevWorth = False

        self.Q = Q

    def act(self):
        self.__updateBeliefs()

        # in first tick we do not have enough information
        if self.__curTick > 0:
            self.__learn()

        # desires = self.__getDesires()
        # intention = self.__getNextAction(desires)
        intention = self.__egreedy()
        listener.addCaAction(intention)

        # Cooperation step
        # Broadcast it to peers that did not act yet
        startIdx = self.__peers.index(self) + 1
        if self.__willCooperate():
            listener.addCooperation(self._name)
            for i in range(startIdx, len(self.__peers)):
                self.__peers[i].receiveAction(intention)

        # keep last state/action/worth (QLearning)
        self.__prevAction = intention
        self.__prevState = self.__flattenState()
        self.__prevWorth = self.getWorth()

        self.__execute(intention)

        self.__peerActions = {
            Action.BUY: 0,
            Action.HOLD: 0,
            Action.SELL: 0,
            Action.SKIP: 0
        }
        self.__curTick += 1

    def __execute(self, action: Action) -> None:
        if action == Action.HOLD:
            # print("Holding")
            return
        elif action == Action.SELL:
            # print("Selling")
            self.placeSellOrder(1, self.__stockPrice * 1.2)
        elif action == Action.BUY:
            # print("Buying")
            nStocks = math.floor(self._balance * 0.8 / (self.__stockPrice * 1.2))
            self.placeBuyOrder(nStocks, self.__stockPrice * 1.2)
        else:
            return


    def __willCooperate(self) -> bool:
        return random.random() < self.__probCoop

    # cooperation method
    def receiveAction(self, action: Action):
        if action not in self.__peerActions:
            self.__peerActions[action] = 0

        self.__peerActions[action] += 1



    # QLearning methods
    def __getQValue(self, state: str, action: Action) -> float:
        if state in self.Q:
            return self.Q[state][action]
        return 1

    def __getMaxQ(self, state: str) -> (Action, float):
        skipQ = self.__getQValue(state, Action.SKIP)
        holdQ = self.__getQValue(state, Action.HOLD)
        buyQ = self.__getQValue(state, Action.BUY)
        sellQ = self.__getQValue(state, Action.SELL)

        bestActions = [Action.SKIP]
        maxQ = skipQ

        if holdQ > maxQ:
            maxQ = holdQ
            bestActions = [Action.HOLD]
        elif holdQ == maxQ:
            bestActions.append(Action.HOLD)

        if buyQ > maxQ:
            maxQ = buyQ
            bestActions = [Action.BUY]
        elif buyQ == maxQ:
            bestActions.append(Action.BUY)

        if sellQ > maxQ:
            maxQ = sellQ
            bestActions = [Action.SELL]
        elif sellQ == maxQ:
            bestActions.append(Action.SELL)

        return random.choice(bestActions), maxQ

    def __setQValue(self, state: str, action: Action, value: float) -> None:
        if state not in self.Q:
            self.Q[state] = {
                Action.HOLD: 1,
                Action.BUY: 1,
                Action.SELL: 1,
                Action.SKIP: 1,
            }

        self.Q[state][action] = value

    def __learn(self):
        r = self.getWorth() - self.__prevWorth
        prevQ = self.__getQValue(self.__prevState, self.__prevAction)
        
        curState = self.__flattenState()
        _, maxQ = self.__getMaxQ(curState)

        newQ = prevQ + self.__alpha * (r + self.__gamma * maxQ - prevQ)
        self.__setQValue(self.__prevState, self.__prevAction, newQ)

    def __egreedy(self):
        p = random.random()
        if p < self.__eps:
            return random.choice([Action.HOLD, Action.SELL, Action.BUY, Action.SKIP])
        else:
            return self.__getMaxQ(self.__flattenState())[0]

    def __flattenState(self) -> str:
        res = ""

        #  price is rising (up) / falling (down)
        oldest = max(-10, -len(self.__stockHistory))
        res += "U" if self.__stockHistory[-1] >= self.__stockHistory[oldest] else "D"

        #           L         M         H
        #  BUY:  [0, 20[, [20, 50[, [50, 100]
        #  HOLD: [0, 20[, [20, 50[, [50, 100]
        #  SELL: [0, 20[, [20, 50[, [50, 100]
        #  SKIP: [0, 20[, [20, 50[, [50, 100]
        b1 = 0.2
        b2 = 0.5

        buyingPeers = self.__peerActions[Action.BUY] / len(self.__peers)
        res += "B" + ("l" if buyingPeers < b1 else ("m" if buyingPeers < b2 else "h"))

        holdingPeers = self.__peerActions[Action.HOLD] / len(self.__peers)
        res += "H" + ("l" if holdingPeers < b1 else ("m" if holdingPeers < b2 else "h"))

        sellingPeers = self.__peerActions[Action.SELL] / len(self.__peers)
        res += "S" + ("l" if sellingPeers < b1 else ("m" if sellingPeers < b2 else "h"))

        skippingPeers = self.__peerActions[Action.SKIP] / len(self.__peers)
        res += "K" + ("l" if skippingPeers < b1 else ("m" if skippingPeers < b2 else "h"))

        #  Have stocks?
        res += "T" if self._stocks > 0 else "F"

        #  Have enough balance?
        res += "T" if self._balance > self.__stockPrice else "F"

        return res



    def __updateBeliefs(self):
        self.__stockPrice = self._market.getStockPrice()
        self.__stockHistory.append(self.__stockPrice)

        # perceive the current state
        self.__curState = self.__flattenState()


    # Le creme de la creme (sike)
    def __getDesires(self):
        desires = []

        desires.append(Desire(Action.HOLD, self.__calculateHoldStrength()))
        desires.append(Desire(Action.BUY, self.__calculateBuyStrength()))
        desires.append(Desire(Action.SELL, self.__calculateSellStrength()))
        desires.append(Desire(Action.SKIP, self.__calculateSkipStrength()))

        return desires

    def __calculateSkipStrength(self) -> int:
        return self.__probAct

    def __calculateHoldStrength(self) -> int:
        defaultStrength = 0.8
        holdingPeers = self.__peerActions[Action.HOLD] / len(self.__peers)
        return defaultStrength * 0.5 + holdingPeers * 0.5

    def __calculateBuyStrength(self) -> int:
        if self._balance == 0 or self.__curTick < self.__maxTicks * 0.1:
            return 0

        defaultStrength = 0.5
        buyingPeers = self.__peerActions[Action.BUY] / len(self.__peers)
        # TODO: do we want to consider if valley

        return defaultStrength * 0.5 + buyingPeers * 0.5

    def __calculateSellStrength(self) -> int:
        if self._stocks == 0:
            return 0

        defaultStrength = 0.5
        sellingPeers = self.__peerActions[Action.BUY] / len(self.__peers)
        priceStrength = self.__stockPrice / max(self.__stockHistory)

        return defaultStrength * 0.3 + sellingPeers * 0.5 + priceStrength * 0.2

    def __getNextAction(self, options: List[Desire]) -> Action:
        total = 0
        for opt in options:
            total += opt.strength

        if total == 0:
            return Action.SKIP

        choice = random.random() * total
        i = 0
        while choice > 0 and i < len(options):
            choice -= options[i].strength
            i+=1

        return options[i-1].action
