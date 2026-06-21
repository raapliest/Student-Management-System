package com.sapms.util;

/**
 * Utility to convert basic Markdown to HTML for display in Thymeleaf templates.
 * Handles: headers, bold, italic, bullet lists, numbered lists, tables, line breaks.
 */
public class MarkdownUtil {

    public static String toHtml(String markdown) {
        if (markdown == null || markdown.isBlank()) return "";

        String[] lines = markdown.split("\n");
        StringBuilder html = new StringBuilder();
        boolean inUl = false, inOl = false, inTable = false;
        boolean tableHeaderDone = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();

            // ── Table ──
            if (trimmed.startsWith("|")) {
                if (!inTable) {
                    if (inUl) {
                        html.append("</ul>");
                        inUl = false;
                    }
                    if (inOl) {
                        html.append("</ol>");
                        inOl = false;
                    }
                    html.append("<table class='ai-table'>");
                    inTable = true;
                    tableHeaderDone = false;
                }
                // Skip separator row (---|---|---)
                if (trimmed.matches("[|\\-: ]+")) {
                    tableHeaderDone = true;
                    continue;
                }
                String tag = tableHeaderDone ? "td" : "th";
                String[] cells = trimmed.split("\\|");
                html.append("<tr>");
                for (String cell : cells) {
                    if (cell.isBlank()) continue;
                    html.append("<").append(tag).append(">")
                            .append(inlineFormat(cell.trim()))
                            .append("</").append(tag).append(">");
                }
                html.append("</tr>");
                continue;
            } else if (inTable) {
                html.append("</table>");
                inTable = false;
                tableHeaderDone = false;
            }

            // ── Headers ──
            if (trimmed.startsWith("### ")) {
                closeLists(html, inUl, inOl);
                inUl = inOl = false;
                html.append("<h4 class='ai-h4'>").append(inlineFormat(trimmed.substring(4))).append("</h4>");
                continue;
            }
            if (trimmed.startsWith("## ")) {
                closeLists(html, inUl, inOl);
                inUl = inOl = false;
                html.append("<h3 class='ai-h3'>").append(inlineFormat(trimmed.substring(3))).append("</h3>");
                continue;
            }
            if (trimmed.startsWith("# ")) {
                closeLists(html, inUl, inOl);
                inUl = inOl = false;
                html.append("<h2 class='ai-h2'>").append(inlineFormat(trimmed.substring(2))).append("</h2>");
                continue;
            }

            // ── Numbered list ──
            if (trimmed.matches("^\\d+\\.\\s+.*")) {
                if (inUl) {
                    html.append("</ul>");
                    inUl = false;
                }
                if (!inOl) {
                    html.append("<ol class='ai-ol'>");
                    inOl = true;
                }
                String item = trimmed.replaceFirst("^\\d+\\.\\s+", "");
                html.append("<li>").append(inlineFormat(item)).append("</li>");
                continue;
            }

            // ── Bullet list ──
            if (trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("• ")) {
                if (inOl) {
                    html.append("</ol>");
                    inOl = false;
                }
                if (!inUl) {
                    html.append("<ul class='ai-ul'>");
                    inUl = true;
                }
                String item = trimmed.substring(2);
                html.append("<li>").append(inlineFormat(item)).append("</li>");
                continue;
            }

            // ── Indented sub-bullet ──
            if (line.startsWith("   - ") || line.startsWith("    - ") || line.startsWith("      - ")) {
                if (!inUl) {
                    html.append("<ul class='ai-ul'>");
                    inUl = true;
                }
                String item = trimmed.substring(2);
                html.append("<li style='margin-left:16px'>").append(inlineFormat(item)).append("</li>");
                continue;
            }

            // ── Close lists on blank line ──
            if (trimmed.isEmpty()) {
                closeLists(html, inUl, inOl);
                inUl = inOl = false;
                html.append("<div class='ai-spacer'></div>");
                continue;
            }

            // ── Horizontal rule ──
            if (trimmed.equals("---") || trimmed.equals("***")) {
                html.append("<hr class='ai-hr'>");
                continue;
            }

            // ── Normal paragraph ──
            closeLists(html, inUl, inOl);
            inUl = inOl = false;
            html.append("<p class='ai-p'>").append(inlineFormat(trimmed)).append("</p>");
        }

        // Close any open tags
        closeLists(html, inUl, inOl);
        if (inTable) html.append("</table>");

        return sanitize(html.toString());
    }

    private static String sanitize(String html) {
        // Strip script/iframe/embed/object blocks
        html = html.replaceAll("(?is)<script[^>]*>.*?</script>", "");
        html = html.replaceAll("(?is)<(iframe|object|embed|form)[^>]*>.*?</(iframe|object|embed|form)>", "");
        html = html.replaceAll("(?i)<(iframe|object|embed|form)[^>]*/?>", "");
        // Strip event handlers (onclick=, onerror=, etc.) and javascript: URLs
        html = html.replaceAll("(?i)\\s+on\\w+\\s*=\\s*\"[^\"]*\"", "");
        html = html.replaceAll("(?i)\\s+on\\w+\\s*=\\s*'[^']*'", "");
        html = html.replaceAll("(?i)javascript\\s*:", "");
        return html;
    }

    private static void closeLists(StringBuilder html, boolean inUl, boolean inOl) {
        if (inUl) html.append("</ul>");
        if (inOl) html.append("</ol>");
    }

    /**
     * Handle inline: **bold**, *italic*, `code`
     */
    private static String inlineFormat(String text) {
        // Bold+italic ***text***
        text = text.replaceAll("\\*\\*\\*(.+?)\\*\\*\\*", "<strong><em>$1</em></strong>");
        // Bold **text**
        text = text.replaceAll("\\*\\*(.+?)\\*\\*", "<strong>$1</strong>");
        // Italic *text*
        text = text.replaceAll("\\*(.+?)\\*", "<em>$1</em>");
        // Inline code `text`
        text = text.replaceAll("`(.+?)`", "<code class='ai-code'>$1</code>");
        // Bold with __text__
        text = text.replaceAll("__(.+?)__", "<strong>$1</strong>");
        return text;
    }
}
