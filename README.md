# Battleship Test Game — Algorithm Explanation

## Overview

On a high level explanation I followed the **Hunt and Target** strategy.

The basic idea is:

- We randomly hunt for the ship
- Whenever we hit a ship we try to look at the non-diagonal cells
- After we got the direction of the ship we hit the direction cells until it sunk

### Game Characteristics

- There are 1-4 block size ships
- When we hit a block of ship, its diagonal cells are automatically grayed out — that means any near cell can't have a ship or a sunk ship. So no ship is touching each other.

---

## Code Implementation and Inner Details

### Step 1 — Getting the Game Board Ready

Once we navigated to the desired url:

1. First we selected a random opponent.
2. After selecting the opponent we randomly arranged the ship using the random function, which produces a number between 0-15 as required by the task.
3. Once the board is arranged, we clicked the play button.
4. We waited for the opponent to join using the locator wait strategy.

---

### Step 2 — Starting the Game and Getting the Final Result

We have a method `startPlayingGame()` which returns the `GameResult` enum, which contains 4 results (`WIN`, `DEFEAT`, `OPPONENT_LEFT`, `TIMEOUT`) with the desired description.

`startPlayingGame()` starts by:

1. Taking an object of `GameStrategy` class, which contains all the methods related to our algorithm to play the battleship game.
2. Defining a deadline by taking the timeout from `Settings`.
3. Reading all the cells of the opponent board and storing them inside a list of strings using `saveBoardInitialCells()` from the `GamePage` class.
   - This method takes all the `<td>` tags' `outerHTML` attribute and saves it inside a list of strings.
   - This is used later to determine any change from the initial board to the current board.

Once it saved the initial cells' `outerHTML` attribute values to the list, it enters a **while loop** that keeps running until it meets the deadline. After the loop, it returns `GameResult.TIMEOUT` since the game did not finish within the timeout.

---

## Walking Through the While Loop

### 1. Check the Game Outcome

Since we need to check a lot of things while playing — for example, if the player has left, or the opponent won, or we have won — after we enter the while loop we need to check the game outcome.

- It always checks in the loop if any outcome is there or not, using `gamePage.getGameOutcome()`.
- This method just looks for the element for victory / defeat / opponentLeft, to see if they are visible.
- If any one of them is visible, that means we have an outcome, so at the start of the loop it checks for an outcome.

### 2. Check Whose Turn It Is

- If it is **not** my turn, then I need to wait for some time and check again if it's my turn or not, because the opponent is having his turn.
- In this block, if it's not my turn then we `continue`, and the while loop cycles again and checks the condition.

### 3. Sync the Board with Cells the System Already Marked

On each cycle, once it's confirmed to be my turn:

- We first take all the marked cells from the opponent board using `gamePage.getMarkedCells()`.
- Then, for each cell we use `strategy.markUnknownCell()`.

Basically what we are doing is:

- When we hit a cell (hit / miss), we have the cell status and we are storing it.
- But when we get a hit or sink a ship, the cells which are automatically grayed out by the system — the bot does not have that information.
- So in each cycle:
  - Inside `gamePage.getMarkedCells()`, it first gets the opponent board's all `<td>` `outerHTML` attribute values and matches them with the initial cells' attribute value list.
  - Whenever it does not find a match, it adds a cell with the particular row and column where it was changed.
  - Then, for all these unknown cells, the bot's board is marked with `CellStatus.MISS` by `strategy.markUnknownCell()`.

### 4. Finding the Next Perfect / Optimized Cell to Target

We use `strategy.nextMove()`, which returns a target `Cell`.

Inside the method:

1. We first check a flag — the list of `currentHits`.
2. It checks whether we already have a hit on the board or not.

**Case 1 — No current hit (`currentHits` is empty):**

- That means there is no hit on the board, so we go to the `hunt()` method.
- Inside `hunt()`:
  - We go through the bot's board and check all the cells which have `UNKNOWN` status, adding those cells to a candidate array.
  - We also keep a **parityCells** list with higher-probability cells.
  - Using the random function, we pick a random cell and return it as the target.

**Case 2 — There is already a current hit:**

- Since we already have a current hit, we need to look at its non-diagonal cells.
- We first check how many `currentHits` are in the list:
  - **If it's one** → we don't have a direction yet. We randomly select a non-diagonal cell using `probeAround()`.
    - This method looks at the target cell's non-diagonal cells, picks one, and returns it as the next target.
  - **If there is more than one current hit** → we have the direction. So we continue along it using `continueLine()`.
    - This method first determines if the line is vertical or horizontal.
    - Then it finds the start and end of the line.
    - If there are unknown cells at the start or end, it hits them.
    - If there are no unknown cells left (all known and inside `currentHits`), that means the ship is sunk, and it returns `null`.

**Handling the `null` result:**

- Whenever `nextMove()` gets `null`, that means the ship is sunk, and we call `finishShip()`.
- Inside `finishShip()`:
  - We clear the `currentHits` array — but before that, we mark the ship's cells as `CellStatus.SUNK`.
  - We also mark all its neighbour cells with `CellStatus.MISS` inside `markAround()`.

**`markAround()` method:**

- Takes a cell and a boolean flag, deciding whether to mark the diagonal or non-diagonal cells too.
- It loops through all 9 cells around the cell (itself included) and marks them accordingly, depending on the condition.

### 5. Shooting the Target Cell

Using `nextMove()` we got the perfect cell to check — now we shoot it.

We use `gamePage.shootCell()` to shoot the cell. This method returns a shoot result: `MISS`, `HIT`, or `SUNK`.

This method:

1. Takes all the cells from the opponent board.
2. Fetches the exact cell (via row and column) from the board as a `WebElement` and clicks on it.
3. *FYI:* before clicking, it stores the element's `outerHTML` property, to check how it changes after the click.
4. After clicking, it waits for any changes.
5. Then checks whose turn it is:
   - If it's the opponent's turn → it was **not** a hit, it was a **miss**.
   - If it's still our turn → it was a **hit**, so `shootCell()` returns `HIT`.
6. If the element's `outerHTML` did not change at all, that means the cell was already checked — we just mark it `MISS` using `strategy.markUnknownCell(cell)` and continue.

**Registering the result:**

- After we get a shoot status (hit or miss), we register it on the bot's board using `strategy.registerResult()`.
- Inside this method:
  - If the result is a miss, we just update the board and return.
  - If it was a hit, we update the board cell as a hit, and also mark its diagonal cells as `MISS`, since they can't contain a ship anymore.

---

## Ending the Game

While the loop keeps running, once the game has a result and it's visible on the screen, the game ends immediately, and the result is passed to the `assert` method in the main test.

- If the game was a **WIN**, the test **passes**.
- If it's not a win, it **fails**.
