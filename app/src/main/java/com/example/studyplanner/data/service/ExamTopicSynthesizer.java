package com.example.studyplanner.data.service;

import com.example.studyplanner.models.ExamTopic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExamTopicSynthesizer {

    public static class SubjectTemplate {
        public final String displayName;
        public final String emoji;
        public final List<ExamTopic> topics;

        public SubjectTemplate(String displayName, String emoji, List<ExamTopic> topics) {
            this.displayName = displayName;
            this.emoji = emoji;
            this.topics = topics;
        }
    }

    public static List<SubjectTemplate> getQuickTemplates() {
        List<SubjectTemplate> list = new ArrayList<>();
        list.add(new SubjectTemplate("Operating Systems", "💻", getOperatingSystemsTopics()));
        list.add(new SubjectTemplate("Data Structures & Algos", "🌳", getDSATopics()));
        list.add(new SubjectTemplate("DBMS (Database)", "🗄️", getDBMSTopics()));
        list.add(new SubjectTemplate("Computer Networks", "🌐", getNetworksTopics()));
        list.add(new SubjectTemplate("Calculus & Linear Algebra", "📐", getMathTopics()));
        list.add(new SubjectTemplate("Machine Learning / AI", "🧠", getMLTopics()));
        list.add(new SubjectTemplate("Software Engineering", "⚙️", getSoftwareEngineeringTopics()));
        list.add(new SubjectTemplate("Digital Electronics", "⚡", getElectronicsTopics()));
        list.add(new SubjectTemplate("Microeconomics", "📊", getEconomicsTopics()));
        return list;
    }

    public static List<ExamTopic> synthesize(String rawSubject) {
        if (rawSubject == null || rawSubject.trim().isEmpty()) {
            return getOperatingSystemsTopics();
        }

        String query = rawSubject.toLowerCase(Locale.getDefault()).trim();

        if (query.contains("operat") || query.contains("os") || query.contains("system programming")) {
            return getOperatingSystemsTopics();
        } else if (query.contains("data struct") || query.contains("algo") || query.contains("dsa") || query.contains("leetcode")) {
            return getDSATopics();
        } else if (query.contains("dbms") || query.contains("database") || query.contains("sql") || query.contains("relational")) {
            return getDBMSTopics();
        } else if (query.contains("network") || query.contains("cn") || query.contains("tcp") || query.contains("internet")) {
            return getNetworksTopics();
        } else if (query.contains("calculus") || query.contains("math") || query.contains("algebra") || query.contains("matrix")) {
            return getMathTopics();
        } else if (query.contains("machine learning") || query.contains("ai") || query.contains("data science") || query.contains("ml")) {
            return getMLTopics();
        } else if (query.contains("software") || query.contains("oop") || query.contains("design pattern") || query.contains("agile")) {
            return getSoftwareEngineeringTopics();
        } else if (query.contains("electr") || query.contains("digital") || query.contains("circuit") || query.contains("physics")) {
            return getElectronicsTopics();
        } else if (query.contains("econ") || query.contains("finance") || query.contains("business") || query.contains("accounting")) {
            return getEconomicsTopics();
        } else if (query.contains("chem")) {
            return getChemistryTopics(rawSubject);
        } else if (query.contains("bio") || query.contains("anat") || query.contains("med")) {
            return getBiologyTopics(rawSubject);
        } else {
            return generateAdaptiveTopics(rawSubject);
        }
    }

    private static List<ExamTopic> getOperatingSystemsTopics() {
        List<ExamTopic> list = new ArrayList<>();
        list.add(new ExamTopic(
                "Process Synchronization & Critical Sections",
                "⚠️ Exam Trap: Busy-waiting (Spinlocks) wastes CPU cycles; semaphores use sleep/wakeup. Peterson's algorithm fails on modern multi-core CPUs without memory barriers.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Deadlocks & Banker's Algorithm",
                "⚠️ Exam Trap: An 'unsafe state' does NOT mean deadlock has occurred; it only means a deadlock might occur if worst-case requests arrive.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Virtual Memory & Page Replacement",
                "⚠️ Exam Trap: Belady's Anomaly occurs with FIFO page replacement (more frames = more faults), but NEVER with stack algorithms like LRU or Optimal.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "CPU Scheduling (SJF, Round Robin, Priority)",
                "⚠️ Exam Trap: SJF gives the provably optimal average waiting time, but causes starvation of longer processes. Know pre-emptive vs non-pre-emptive formulas.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "File Systems, Inodes & Disk Scheduling",
                "⚠️ Exam Trap: Deleting a file only frees disk blocks when its hard-link reference count reaches zero and no open file handles remain.",
                "⚡ Core Exam Focus"
        ));
        return list;
    }

    private static List<ExamTopic> getDSATopics() {
        List<ExamTopic> list = new ArrayList<>();
        list.add(new ExamTopic(
                "Binary Search Trees & AVL Rotations",
                "⚠️ Exam Trap: Worst-case search in an unbalanced BST is O(N). In-order traversal of any valid BST always yields strictly sorted keys.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Dynamic Programming: 0/1 Knapsack & Subsequences",
                "⚠️ Exam Trap: Fractional Knapsack uses a Greedy strategy, but 0/1 Knapsack MUST use Dynamic Programming (overlapping subproblems + optimal substructure).",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Graph Algorithms: Dijkstra, BFS/DFS & Bellman-Ford",
                "⚠️ Exam Trap: Dijkstra fails with negative edge weights! Bellman-Ford is required to detect and handle negative weight cycles.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Hashing & Collision Resolution (Chaining vs Open Addressing)",
                "⚠️ Exam Trap: In Open Addressing (Linear Probing), primary clustering degrades performance, and deletion requires a tombstone marker so lookups don't abort early.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Sorting & Search Edge Cases (Quicksort vs Mergesort)",
                "⚠️ Exam Trap: Quicksort is O(N^2) in worst-case with naive pivot; Mergesort is guaranteed O(N log N) but requires O(N) auxiliary RAM space.",
                "⚡ Core Exam Focus"
        ));
        return list;
    }

    private static List<ExamTopic> getDBMSTopics() {
        List<ExamTopic> list = new ArrayList<>();
        list.add(new ExamTopic(
                "Normalization & Functional Dependencies (1NF to BCNF)",
                "⚠️ Exam Trap: BCNF is strictly stronger than 3NF, but converting to BCNF can sometimes lose functional dependency preservation.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "ACID Properties & Transaction Isolation Anomalies",
                "⚠️ Exam Trap: READ COMMITTED prevents dirty reads, but allows non-repeatable reads; SERIALIZABLE is required to prevent phantom reads.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Indexing & B+ Trees vs Hash Indexes",
                "⚠️ Exam Trap: B+ Trees store data record pointers ONLY in leaf nodes (chained for fast range queries), unlike B-Trees which store keys and records in internal nodes.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Concurrency Control & 2-Phase Locking (Strict 2PL)",
                "⚠️ Exam Trap: 2PL guarantees conflict serializability, but does NOT prevent cascading aborts unless Strict 2PL is enforced.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "SQL Query Processing: HAVING vs WHERE & Null Handling",
                "⚠️ Exam Trap: WHERE filters rows before aggregation; HAVING filters aggregated groups after. Aggregate functions like COUNT(col) ignore NULL values.",
                "⚡ Core Exam Focus"
        ));
        return list;
    }

    private static List<ExamTopic> getNetworksTopics() {
        List<ExamTopic> list = new ArrayList<>();
        list.add(new ExamTopic(
                "TCP 3-Way Handshake, Flow Control vs Congestion Control",
                "⚠️ Exam Trap: Flow control protects the RECEIVER buffer; Congestion control protects the intermediate NETWORK from overload.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "IP Addressing, Subnetting & CIDR Calculation",
                "⚠️ Exam Trap: In any subnet, the very first address is the Network ID and the last address is the Directed Broadcast ID—neither can be assigned to a host!",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "OSI Layers & Packet Encapsulation",
                "⚠️ Exam Trap: MAC addresses change hop-by-hop at each router interface, while source and destination IP addresses remain constant end-to-end.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Routing Protocols: Distance Vector vs Link State",
                "⚠️ Exam Trap: Distance Vector algorithms suffer from Count-to-Infinity problem; Split Horizon with Poison Reverse mitigates two-node loops.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "DNS & Application Layer Protocols (HTTP/2 vs 3, UDP Port 53)",
                "⚠️ Exam Trap: DNS uses UDP for standard queries under 512 bytes for speed, but falls back to TCP for zone transfers and truncated messages.",
                "⚡ Core Exam Focus"
        ));
        return list;
    }

    private static List<ExamTopic> getMathTopics() {
        List<ExamTopic> list = new ArrayList<>();
        list.add(new ExamTopic(
                "Eigenvalues, Eigenvectors & Diagonalization",
                "⚠️ Exam Trap: A matrix is diagonalizable if and only if geometric multiplicity equals algebraic multiplicity for every eigenvalue.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Multivariable Gradients, Hessian & Saddle Points",
                "⚠️ Exam Trap: If the discriminant D < 0 in the second derivative test, the critical point is a SADDLE POINT (neither local max nor local min).",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Definite Integrals & Integration by Parts (ILATE Rule)",
                "⚠️ Exam Trap: In integration by parts, don't drop the minus sign in ∫u dv = uv - ∫v du, and remember to transform limits when substituting u.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Rank-Nullity Theorem & Linear Independence",
                "⚠️ Exam Trap: Rank + Nullity = Number of columns. If Det(A) = 0, the columns are linearly dependent and A has no inverse.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Taylor Series & Radius of Convergence",
                "⚠️ Exam Trap: When Ratio Test gives L = 1, it is INCONCLUSIVE; the boundary endpoints of the interval of convergence must always be tested separately.",
                "⚡ Core Exam Focus"
        ));
        return list;
    }

    private static List<ExamTopic> getMLTopics() {
        List<ExamTopic> list = new ArrayList<>();
        list.add(new ExamTopic(
                "Bias-Variance Tradeoff & Regularization (L1 Lasso vs L2 Ridge)",
                "⚠️ Exam Trap: L1 (Lasso) drives coefficients exactly to zero (acting as feature selection); L2 (Ridge) only shrinks them toward zero.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Classification Metrics: Precision, Recall, F1 & ROC-AUC",
                "⚠️ Exam Trap: High accuracy on an imbalanced dataset is deceptive! Always look at Precision-Recall or F1-Score for rare anomaly/fraud classes.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Neural Network Backpropagation & Activation Functions",
                "⚠️ Exam Trap: Sigmoid causes vanishing gradient in deep layers; ReLU prevents vanishing gradient for positive inputs but can suffer from dying neurons for negatives.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Supervised Classifiers: SVM Margins, Logistic Reg & Naive Bayes",
                "⚠️ Exam Trap: Naive Bayes assumes ALL features are strictly independent given the target class—rarely true, yet surprisingly effective as a baseline.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "K-Means Clustering & PCA Dimensionality Reduction",
                "⚠️ Exam Trap: K-Means is Euclidean distance-based, so features with large scales dominate unless normalized or standardized first.",
                "⚡ Core Exam Focus"
        ));
        return list;
    }

    private static List<ExamTopic> getSoftwareEngineeringTopics() {
        List<ExamTopic> list = new ArrayList<>();
        list.add(new ExamTopic(
                "SOLID Principles & Common Design Patterns",
                "⚠️ Exam Trap: Open/Closed Principle means open for extension, closed for modification. Strategy Pattern prefers object composition over inheritance.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Testing Methodologies: Unit vs Integration & Cyclomatic Complexity",
                "⚠️ Exam Trap: Cyclomatic complexity V(G) = E - N + 2P gives the exact MINIMUM number of independent test cases required for complete branch coverage.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "OOP Polymorphism: Method Overloading vs Method Overriding",
                "⚠️ Exam Trap: Method overloading is resolved at COMPILE-TIME (static binding); method overriding is resolved at RUNTIME (dynamic dispatch).",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Agile Scrum Workflows: Sprints, Epics, Stories & Velocity",
                "⚠️ Exam Trap: Active sprints must not have scope changes injected mid-sprint; new requirements are groomed into product backlog for future sprints.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Git Branching Strategies: Merge vs Rebase & CI/CD Stages",
                "⚠️ Exam Trap: Rebase rewrites history to create a single linear line, which can cause confusion on shared public branches; Merge preserves true history.",
                "⚡ Core Exam Focus"
        ));
        return list;
    }

    private static List<ExamTopic> getElectronicsTopics() {
        List<ExamTopic> list = new ArrayList<>();
        list.add(new ExamTopic(
                "K-Maps, Boolean Minimization & De Morgan's Laws",
                "⚠️ Exam Trap: Don't miss corner wrap-around groupings on 4-variable K-Maps to achieve minimal Sum of Products (SOP).",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Flip-Flops (JK, D, T) & Race-Around Condition",
                "⚠️ Exam Trap: In JK flip-flop, J=1 and K=1 causes toggle; Master-Slave setup or edge-triggering is required to prevent race-around oscillation.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Op-Amp Ideal Characteristics & Virtual Ground",
                "⚠️ Exam Trap: Virtual ground concept only holds when negative feedback is present and the output has not saturated to supply rails.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Zener Diodes & Voltage Regulators",
                "⚠️ Exam Trap: Zener diodes operate in the REVERSE breakdown region to maintain a fixed stable reference voltage, unlike normal rectifiers.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Synchronous vs Asynchronous (Ripple) Counters",
                "⚠️ Exam Trap: Ripple counters have cumulative propagation delays that cause glitches at high clock frequencies; synchronous counters clock all flip-flops simultaneously.",
                "⚡ Core Exam Focus"
        ));
        return list;
    }

    private static List<ExamTopic> getEconomicsTopics() {
        List<ExamTopic> list = new ArrayList<>();
        list.add(new ExamTopic(
                "Price Elasticity of Demand & Total Revenue Test",
                "⚠️ Exam Trap: When demand is inelastic (|Ed| < 1), raising prices INCREASES total revenue; when demand is elastic (|Ed| > 1), raising prices drops revenue.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Market Structures: Perfect Competition vs Monopoly",
                "⚠️ Exam Trap: In the long run, perfectly competitive firms earn ZERO economic profit (normal profit) because free entry/exit drives Price = min ATC.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Marginal Cost & Average Total Cost Curve Relationships",
                "⚠️ Exam Trap: The Marginal Cost (MC) curve ALWAYS intersects the ATC and AVC curves at their absolute minimum points.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Consumer Optimum & Indifference Curve Tangency",
                "⚠️ Exam Trap: Consumer equilibrium occurs where the Marginal Rate of Substitution (MRS) equals the price ratio (Px / Py).",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Externalities, Public Goods & Market Failure",
                "⚠️ Exam Trap: Public goods are Non-Excludable and Non-Rivalrous; Common Pool Resources are Rivalrous, leading to the Tragedy of the Commons.",
                "⚡ Core Exam Focus"
        ));
        return list;
    }

    private static List<ExamTopic> getChemistryTopics(String name) {
        List<ExamTopic> list = new ArrayList<>();
        list.add(new ExamTopic(
                "Reaction Mechanisms, Intermediate Stability & Rate Laws",
                "⚠️ Exam Trap: The rate-determining step is the slowest elementary step; intermediates do NOT appear in the overall rate law.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Chemical Equilibrium & Le Chatelier's Principle",
                "⚠️ Exam Trap: Catalysts increase the speed of both forward and reverse reactions equally—they NEVER alter the equilibrium constant (Keq)!",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Thermodynamics: ΔG = ΔH - TΔS & Spontaneity",
                "⚠️ Exam Trap: A negative ΔH (exothermic) is not sufficient for spontaneity; temperature and entropy (ΔS) dictate if ΔG < 0.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Molecular Geometry, Hybridization & Dipole Moments",
                "⚠️ Exam Trap: Lone pairs exert greater electrostatic repulsion than bonding pairs, compressing ideal bond angles (e.g. H2O is 104.5°, not 109.5°).",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Acid-Base Equilibria (pH, pKa, Buffer Systems & Titrations)",
                "⚠️ Exam Trap: At the half-equivalence point in a weak acid titration, pH = pKa because [HA] = [A-].",
                "⚡ Core Exam Focus"
        ));
        return list;
    }

    private static List<ExamTopic> getBiologyTopics(String name) {
        List<ExamTopic> list = new ArrayList<>();
        list.add(new ExamTopic(
                "Cellular Respiration vs Photosynthesis (ATP Yield & Electron Transport)",
                "⚠️ Exam Trap: The bulk of ATP is synthesized by ATP Synthase through chemiosmosis / proton gradient, NOT substrate-level phosphorylation.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Central Dogma: DNA Replication, Transcription & Translation",
                "⚠️ Exam Trap: DNA polymerase synthesizes ONLY in the 5' to 3' direction, requiring Okazaki fragments on the lagging strand.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                "Genetics & Mendelian Inheritance (Punnett Squares & Linkage)",
                "⚠️ Exam Trap: Linked genes on the same chromosome violate Mendel's Law of Independent Assortment unless separated by crossing over.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Enzyme Kinetics: Km, Vmax & Competitive vs Non-competitive Inhibition",
                "⚠️ Exam Trap: Competitive inhibitors increase Km (lower apparent affinity) but leave Vmax unchanged; non-competitive decrease Vmax.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                "Homeostasis, Signal Transduction & Feedback Loops",
                "⚠️ Exam Trap: Negative feedback stabilizes systems around a set point; positive feedback amplifies responses (e.g. oxytocin in childbirth, action potentials).",
                "⚡ Core Exam Focus"
        ));
        return list;
    }

    private static List<ExamTopic> generateAdaptiveTopics(String subjectName) {
        String clean = subjectName.trim();
        List<ExamTopic> list = new ArrayList<>();
        list.add(new ExamTopic(
                clean + ": Foundational Principles & Core Definitions",
                "⚠️ Exam Trap: Watch out for precise definitions and boundary conditions that professors test to distinguish rote memorizers from true conceptual masters.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                clean + ": Core Mechanisms, Formulas & Step-by-Step Derivations",
                "⚠️ Exam Trap: Sign errors, incorrect unit conversions, and skipping intermediate justification steps are where 70% of exam points are lost.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                clean + ": Real-World Applications & Scenario Dilemmas",
                "⚠️ Exam Trap: Look for scenario-based questions where multiple methods appear valid, but one specific constraint makes only one method correct.",
                "🔥 High-Yield 80/20"
        ));
        list.add(new ExamTopic(
                clean + ": Common Edge Cases & Classic Professor Trick Questions",
                "⚠️ Exam Trap: Edge cases that occur at extremes (zero, infinity, null states, boundary limits) are favorite target zones for exam writers.",
                "⚡ Core Exam Focus"
        ));
        list.add(new ExamTopic(
                clean + ": Synthesis, Comparison & Comprehensive Review",
                "⚠️ Exam Trap: Compare-and-contrast questions (e.g., Method A vs Method B tradeoffs) are often weighted highest on final exam papers.",
                "⚡ Core Exam Focus"
        ));
        return list;
    }
}
