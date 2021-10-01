from Order import Order
from Agent import Agent
from typing import List
from Listener import listener
import math

# Holds a single company
class StockMarket:

    def __init__(self, price: float, nStocks: int):
        self.__stockPrice: float = price
        self.__buyOrders: List['Order']  = []
        self.__sellOrders: List['Order'] = []
        self.__nStocks: int = nStocks

    #################################
    # Getters
    #################################
    def getStockPrice(self) -> float:
        return self.__stockPrice

    def getNStocks(self) -> int:
        return self.__nStocks

    # places <amount> buy orders, each at
    # value <value>
    def receiveBuyOrder(self, agent: "Agent", amount: int, value: float) -> None:
        if(value < 0):
            print(f'ERROR: Negative Buy offer by agent {agent.getName()}')
            exit()
        for i in range(amount):
            self.__buyOrders.append(Order(agent, value))

    # places <amount> sell orders, each at
    # value <value>
    def receiveSellOrder(self, agent: "Agent", amount: int, value: float) -> None:
        if(value < 0):
            print(f'ERROR: Negative Sell offer by by agent {agent.getName()}')
            exit()
        for i in range(amount):
            self.__sellOrders.append(Order(agent, value))


    def matchOrders(self) -> None:
        # double auction algorithm

        if len(self.__buyOrders) == 0 or len(self.__sellOrders) == 0:
            print('No matches')
            self.__buyOrders = []
            self.__sellOrders = []
            return

        # sort buyers decreasing order
        self.__buyOrders.sort(reverse=True)
        # sort sellers increasing order
        self.__sellOrders.sort()

        # remove infinite valued buy orders
        i = 0
        maxSellValue = self.__sellOrders[-1].value
        while self.__buyOrders[i].value == math.inf:
            self.__buyOrders[i] = Order(self.__buyOrders[i].agent, maxSellValue)
            i+=1

        min_len = min(len(self.__buyOrders), len(self.__sellOrders))
        k = 0
        while k < min_len and self.__buyOrders[k].value >= self.__sellOrders[k].value:
            k+=1

        if k != 0:
            # clearing price is average of last matching offers
            clearingPrice = (self.__buyOrders[k-1].value + self.__sellOrders[k-1].value) / 2

            # confirm the matched transactions
            for i in range(k):
                self.__buyOrders[i].agent.buy(clearingPrice)
                self.__sellOrders[i].agent.sell(clearingPrice)

            self.__stockPrice = clearingPrice

        listener.newStockPrice(self.__stockPrice)
        print(f"Matched {k} ({len(self.__buyOrders)}:{len(self.__sellOrders)}) offers. New price: {self.__stockPrice}")
        self.__buyOrders = []
        self.__sellOrders = []
        # return k
