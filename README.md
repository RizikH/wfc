# Wave Function Collapse (Java)

A **from-scratch Java implementation** of the **Wave Function Collapse (WFC)** algorithm for procedural generation.  
This project emphasizes **correctness, clarity, and extensibility**, and is designed both as a learning resource and a portfolio-quality implementation.

The system supports **tile-based constraint solving**, **directional adjacency rules**, and **real-time visualization using Processing**.

---

## ✨ Features

- ✅ Core Wave Function Collapse algorithm
- 🧠 Entropy-based cell selection
- 🔄 Queue-based constraint propagation
- 🧩 Directional adjacency rules (N / E / S / W)
- 🎨 Processing-based visualization (zoom, pan, restart)
- 🌍 Terrain generation with edge & corner transition tiles
- 🧱 Clean modular architecture (Grid, Cell, Domain, Ruleset, Solver)
- 🧼 Optional post-processing (minimum region cleanup)

---

## 📂 Project Structure

```
src/main/java/com/rizikh/wfc
├── app            # Processing sketches (visualization)
├── core           # Core primitives (Direction, Pos)
├── model          # Grid, Cell, Domain
├── rules          # Rulesets (Terrain, Roads, etc.)
├── solver         # WFC solver implementation
├── tiles          # Tile enums (TerrainTile, RoadTile)
└── post           # Post-processing utilities
```

---

## 🛠 Requirements

- **Java 21**
- **Maven**
- *(Optional)* **Processing** (used for visualization)

---

## 🚀 Build

```bash
mvn clean package
```

---

## ▶️ Run (Visualization)

Example: run the terrain visualization sketch

```bash
mvn exec:java -Dexec.mainClass="com.rizikh.wfc.app.TerrainSketch"
```

> Make sure tile images exist under:
> ```
> src/main/resources/terrain/
> ```

---

## 🎮 Controls (Processing Viewer)

- **Mouse Wheel** — Zoom in / out  
- **Left Mouse Drag** — Pan the world  
- **Space** — Restart generation  

---

## 🧠 How It Works (High Level)

1. Each grid cell begins with a **domain** of possible tiles
2. The solver selects the cell with **minimum entropy**
3. A tile is chosen using **weighted randomness**
4. Constraints propagate to neighboring cells
5. The process repeats until:
   - the grid is **fully solved**, or
   - a **contradiction** is detected

Directional adjacency constraints are enforced using **bitmask-based compatibility checks** for performance and correctness.

---

## 🧩 Tiles & Rulesets

- Tiles are represented as enums (`TerrainTile`, `RoadTile`)
- Each tile defines the **biome/edge type** on all four sides
- Rulesets automatically generate adjacency masks by matching edges
- Transition tiles (edges & corners) eliminate speckling and contradictions

This design avoids hand-written adjacency tables and ensures **mutual compatibility** by construction.

---

## 🎯 Design Goals

- **Readable** — no black-box logic
- **Correct** — strict constraint enforcement
- **Extensible** — easy to add new tile sets or rulesets
- **Educational** — suitable for studying WFC internals

---

## 🔮 Future Improvements

- ⏳ Backtracking on contradiction
- 🌊 Weighted biome seeding
- 🗺 Chunked / infinite maps
- 🧬 Diagonal adjacency support
- 📦 Export generated maps to files

---

## 📜 License

MIT License — free to use, modify, and distribute.

---

## 👤 Author

**Rizik Haddad**  
Computer Science — Java · Algorithms · Procedural Generation
