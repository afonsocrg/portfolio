
class Debt:
    def __init__(self, deadline: int, nStocks: int, sellValue: float):
        self.__deadline = deadline
        self.__nStocks = nStocks
        self.__sellValue = sellValue

    def getDeadline(self):
        return self.__deadline

    def getNStocks(self):
        return self.__nStocks

    def getSellValue(self):
        return self.__sellValue

    def amortizeStocks(self, nStocks):
        if nStocks > self.__nStocks:
            print("AMORTIZING MORE STOCKS THAN THE ONES OF THE DEBT")
            exit()

        self.__nStocks -= nStocks
