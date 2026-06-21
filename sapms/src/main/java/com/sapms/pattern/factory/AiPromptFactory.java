package com.sapms.pattern.factory;

public class AiPromptFactory {

    public enum Role {STUDENT, LECTURER, ADMIN}

    public static AiPrompt create(Role role) {
        return switch (role) {
            case STUDENT -> new StudentAdvisorPrompt();
            case LECTURER -> new LecturerWarningPrompt();
            case ADMIN -> new AdminReportPrompt();
        };
    }

    private static class StudentAdvisorPrompt implements AiPrompt {
        @Override
        public String buildSystemPrompt() {
            return "You are an academic advisor for university students. " +
                    "Analyze the student's grades and attendance, then provide clear, actionable advice " +
                    "to help them improve their academic performance. Be encouraging yet honest. " +
                    "Format your response with clear sections using markdown.";
        }

        @Override
        public String buildUserPrompt(String data) {
            return "Please analyze the following student academic data and provide personalized recommendations:\n\n" + data;
        }
    }

    private static class LecturerWarningPrompt implements AiPrompt {
        @Override
        public String buildSystemPrompt() {
            return "You are an academic analytics assistant for university lecturers. " +
                    "Analyze course grade data to identify at-risk students and suggest targeted interventions. " +
                    "Be specific about which students need attention and what actions to take. " +
                    "Format your response with clear sections using markdown.";
        }

        @Override
        public String buildUserPrompt(String data) {
            return "Please analyze the following course data and identify students at risk of failing, " +
                    "with specific intervention recommendations for each:\n\n" + data;
        }
    }

    private static class AdminReportPrompt implements AiPrompt {
        @Override
        public String buildSystemPrompt() {
            return "You are an academic performance analyst for university administrators. " +
                    "Provide executive-level insights on overall academic performance trends, " +
                    "highlight courses or areas of concern, and suggest institutional improvements. " +
                    "Format your response with clear sections using markdown.";
        }

        @Override
        public String buildUserPrompt(String data) {
            return "Please analyze the following system-wide academic data and provide a comprehensive summary " +
                    "with key insights and recommendations:\n\n" + data;
        }
    }
}
