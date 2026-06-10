package com.sapms.pattern.command;

/**
 * DESIGN PATTERN: Command — Interface
 * Every action in SAPMS that can be executed/undone implements this.
 */
public interface Command {
    void execute();
    void undo();
    String getDescription();
}
