# 🦆 Maps & DaleDB

**A from-scratch implementation of the Map ADT in Java: a linear-probing HashMap, a self-balancing AVL-backed TreeMap, and a partitioned NoSQL-style database built entirely on top of them.**

![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white)
![Data Structures](https://img.shields.io/badge/Topic-Data%20Structures%20%26%20Algorithms-blue)
![Course](https://img.shields.io/badge/Georgia%20Tech-CS%201332-b3a369)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen)

---

## 📖 Overview

This project builds two different Map implementations from the ground up, then combines them into a small database that tracks activity across "ponds." No `java.util` map classes are used anywhere, so every lookup, insert, and eviction runs on structures I wrote myself.

| Part | Focus | What it covers |
|------|-------|----------------|
| **1. HashMap** | Hashing | Linear probing, soft deletes, resizing, custom iterator |
| **2. TreeMap** | Balanced trees | AVL rotations, key-ordered map, range queries, in-order iterator |
| **3. DaleDB** | Application | Partitioned record store with LRU eviction and analytics queries |

---

## ✨ Features

### Part 1: Linear Probing HashMap

- **Open addressing** with linear probing for collision resolution
- **Soft deletion** using a removed flag (DEL marker), so probe chains stay intact after removals
- **Dynamic resizing** to capacity `2n + 1` when the load factor would be exceeded, with a full **re-hash** of live entries (deleted entries are dropped)
- **Duplicate-key handling:** replaces the value and returns the old one
- **O(1) auxiliary-space iterator** that walks the backing array left to right, skipping empty slots and deleted entries

### Part 2: AVL Tree & TreeMap

- **Self-balancing AVL** with height/balance-factor updates, left and right rotations, and rebalancing
- **TreeMap** built on the AVL logic, storing key-value pairs in sorted key order
- **Predecessor-based removal** for the two-child case
- **`getRange(lower, upper)`** returns values for keys in an inclusive range in **O(log n + k)** time with O(1) extra space, and works even when the bounds aren't keys in the map
- **Iterative in-order iterator** using an explicit stack (O(log n) auxiliary space), since recursion can't persist across `next()` calls
- Full Map API: `put`, `get`, `remove`, `containsKey`, `keySet`, `values`, `size`, `height`, `clear`

### Part 3: DaleDB 🗄️

A small database that partitions data by **pond name** (a hash-map lookup) and orders each pond's records by **timestamp** (a tree-map sort key). It stores two record types: `BoundaryEvent` (animals entering or exiting, including cows) and `FishReport` (fish weights caught by a duck).

| Method | What it does | Target complexity |
|--------|--------------|-------------------|
| `putRecord` | Inserts or replaces a record | O(log r) |
| `deleteRecord` | Removes a record; drops the pond when it becomes empty | O(log r) |
| `getRecord` | Looks up one record by pond and timestamp | O(log r) |
| `getPond` | Returns all records in a pond, sorted by timestamp | O(r) |
| `getPondCount` | Number of ponds in the database | O(1) |
| `getRecordRange` | Records within an inclusive timestamp range | O(r) time, O(log r) space |
| `evict` | Removes the k least-recently-accessed ponds and returns their records | O(1) to choose ponds; O(kr) overall |
| `getPeakConcurrentOccupancy` | Maximum number of ducks present at once (cows excluded) | O(r) time, O(log r) space |
| `getMostFrequentVisitor` | Most frequently listed animal in a pond | O(kr) |
| `mergeReports` | Consolidates each duck's fish reports into its latest record and returns a map of merged timestamps | O(r + k log r) |

*(r = records in a pond, k = size of the relevant group, as defined per method.)*

---

## 🧠 Highlights

- **Two Map implementations, one ADT.** The same interface is backed by hashing (fast average-case lookup) and by a balanced tree (ordered iteration and range queries), which makes the trade-offs concrete.
- **Composed data structures.** DaleDB nests a map of maps: an outer lookup by pond, an inner ordered structure by timestamp. This is the same partition key plus sort key pattern used by NoSQL stores like DynamoDB.
- **O(1) eviction decisions.** Tracking pond access order efficiently means the least-recently-used pond is always known without scanning.
- **Strict space bounds.** Iterators and queries are written to hit auxiliary-space limits (O(1) or O(log n)), not just time bounds.
- **Efficiency verified by comparison counting.** The test suite checks how many comparisons the tree operations make, so the implementation has to be efficient in practice, not just correct.

---

## 🛠️ Tech Stack

- **Language:** Java
- **Core structures:** Linear probing hash table, AVL tree, stacks, linked/array lists
- **Testing:** JUnit
- **Style:** Checkstyle-compliant

---

## 📂 Project Structure

```
.
├── src/        # HashMap, AVL, TreeMap, DaleDB, and supporting classes
├── tst/        # JUnit test files
└── README.md
```

---

## 🚀 Getting Started

**Prerequisites:** JDK 17+ (or the version your IDE is configured for) and IntelliJ IDEA (recommended).

```bash
# Clone the repository
git clone https://github.com/<your-username>/<repo-name>.git
cd <repo-name>
```

1. Open the project root in IntelliJ.
2. Mark `src/` as the **Sources Root** and `tst/` as the **Test Sources Root**.
3. Run the JUnit tests in `tst/` to verify everything works.

---

## 🔍 Examples

**TreeMap range query.** Given a map holding keys 1 through 8, `getRange(2, 6)` returns the values for keys 2, 3, 4, 5, and 6 in key order, even when the bounds themselves aren't present in the map.

**Pond eviction.** After writing to ponds A, B, and C and then reading A, the access order is A, C, B. Calling `evict(1)` removes pond B, the least recently accessed, and returns its records.

**Merging reports.** If a duck has fish reports at timestamps 5 and 6, `mergeReports` keeps only the record at 6 with both weight lists combined, and reports `{6: [5]}`.

---

## 📚 What I Learned

- How hashing, probing, and resizing interact, and why soft deletes are necessary in open addressing
- How AVL rotations keep a tree balanced and what that buys you for ordered operations
- Designing iterators under strict space limits without relying on recursion
- Combining simple data structures into a layered system that meets multiple complexity targets at once

---

## 🎓 Academic Note

This project was completed for **CS 1332: Data Structures & Algorithms** at the Georgia Institute of Technology.

---

## 👤 Author

**Shrey**
Computer Engineering @ Georgia Tech

[LinkedIn](https://www.linkedin.com/in/shrey-patel25) · [GitHub](https://github.com/shreyp3)
