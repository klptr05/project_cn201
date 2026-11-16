```mermaid
classDiagram
    direction TB

    %% ==========================================
    %% 1. Villager & Animal Hierarchy
    %% ==========================================
    class Villager {
        <<abstract>>
        +MAX_FRIENDSHIP: int$
        #FIRST_REQUEST_DAY: int$
        #name: String
        #col: int
        #row: int
        #request: Request
        #friendship: int
        #requestsDone: int
        #chats: int
        #currentDay: int
        +Villager(name: String, col: int, row: int)
        +talk(farmer: Farmer)* String
        +draw(g: Graphics2D, frames: int)* void
        +newDay(day: int, rng: Random) String
        +setCurrentDay(day: int) void
        +getName() String
        +getCol() int
        +getRow() int
        +getRequest() Request
        +getFriendship() int
        +getRequestsDone() int
        +setRequest(request: Request) void
        +restore(friendship: int, requestsDone: int) void
    }

    class ajKhiao {
        -CHAT1: String[]$
        -CHAT2: String[]$
        -CHAT3: String[]$
        -chat1Index: int
        -askedQuestion: boolean
        -questCompleted: boolean
        +ajKhiao(name: String, col: int, row: int)
        +shouldAskQuestion() boolean
        +getQuestionText() String
        +answerQuestion(farmer: Farmer, yes: boolean) String
        +talk(farmer: Farmer) String
        -randomChat3() String
        +newDay(day: int, rng: Random) String
        +isAskedQuestion() boolean
        +isQuestCompleted() boolean
        +restoreState(askedQuestion: boolean, questCompleted: boolean) void
        +draw(g: Graphics2D, frames: int) void
    }

    class mrsDaeng {
        -CHAT1: String[]$
        +mrsDaeng(name: String, col: int, row: int)
        +talk(farmer: Farmer) String
        +draw(g: Graphics2D, frames: int) void
    }

    class Animal {
        <<abstract>>
        +MAX_FRIENDSHIP: int$
        #name: String
        #col: int
        #row: int
        #friendship: int
        #chats: int
        +Animal(name: String, col: int, row: int)
        +talk(farmer: Farmer)* String
        +draw(g: Graphics2D, frames: int)* void
        +getName() String
        +getCol() int
        +getRow() int
        +getFriendship() int
        +restore(friendship: int) void
    }

    class meengern {
        -CHAT1: String[]$
        -countforFirstTalk: int
        +meengern(name: String, col: int, row: int)
        +talk(farmer: Farmer) String
        +draw(g: Graphics2D, frames: int) void
    }

    ajKhiao --|> Villager
    mrsDaeng --|> Villager
    meengern --|> Animal

    %% ==========================================
    %% 2. Weather Strategy & Factory Pattern
    %% ==========================================
    class RainChanceStrategy {
        <<interface>>
        +shouldRain(rng: Random) boolean
    }

    class NormalRainChanceStrategy {
        +shouldRain(rng: Random) boolean
    }

    class FiftyRainChanceStrategy {
        +shouldRain(rng: Random) boolean
    }

    class RainChanceStrategyFactory {
        +create(name: String)$ RainChanceStrategy
    }

    NormalRainChanceStrategy ..|> RainChanceStrategy
    FiftyRainChanceStrategy ..|> RainChanceStrategy
    RainChanceStrategyFactory ..> NormalRainChanceStrategy : creates
    RainChanceStrategyFactory ..> FiftyRainChanceStrategy : creates

    %% ==========================================
    %% 3. Custom Exception Hierarchy
    %% ==========================================
    class FarmException {
        <<modified>>
        +FarmException(message: String)
    }

    class InsufficientEnergyException {
        +InsufficientEnergyException(message: String)
    }

    class InsufficientGoldException {
        +InsufficientGoldException(message: String)
    }

    class OutOfSeedsException {
        +OutOfSeedsException(message: String)
    }

    class OutOfWaterException {
        +OutOfWaterException(message: String)
    }

    InsufficientEnergyException --|> FarmException
    InsufficientGoldException --|> FarmException
    OutOfSeedsException --|> FarmException
    OutOfWaterException --|> FarmException

    %% ==========================================
    %% 4. Dialogue Choice UI & Test
    %% ==========================================
    class Screen {
        <<interface>>
        +keyPressed(keyCode: int) void
        +update() void
        +draw(g: Graphics2D) void
    }

    class ChoiceScreen {
        -game: GamePanel
        -previous: Screen
        -speaker: String
        -question: String
        -menu: Menu
        +ChoiceScreen(game: GamePanel, previous: Screen, speaker: String, question: String, onAnswer: Consumer~Boolean~)
        +keyPressed(code: int) void
        +draw(g: Graphics2D) void
    }

    class FarmGoldenOutputTest {
        +main(args: String[])$ void
        -sha256(image: BufferedImage)$ String
    }

    ChoiceScreen ..|> Screen

    %% ==========================================
    %% 5. Connections to Modified Core Classes
    %% ==========================================
    class Farm {
        <<modified>>
        -villagers: List~Villager~
        -animals: List~Animal~
        +getVillagers() List~Villager~
        +getAnimals() List~Animal~
        +animalAt(col: int, row: int) Animal
    }

    class Farmer {
        <<modified>>
        -choice: boolean
        +getChoice() boolean
        +setChoice(choice: boolean) void
    }

    class GameClock {
        <<modified>>
        -rainStrategy: RainChanceStrategy
        +GameClock()
        +GameClock(rainStrategy: RainChanceStrategy)
    }

    class FarmScreen {
        <<modified>>
        -talkTo(villager: Villager) void
        -talkTo(animal: Animal) void
        -drawEntities(g: Graphics2D) void
    }

    Farm *-- "*" Villager
    Farm *-- "*" Animal
    GameClock o-- "1" RainChanceStrategy
    FarmScreen ..> ChoiceScreen : opens
    ajKhiao ..> Farmer : updates choice
    FarmGoldenOutputTest ..> Farm : renders & hashes
    ```