from Agent import Agent

class Order:
    def __init__(self, agent: "Agent", value: float):
        self.agent: "Agent" = agent
        self.value: float = value

    def __lt__(self, other: "Order") -> bool:
        return self.value < other.value
