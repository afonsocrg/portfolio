# What the Gang of Four’s original Singleton Pattern
# might look like in Python.
import matplotlib.pyplot as plt
import pandas as pd
from Action import Action
from datetime import datetime
from pathlib import Path

PLOT_DIR = "plots"

class Listener(object):
    _instance = None
    

    def __init__(self):
        raise RuntimeError('Call instance() instead')

    @classmethod
    def instance(cls):
        if cls._instance is None:
            cls._instance = cls.__new__(cls)
            cls._instance.tick = 0
            cls._instance.stockPrice = {}
            cls._instance.agentWealths = {}
            cls._instance.agentWorths = {} 
            cls._instance.cooperations = {} 
            cls._instance.caActions = {} 

            # create directory to store plots
            path = (Path(PLOT_DIR) / datetime.now().strftime("%Y%m%d%H%M%S")).resolve()
            path.mkdir(parents=True)
            cls._instance.plotDir = path
        return cls._instance
    
    def newTick(self):
        self.tick += 1

    def newStockPrice(self, price: float):
        if len(self.stockPrice) == 0:
            self.initialPrice = price

        self.stockPrice[self.tick] = price

    def addAgentWealth(self, agentName: str, agentBalance: float):
        if self.tick not in self.agentWealths:
            self.agentWealths[self.tick] = {}
        self.agentWealths[self.tick][agentName] = agentBalance

    def addAgentWorth(self, agentName: str, agentBalance: float):
        if self.tick not in self.agentWorths:
            self.agentWorths[self.tick] = {}
        self.agentWorths[self.tick][agentName] = agentBalance

    def addCooperation(self, agentName: str):
        if self.tick not in self.cooperations:
            self.cooperations[self.tick] = []

        self.cooperations[self.tick].append(agentName)

    def addCaAction(self, action):
        if self.tick not in self.caActions:
            self.caActions[self.tick] = {}

        if action not in self.caActions[self.tick]:
            self.caActions[self.tick][action] = 0

        self.caActions[self.tick][action] += 1

    def __plotStockTickVariation(self, axis):
        xx = []
        yy = []
        for (t, v) in self.stockPrice.items():
            if t-1 in self.stockPrice:
                yy.append(v - self.stockPrice[t-1])
                xx.append(t)

        axis.plot(xx, yy)

    def __plotStockBeginDiff(self, axis):
        xx = []
        yy = []
        for (t, v) in self.stockPrice.items():
            yy.append(v - self.initialPrice)
            xx.append(t)

        axis.plot(xx, yy)

    def __plotMeanAgentWealth(self, axis):
        xx = []
        yy = []
        for (t, d) in self.agentWealths.items():
            xx.append(t)
            yy.append(sum(d.values())/len(d))
        
        axis.plot(xx, yy)

    def __plotMeanAgentWorth(self, axis):
        xx = []
        yy = []
        for (t, d) in self.agentWorths.items():
            xx.append(t)
            yy.append(sum(d.values())/len(d))
        
        axis.plot(xx, yy)

    def __plotCoopPercentPerTick(self):
        plt.figure(figsize=(10,5))

        # number of cooperative agents
        n = len([a for a in self.agentWorths[0].keys() if "ca_" in a])

        xx = self.cooperations.keys()
        yy = [ len(l)/n for l in self.cooperations.values() ]

        plt.ylim(0, 1)
        plt.ylabel("Coop. percent.")
        plt.xlabel("Ticks")
        plt.plot(xx, yy)
        plt.title("Cooperation percentage per tick")
        plt.savefig(self.plotDir / '1_coopPercentPerTick.png')

    def __plotStockPrices(self):
        plt.figure(figsize=(10,5))

        xx = []
        yy = []
        for (t, v) in self.stockPrice.items():
            xx.append(t)
            yy.append(v)

        plt.plot(xx, yy)
        plt.ylabel("Stock Price")
        plt.xlabel("Ticks")
        plt.title('Stock price per tick')
        plt.savefig(self.plotDir / '2_stockPricePerTick.png')


    def __plotFinalAgentsWorth(self):
        plt.figure(figsize=(10,5))

        lastTick = max(self.agentWorths.keys())
        xx = []
        heights = []
        wia_sum = 0
        wia_count = 0
        ca_sum = 0
        ca_count = 0
        for agent, balance in self.agentWorths[lastTick].items():
            agentClass = agent[:agent.find("_")]
            if agent == "sia_0":
                xx.append("Strong Isolated\nAgents")
                heights.append(balance - self.agentWorths[0][agent])
            elif agentClass == "wia":
                wia_sum += balance - self.agentWorths[0][agent]
                wia_count += 1
            elif agentClass == "ca":
                ca_sum += balance - self.agentWorths[0][agent]
                ca_count += 1

        wia_avg = 0
        ca_avg = 0
        if wia_count != 0:
            wia_avg = wia_sum / wia_count
        if ca_count != 0:
            ca_avg = ca_sum / ca_count

        xx.append("Weak Isolated\nAgents")
        heights.append(wia_avg)
        xx.append("Cooperative\nAgents")
        heights.append(ca_avg)

        plt.bar(x=xx, height=heights)
        plt.ylabel("Final worth balance")
        plt.title("Average final worth balance per agent type")
        plt.savefig(self.plotDir / '3_avgFianlWorthBalancePerAgent.png')

    def __plotFinalAgentsWealth(self):
        plt.figure(figsize=(10,5))

        lastTick = max(self.agentWealths.keys())
        xx = []
        heights = []
        wia_sum = 0
        wia_count = 0
        ca_sum = 0
        ca_count = 0
        for agent, balance in self.agentWealths[lastTick].items():
            agentClass = agent[:agent.find("_")]
            if agent == "sia_0":
                xx.append("Strong Isolated\nAgents")
                heights.append(balance - self.agentWealths[0][agent])
            elif agentClass == "wia":
                wia_sum += balance - self.agentWealths[0][agent]
                wia_count += 1
            elif agentClass == "ca":
                ca_sum += balance - self.agentWealths[0][agent]
                ca_count += 1

        wia_avg = 0
        ca_avg = 0
        if wia_count != 0:
            wia_avg = wia_sum / wia_count
        if ca_count != 0:
            ca_avg = ca_sum / ca_count

        xx.append("Weak Isolated\nAgents")
        heights.append(wia_avg)
        xx.append("Cooperative\nAgents")
        heights.append(ca_avg)

        plt.bar(x=xx, height=heights)
        plt.ylabel("Final wealth balance")
        plt.title("Average final wealth balance per agent type")
        plt.savefig(self.plotDir / '4_avgFinalWealthBalancePerAgent.png')

    def __plotAvgWorthPerAgentType(self):
        plt.figure(figsize=(10,5))

        xx = []
        yy_wia = []
        yy_sia = []
        yy_ca  = []

        lastTick = max(self.agentWorths.keys())
        for t in range(1, lastTick):
            xx.append(t)
            wia_sum = 0
            sia_sum = 0
            ca_sum  = 0

            wia_count = 0
            ca_count  = 0
            for agent, balance in self.agentWorths[t].items():
                agentClass = agent[:agent.find("_")]
                y = balance - self.agentWorths[0][agent]
                if agent == "sia_0":
                    yy_sia.append(y)
                elif agentClass == "wia":
                    wia_sum += y
                    wia_count += 1
                elif agentClass == "ca":
                    ca_sum += y
                    ca_count += 1

            if wia_count != 0:
                yy_wia.append(wia_sum / wia_count)
            if ca_count != 0:
                yy_ca.append(ca_sum / ca_count)

        plt.title("Average Worth Balance per tick")
        plt.ylabel("Mean Agent Worth Balance")
        plt.xlabel("Ticks")
        plt.plot(xx, yy_wia, label="WIA")
        plt.plot(xx, yy_sia, label="SIA")
        plt.plot(xx, yy_ca,  label="CA")
        # plt.legend(bbox_to_anchor = (0.75, 0.21), ncol = 3)
        plt.legend(["WIA", "SIA", "CA"])

        plt.savefig(self.plotDir / '5_avgWorthPerTick.png')

    def __plotCooperationImmediateEffect(self):
        plt.figure(figsize=(10,5))

        coop = []
        ncoop = []

        lastTick = max(self.agentWorths.keys())
        for t in range(0, lastTick, 1):
            for agent, worth in self.agentWorths[t].items():
                if "ca" not in agent:
                    continue
                
                if agent in self.cooperations[t]:
                    coop.append(self.agentWorths[t+1][agent] - worth)
                else:
                    ncoop.append(self.agentWorths[t+1][agent] - worth)

        # heights = [coop_sum, ncoop_sum]
        xx = [coop, ncoop]
        plt.title("Cooperation immediate effect in agent worth")

        red_square = dict(markerfacecolor='g', marker='o', alpha=0.2)

        plt.boxplot(x=xx, labels=["Cooperate", "Not cooperate"], flierprops=red_square)
        plt.savefig(self.plotDir / '6_cooperationImmediateEffect.png')

    def __plotCaActions(self):
        plt.figure(figsize=(10,5))

        nca = sum([1 for agent in self.agentWorths[0].keys() if "ca" in agent])
        if nca == 0:
            return

        actions = [Action.SKIP, Action.HOLD, Action.SELL, Action.BUY]
        lastTick = max(self.agentWorths.keys())

        xx = []
        yy = {}
        for a in actions:
            yy[a] = []

        variationThreshold = 3
        for t in range(0, lastTick, 1):
            if abs(self.stockPrice[t] - self.stockPrice[t+1]) < variationThreshold:
                # ignore ticks with small variation
                continue

            xx.append(t)
            s = 0
            for a in actions:
                if a not in self.caActions[t]:
                    self.caActions[t][a] = 0
                
                percent = self.caActions[t][a] / nca
                s += percent
                yy[a].append(s)

        for a in actions:
            df = pd.DataFrame(yy[a])
            df.rolling(100, min_periods=1)
            plt.plot(xx, df.values.tolist())

        plt.fill_between(xx, 0, yy[actions[0]])
        plt.fill_between(xx, yy[actions[0]], yy[actions[1]])
        plt.fill_between(xx, yy[actions[1]], yy[actions[2]])
        plt.fill_between(xx, yy[actions[2]], yy[actions[3]])

        plt.savefig(self.plotDir / '7_caActions.png')


    def spitData(self):
        # self.__plotCoopPercentPerTick()
        # self.__plotStockPrices()
        # self.__plotFinalAgentsWorth()
        # self.__plotFinalAgentsWealth()
        # self.__plotAvgWorthPerAgentType()
        # self.__plotCooperationImmediateEffect()
        # self.__plotCaActions()
        print(sum(self.stockPrice.values())/len(self.stockPrice.keys()))

listener = Listener.instance()
