package com.example.studyplanner.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SprintMissionHelper {

    public static class MissionTypeConfig {
        public final String id;
        public final String displayName;
        public final String emoji;
        public final int defaultMinutes;
        public final String defaultEnergy;
        public final List<String> kickstarts;

        public MissionTypeConfig(String id, String displayName, String emoji, int defaultMinutes, String defaultEnergy, List<String> kickstarts) {
            this.id = id;
            this.displayName = displayName;
            this.emoji = emoji;
            this.defaultMinutes = defaultMinutes;
            this.defaultEnergy = defaultEnergy;
            this.kickstarts = kickstarts;
        }
    }

    public static List<MissionTypeConfig> getMissionTypes() {
        List<MissionTypeConfig> list = new ArrayList<>();

        list.add(new MissionTypeConfig(
                "problem_solving",
                "Problem Solving",
                "🎯",
                45,
                "Deep Grind",
                Arrays.asList(
                        "Read question #1 and copy given values onto scratchpad",
                        "Draw diagram / visualize problem #1",
                        "Solve the easiest example problem first to build momentum",
                        "List formulas needed before calculating"
                )
        ));

        list.add(new MissionTypeConfig(
                "assignment",
                "Assignment / HW",
                "📝",
                30,
                "Balanced Focus",
                Arrays.asList(
                        "Open assignment PDF and list the total questions",
                        "Write out the title and question #1 prompt",
                        "Gather reference lecture slides for question #1",
                        "Draft answers in bullet points first"
                )
        ));

        list.add(new MissionTypeConfig(
                "reading",
                "Theory & Reading",
                "📖",
                25,
                "Low Friction",
                Arrays.asList(
                        "Skim headings and bold terms in section 1",
                        "Read the chapter summary / takeaways first",
                        "Read first 2 pages with a highlighter ready",
                        "Write 3 main questions you want answered from this chapter"
                )
        ));

        list.add(new MissionTypeConfig(
                "revision",
                "Rapid Revision",
                "🔄",
                15,
                "Low Friction",
                Arrays.asList(
                        "Review 5 flashcards or key formulas from memory",
                        "Do 2 practice MCQs without looking at solutions",
                        "Write a 1-page mind map of core concepts",
                        "Scan through highlighted exam traps"
                )
        ));

        list.add(new MissionTypeConfig(
                "coding",
                "Coding & Lab",
                "💻",
                50,
                "Deep Grind",
                Arrays.asList(
                        "Open project and run the build / existing tests",
                        "Write the first function signature and return null/0",
                        "Write 1 unit test test case to verify inputs",
                        "Add a simple print/log statement to verify flow"
                )
        ));

        list.add(new MissionTypeConfig(
                "essay",
                "Writing & Notes",
                "✍️",
                35,
                "Balanced Focus",
                Arrays.asList(
                        "Jot down 3 bullet points for the outline",
                        "Write just the introductory hook sentence",
                        "Find 2 reference sources / citations",
                        "Brainstorm main arguments on rough paper"
                )
        ));

        return list;
    }

    public static List<String> getQuickSubjects() {
        return Arrays.asList(
                "Operating Systems",
                "Data Structures & Algos",
                "DBMS (Database)",
                "Computer Networks",
                "Calculus & Linear Algebra",
                "Machine Learning / AI",
                "Software Engineering",
                "Digital Electronics",
                "Microeconomics",
                "Physics / Chemistry",
                "General Study"
        );
    }

    public static int calculateFocusXP(int durationMinutes, String energyLevel) {
        int baseXP = durationMinutes * 2;
        if ("Deep Grind".equalsIgnoreCase(energyLevel)) {
            baseXP += 25;
        } else if ("Balanced Focus".equalsIgnoreCase(energyLevel)) {
            baseXP += 10;
        } else {
            baseXP += 5;
        }
        return baseXP;
    }
}
