package com.sapms.pattern.command;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * DESIGN PATTERN: Command — Invoker
 * <p>
 * Executes commands and maintains a history stack for undo support.
 * Decouples the "who triggers" from the "what happens".
 * <p>
 * Usage:
 * invoker.execute(new SaveGradeCommand(...));
 * invoker.undo(); // undoes last command
 */
@Component
public class CommandInvoker {

    private final Deque<Command> history = new ArrayDeque<>();
    private final List<String> auditTrail = new ArrayList<>();
    private static final int MAX_HISTORY = 50;

    /**
     * Execute a command and push to history stack
     */
    public void execute(Command command) {
        command.execute();

        if (history.size() >= MAX_HISTORY) history.pollFirst();
        history.push(command);
        auditTrail.add("[EXEC] " + command.getDescription());
    }

    /**
     * Undo the most recent command
     */
    public boolean undo() {
        if (history.isEmpty()) return false;
        Command last = history.pop();
        last.undo();
        auditTrail.add("[UNDO] " + last.getDescription());
        return true;
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public List<String> getAuditTrail() {
        return new ArrayList<>(auditTrail);
    }

    public int getHistorySize() {
        return history.size();
    }
}
