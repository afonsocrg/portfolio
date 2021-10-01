import typing
from Agent import Agent
from Listener import listener
import random

# help mypy find symbol
if typing.TYPE_CHECKING:
    from StockMarket import StockMarket

class WeakIsolatedAgent(Agent):

    def __init__(self, name: str, market: 'StockMarket', balance: float, stocks: int):
        super().__init__(f'wia_{name}', market, balance, stocks)
        self.__probAct: float = 0.8

        # max percentage of stock price that the agent will change
        self.__maxBargain: float = 0.1
        self.__greed: float = 0.5


    def act(self):
        price: float = self.getStockPrice()

        # sometimes these agents do not want to act...
        if random.random() > self.__probAct or (self._balance == 0 and self._stocks == 0):
            # print(f'{self._name} skipped')
            return


        # buy if 1) no stocks or 2) stocks and balance and chance
        if self._stocks == 0 or (self._balance != 0 and random.random() > 0.5):

            # offer = self.__bargainOffer(price)
            # offer = min(offer, self._balance)


            # more likely to decrease price
            bargain: float = (random.random()*2 - 1 - self.__greed) * self.__maxBargain
            offer: float = (1 + bargain) * price
            offer = min(offer, self._balance)

            self.placeBuyOrder(1, offer)

            # print(f"{self._name} is buying at {offer}")
            return


        # sell otherwise: 1) no balance or 2) stock and balance and chance
        else:
            # offer = self.__bargainOffer(price)

            # more likely to increase price
            bargain: float = (random.random()*2 - 1 + self.__greed) * self.__maxBargain
            offer: float = (1 + bargain) * price
            # print(f"{self._name} is selling at {offer}")
            self.placeSellOrder(1, offer)
            return

    def __bargainOffer(self, price: float) -> float:
        # percentage in [-maxBargain, maxBargain]
        bargain: float = (random.random()*2 - 1) * self.__maxBargain

        return (1 + bargain) * price

