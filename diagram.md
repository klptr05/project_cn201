```mermaid
classDiagram
    direction TB

    %% ==========================================
    %% 1. Villager & Animal Hierarchy
    %% ==========================================
    class Villager {
        <<abstract>>
        +MAX_FRIENDSHIP: int$
        +FIRST_REQUEST_DAY: int
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
        <<interface, new>>
        +shouldRain(rng: Random) boolean
    }

    class NormalRainChanceStrategy {
        <<new>>
        +shouldRain(rng: Random) boolean
    }

    class FiftyRainChanceStrategy {
        <<new>>
        +shouldRain(rng: Random) boolean
    }

    class RainChanceStrategyFactory {
        <<new>>
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
        <<new>>
        +InsufficientEnergyException(message: String)
    }

    class InsufficientGoldException {
        <<new>>
        +InsufficientGoldException(message: String)
    }

    class OutOfSeedsException {
        <<new>>
        +OutOfSeedsException(message: String)
    }

    class OutOfWaterException {
        <<new>>
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
        <<new>>
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
        +villagerAt(col: int, row: int) Villager
        +animalAt(col: int, row: int) Animal
        +tileAt(col: int, row: int) Tile
        -drawTile()
        -drawSoil()
        -drawPond()
        -drawFence()
    }

    class Farmer {
        <<modified>>
        -choice: boolean
        +getChoice() boolean
        +setChoice(choice: boolean) void
        Exception changed from FarmException to InsufficientGoldException
    }

    class GameClock {
        <<modified>>
        -rainStrategy: RainChanceStrategy
        +GameClock()
        +GameClock(rainStrategy: RainChanceStrategy)
        +nextDay(rng: Random) void
    }

    class FarmScreen {
        <<modified>>
        -talkTo(villager: Villager) void
        -talkTo(animal: Animal) void
        -drawEntities(g: Graphics2D) void
        -sleep(passedOut: boolean) void
        -drawMessage(g: Graphics2D) void
        -doAction() void
        -hint() String
    }

    class GamePanel {
        <<modified>>
        -rainStrategy: RainChanceStrategy
        +GamePanel()
        +GamePanel(rainStrategy: RainChanceStrategy)
        +getRainStrategy() RainChanceStrategy
        +setScreen(next: Screen) void
        +showTitle() void
    }

    class SaveGame {
        <<modified>>
        -hashLine(line: String)$ String
        -writeCheckedLine(out: PrintWriter, line: String)$ void
        +describe() String
        +save(farm: Farm, farmer: Farmer, clock: GameClock) void
        +loadInto(farm: Farm, farmer: Farmer, clock: GameClock) void
        -findVillager(farm: Farm, id: String) Villager
        -findAnimal(farm: Farm, id: String) Animal
    }

    class Hud {
        <<modified>>
        -reqList: ArrayList
        .. displays villager names and quests; adjusts font and position based on quest count ..
    }

    class EndingScreen {
        <<modified>>
        .. adding more characters to the Villager class and adding the Animal class to call each member method in the class by using for loop. ..
    }

    class SummaryScreen {
        <<modified>>
        +draw(g: Graphics2D) void
    }

    class TitleScreen {
        <<modified>>
        +draw(g: Graphics2D) void
    }

    class Checklist {
        <<enumeration>>
        TALK_KHIAO("Say hello to Aj. Khiao"),
        TALK_MEENGERN("Say hello to Meengern"),
    }
    
    class WateringCan {
        <<modified>>
        .. throws OutOfWaterException instead of FarmException ..
    }

    Farm *-- "*" Villager
    Farm *-- "*" Animal
    GameClock o-- "1" RainChanceStrategy
    FarmScreen ..> ChoiceScreen : opens
    ajKhiao ..> Farmer : updates choice
    FarmGoldenOutputTest ..> Farm : renders & hashes
    FarmScreen --> Hud
    GamePanel --> Screen
    GamePanel --> SaveGame
    SaveGame ..> Villager : saves and loads
    SaveGame ..> Animal : saves and loads
    ```