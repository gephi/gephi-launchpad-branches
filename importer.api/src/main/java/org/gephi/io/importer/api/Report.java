/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.io.importer.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.gephi.io.importer.api.Issue.Level;

/**
 * Report is a log and issue container. Filled with information, details, minor or major issues, it is stored in an issue list
 * and can be retrieved to present issues to end-users. Behavior is the same as a simple logging library.
 *
 * @author Mathieu Bastian
 */
public final class Report {

    private final Queue<ReportEntry> entries = new ConcurrentLinkedQueue<ReportEntry>();
    private Issue.Level exceptionLevel = Issue.Level.CRITICAL;

    /**
     * Log an information message in the report.
     * @param message the message to write in the report
     * @throws NullPointerException if <code>message</code> is <code>null</code>
     */
    public void log(String message) {
        entries.add(new ReportEntry(message));
    }

    public void append(Report report) {
        entries.addAll(report.entries);
    }

    /**
     * Log an issue in the report.
     * @param issue the issue to write in the report
     * @throws NullPointerException if <code>issue</code> is <code>null</code>
     */
    public void logIssue(Issue issue) {
        entries.add(new ReportEntry(issue));
        if (issue.getLevel().toInteger() >= exceptionLevel.toInteger()) {
            if (issue.getThrowable() != null) {
                throw new RuntimeException(issue.getMessage(), issue.getThrowable());
            } else {
                throw new RuntimeException(issue.getMessage());
            }
        }
    }

    /**
     * Returns all issues written in the report.
     * @return a collection of all issues written in the report
     */
    public List<Issue> getIssues() {
        List<Issue> res = new ArrayList<Issue>();
        for (ReportEntry re : entries) {
            if (re.issue != null) {
                res.add(re.issue);
            }
        }
        return res;
    }

    /**
     * Returns the report logs and issues, presented as <b>HTML</b> code.
     * @return a string of HTML code where all messages and issues are written
     */
    public String getHtml() {
        StringBuilder builder = new StringBuilder();
        for (ReportEntry re : entries) {
            if (re.issue != null) {
                builder.append(re.issue.getMessage());
                builder.append("<br>");
            } else {
                builder.append(re.message);
                builder.append("<br>");
            }
        }
        return builder.toString();
    }

    /**
     * Returns the report logs and issues, presented as basic multi-line text.
     * @return a string of all messages and issues written in the report, one per line
     */
    public String getText() {
        StringBuilder builder = new StringBuilder();
        for (ReportEntry re : entries) {
            if (re.issue != null) {
                builder.append(re.issue.getMessage());
                builder.append("\n");
            } else {
                builder.append(re.message);
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    /**
     * Get the current exception level for the report. Default is <code>Level.CRITICAL</code>.
     * @return the current exception level
     */
    public Level getExceptionLevel() {
        return exceptionLevel;
    }

    /**
     * Set the level of exception for the report. If a reported issue has his level greater or equal
     * as <code>exceptionLevel</code>, an exception is thrown. Default is <code>Level.CRITICAL</code>
     * @param exceptionLevel the exception level where exceptions are to be thrown
     */
    public void setExceptionLevel(Level exceptionLevel) {
        this.exceptionLevel = exceptionLevel;
    }

    private class ReportEntry {

        private final Issue issue;
        private final String message;

        public ReportEntry(Issue issue) {
            this.issue = issue;
            this.message = null;
        }

        public ReportEntry(String message) {
            this.message = message;
            this.issue = null;
        }
    }

    public void pruneReport(int limit) {
        if (entries.size() > limit) {
            int step = 0;
            while (entries.size() > limit && step < 3) {
                if (step == 0) {
                    ReportEntry lastIssue = null;
                    for (Iterator<ReportEntry> itr = entries.iterator(); itr.hasNext();) {
                        ReportEntry issue = itr.next();
                        if (issue.issue != null && issue.issue.getLevel().equals(Issue.Level.INFO)) {
                            lastIssue = issue;
                            itr.remove();
                        }
                    }
                    if (lastIssue != null) {
                        entries.add(lastIssue);
                        entries.add(new ReportEntry(new Issue("More issues not listed...", Issue.Level.INFO)));
                    }
                    step = 1;
                } else if (step == 1) {
                    ReportEntry lastIssue = null;
                    for (Iterator<ReportEntry> itr = entries.iterator(); itr.hasNext();) {
                        ReportEntry issue = itr.next();
                        if (issue.issue != null && issue.issue.getLevel().equals(Issue.Level.WARNING)) {
                            lastIssue = issue;
                            itr.remove();
                        }
                    }
                    if (lastIssue != null) {
                        entries.add(lastIssue);
                        entries.add(new ReportEntry(new Issue("More issues not listed...", Issue.Level.WARNING)));
                    }
                    step = 2;
                } else if (step == 2) {
                    ReportEntry lastIssue = null;
                    for (Iterator<ReportEntry> itr = entries.iterator(); itr.hasNext();) {
                        ReportEntry issue = itr.next();
                        if (issue.issue != null && issue.issue.getLevel().equals(Issue.Level.INFO)) {
                            lastIssue = issue;
                            itr.remove();
                        }
                    }
                    if (lastIssue != null) {
                        entries.add(lastIssue);
                        entries.add(new ReportEntry(new Issue("More issues not listed...", Issue.Level.INFO)));
                    }
                    step = 3;
                } else if (step == 3) {
                    ReportEntry lastIssue = null;
                    for (Iterator<ReportEntry> itr = entries.iterator(); itr.hasNext();) {
                        ReportEntry issue = itr.next();
                        if (issue.issue == null) {
                            lastIssue = issue;
                            itr.remove();
                        }
                    }
                    if (lastIssue != null) {
                        entries.add(lastIssue);
                        entries.add(new ReportEntry("More messages not listed..."));
                    }
                    step = 4;
                }
            }
        }
    }
}
