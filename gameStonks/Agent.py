import typing
from abc import ABC, abstractmethod
from Listener import listener

# help mypy find symbol
if typing.TYPE_CHECKING:
    from StockMarket import StockMarket


class Agent(ABC):
    def __init__(self, name: str, market: 'StockMarket', balance: float, stocks: int):
        self._name = name
        self._market = market
        self._balance = balance
        self._stocks = stocks

    #################################
    # A: decision
    #################################
    @abstractmethod
    def act(self) -> None:
        pass
    
    #################################
    # B: Sensors
    #################################
    def getStockPrice(self) -> float:
        return self._market.getStockPrice()

    #################################
    # C: Actuators
    #################################
    def placeBuyOrder(self, amount: int, value: float) -> None:
        if amount*value > self._balance:
            print("ERROR: Not enough balance")
            exit()

        self._market.receiveBuyOrder(self, amount, value)


    def placeSellOrder(self, amount: int, value: float) -> None:
        if amount > self._stocks:
            print("ERROR: Not enough stocks to sell")
            exit()

        self._market.receiveSellOrder(self, amount, value)

    def buy(self, price: float) -> None:
        self._stocks += 1
        self._balance -= price
        
        

    def sell(self, price: float) -> None:
        self._stocks -= 1
        self._balance += price

    #################################
    # D: Helper Methods
    #################################
    def getWorth(self):
        return self._balance + self._stocks*self._market.getStockPrice()

    def getWealth(self):
        return self._balance

    def getName(self):
        return self._name
