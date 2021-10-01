import typing
import math
from Agent import Agent
from Debt import Debt
from Listener import listener
import random


if typing.TYPE_CHECKING:
    from StockMarket import StockMarket

class StrongIsolatedAgent(Agent):
    def __init__(self, name: str, market: 'StockMarket', balance: float, stocks: int, maxTicks: int):
        super().__init__(f'sia_{name}', market, balance, stocks)

        # number of previous ticks the 
        # agent considers when acting
        self.__tickWindowSize = 75
        self.__stockHistory = []

        # if price dropped below this threshold,
        # the agent will rebuy stocks
        self.__buyTreshold = 1

        # if price increased above this threshold,
        # the agent will short sell
        self.__sellThreshold = 3

        self.__ticksToRebuy: int = 1000

        self.__curTick: int = 0
        # maximum number of ticks in this simulation
        # used for the agend to decide if it will short sell
        self.__maxTicks: int = maxTicks

        self.__probAct: float = 0.3

        # stocks the agent short sold
        self.__debts = {}


    #################################
    # C: Actuators (OVERRIDDEN)
    #################################
    def placeBuyOrder(self, amount: int, value: float) -> None:
        self._market.receiveBuyOrder(self, amount, value)

    def placeSellOrder(self, amount: int, value: float) -> None:
        self._market.receiveSellOrder(self, amount, value)

    def sell(self, price: float) -> int:
        # TODO: call overridden method instead
        self._stocks -= 1
        self._balance += price

        # create new debt
        if self.__curTick not in self.__debts:
            self.__debts[self.__curTick] = Debt(self.__curTick + self.__ticksToRebuy, 1, price)
        else:
            nDebtStocks = self.__debts[self.__curTick].getNStocks()
            self.__debts[self.__curTick] = Debt(self.__curTick + self.__ticksToRebuy, nDebtStocks + 1, price)

    def buy(self, price: float) -> int:
        # TODO: call overridden method instead
        self._stocks += 1
        self._balance -= price
        
        earliestDebtTick = min(self.__debts.keys())
        self.__debts[earliestDebtTick].amortizeStocks(1)
        if self.__debts[earliestDebtTick].getNStocks() == 0:
            del self.__debts[earliestDebtTick]

    def act(self):
        # update latest stock prices
        self.__stockHistory.append(self._market.getStockPrice())
        if len(self.__stockHistory) > self.__tickWindowSize:
            # remove first element if size exceeds
            self.__stockHistory = self.__stockHistory[1:]



        # cover the position (handle current debts)
        for debt in self.__debts.values():
            nStocks, value = self.willRebuyDebt(debt)
            if nStocks > 0:
                # print(f'SIA place buy order in tick {self.__curTick} with {nStocks} stocks at price {value}')
                self.placeBuyOrder(nStocks, value)

        if (len(self.__debts) > 0):
            # Need to take care of debts, will not short sell
            self.__curTick += 1
            return

        nStocks, value = self.willShortSell()
        if nStocks > 0:    
            print(f'SIA place sell order in tick {self.__curTick} with {nStocks} stocks at price {value}')
            self.placeSellOrder(nStocks, value)

        self.__curTick += 1

    def willShortSell(self):
        # Factors that influence this decision:
        #  -> Stock Peak

        if len(self.__stockHistory) != self.__tickWindowSize or random.random() > self.__probAct:
            # do not short sell if we don't have enough info
            return 0, 0

        if self.__curTick + self.__ticksToRebuy >= self.__maxTicks:
            return 0, 0

        maxPrice: float = 0
        maxIdx: float = 0
        for idx, price in enumerate(self.__stockHistory):
            if price >= maxPrice:
                maxPrice = price
                maxIdx = idx

        if maxIdx >= self.__tickWindowSize - 5:
            # only sell if this maximum is fresh

            minPrice = min(self.__stockHistory[:maxIdx])
            if (maxPrice - minPrice) > self.__sellThreshold:
                return int(self._market.getNStocks()*0.4), self.__stockHistory[-1]

        return 0, 0

    def willRebuyDebt(self, debt: Debt) -> float:
        if debt.getDeadline() <= self.__curTick:
            print(f'Repaying overdue debt at tick {self.__curTick}')
            return debt.getNStocks(), math.inf


        # Factors that influence this decision:
        #  -> Stock Valley
        #  -> TODO Price at what it sold the stock
        #  -> TODO Time until deadline finishes
        #     (if it's rising until the deadline we better buy it now)

        # Find stock valley
        minPrice = math.inf
        minIdx = 0
        for idx, price in enumerate(self.__stockHistory):
            if price <= minPrice:
                minPrice = price
                minIdx = idx

        if minIdx >= self.__tickWindowSize - 5:
            maxPrice = max(self.__stockHistory[:minIdx])
            if (maxPrice - minPrice > self.__buyTreshold and
                # assure minimum is not current value.
                # It may drop even more in the future
                minIdx != len(self.__stockHistory) and
                debt.getSellValue() > self.__stockHistory[-1] * 1
            ):

                return debt.getNStocks(), self.__stockHistory[-1] * 1

        return 0, 0
