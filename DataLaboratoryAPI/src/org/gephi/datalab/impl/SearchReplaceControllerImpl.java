/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.datalab.impl;

import java.util.Set;
import java.util.regex.Matcher;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.datalab.api.AttributeColumnsController;
import org.gephi.datalab.api.GraphElementsController;
import org.gephi.datalab.api.SearchReplaceController;
import org.gephi.datalab.api.SearchReplaceController.SearchResult;
import org.gephi.graph.api.Attributes;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Implementation of the SearchReplaceController interface
 * declared in the Data Laboratory API.
 * @see SearchReplaceController
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
@ServiceProvider(service = SearchReplaceController.class)
public class SearchReplaceControllerImpl implements SearchReplaceController {

    public SearchResult findNext(SearchOptions searchOptions) {
        int row = 0;
        int column = 0;
        if (searchOptions.getStartingRow() != null) {
            row = searchOptions.getStartingRow();
        }
        if (searchOptions.getStartingColumn() != null) {
            column = searchOptions.getStartingColumn();
        }
        SearchResult result = null;
        if (searchOptions.isSearchNodes()) {
            result = findOnNodes(searchOptions, row, column);
            if (result == null && searchOptions.isLoopToBeginning()) {
                searchOptions.resetStatus();
                return findOnNodes(searchOptions, 0, 0);//If the end of data is reached with no success, try to search again from the beginning as a loop
            } else {
                return result;
            }
        } else {
            result = findOnEdges(searchOptions, row, column);
            if (result == null && searchOptions.isLoopToBeginning()) {
                searchOptions.resetStatus();
                return findOnEdges(searchOptions, 0, 0);//If the end of data is reached with no success, try to search again from the beginning as a loop
            } else {
                return result;
            }
        }
    }

    public SearchResult findNext(SearchResult result) {
        return findNext(result.getSearchOptions());
    }

    public boolean canReplace(SearchResult result) {
        AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
        AttributeTable table;
        AttributeColumn column;
        if (result.getFoundNode() != null) {
            table = ac.getModel().getNodeTable();
            column = table.getColumn(result.getFoundColumnIndex());
        } else {
            table = ac.getModel().getEdgeTable();
            column = table.getColumn(result.getFoundColumnIndex());
        }
        return Lookup.getDefault().lookup(AttributeColumnsController.class).canChangeColumnData(column);
    }

    public SearchResult replace(SearchResult result, String replacement) {
        if (result == null) {
            throw new IllegalArgumentException();
        }
        if (!canReplace(result)) {
            //Data has changed and the replacement can't be done, continue finding.
            return findNext(result);//Go to next search result
        }
        AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
        Object value;
        String str;
        Attributes attributes;
        AttributeColumn column;

        if (!result.getSearchOptions().isUseRegexReplaceMode()) {
            replacement = Matcher.quoteReplacement(replacement);//Avoid using groups and other regex aspects in the replacement
        }

        try {
            //Get value to re-match and replace:
            if (result.getFoundNode() != null) {
                attributes = result.getFoundNode().getNodeData().getAttributes();
                column = ac.getModel().getNodeTable().getColumn(result.getFoundColumnIndex());
            } else {
                attributes = result.getFoundEdge().getEdgeData().getAttributes();
                column = ac.getModel().getEdgeTable().getColumn(result.getFoundColumnIndex());
            }
            value = attributes.getValue(result.getFoundColumnIndex());
            str = value != null ? value.toString() : "";
            StringBuffer sb = new StringBuffer();

            //Match and replace the result:
            Matcher matcher = result.getSearchOptions().getRegexPattern().matcher(str.substring(result.getStart()));
            if (matcher.find()) {
                matcher.appendReplacement(sb, replacement);
                int replaceLong = sb.length();
                matcher.appendTail(sb);
                str = str.substring(0, result.getStart()) + sb.toString();

                result.getSearchOptions().setRegionStart(result.getStart() + replaceLong);
                Lookup.getDefault().lookup(AttributeColumnsController.class).setAttributeValue(str, attributes, column);
                return findNext(result);//Go to next search result
            } else {
                //Data has changed and the replacement can't be done, continue finding.
                return findNext(result);//Go to next search result
            }
        } catch (Exception ex) {
            if (ex instanceof IndexOutOfBoundsException) {
                throw new IndexOutOfBoundsException();//Rethrow the exception when it is caused by a bad regex replacement
            }
            //Data has changed (a lot of different errors can happen) and the replacement can't be done, continue finding.
            return findNext(result);//Go to next search result
        }
    }

    public int replaceAll(SearchOptions searchOptions, String replacement) {
        int replacementsCount = 0;
        searchOptions.resetStatus();
        searchOptions.setLoopToBeginning(false);//To avoid infinite loop when the replacement parse makes it to match again.
        SearchResult result;
        result = findNext(searchOptions);
        while (result != null) {
            if (canReplace(result)) {
                result = replace(result, replacement);
                replacementsCount++;
            } else {
                result = findNext(searchOptions);
            }
        }
        searchOptions.setLoopToBeginning(true);//Restore loop behaviour
        return replacementsCount;
    }

    private SearchResult findOnNodes(SearchOptions searchOptions, int rowIndex, int columnIndex) {
        GraphElementsController gec = Lookup.getDefault().lookup(GraphElementsController.class);
        SearchResult result = null;
        Set<Integer> columnsToSearch = searchOptions.getColumnsToSearch();
        boolean searchAllColumns = columnsToSearch.isEmpty();
        Node[] nodes = searchOptions.getNodesToSearch();
        AttributeRow row;
        Object value;
        for (; rowIndex < nodes.length; rowIndex++) {
            if (!gec.isNodeInGraph(nodes[rowIndex])) {
                continue;//Make sure node is still in graph when continuing a search
            }
            row = (AttributeRow) nodes[rowIndex].getNodeData().getAttributes();
            for (; columnIndex < row.countValues(); columnIndex++) {
                if (searchAllColumns || columnsToSearch.contains(columnIndex)) {
                    value = row.getValue(columnIndex);
                    result = matchRegex(value, searchOptions, rowIndex, columnIndex);
                    if (result != null) {
                        result.setFoundNode(nodes[rowIndex]);
                        return result;
                    }
                }
                searchOptions.setRegionStart(0);//Start at the beginning for the next value
            }
            searchOptions.setRegionStart(0);//Start at the beginning for the next value
            columnIndex = 0;//Start at the first column for the next row
        }
        return result;
    }

    private SearchResult findOnEdges(SearchOptions searchOptions, int rowIndex, int columnIndex) {
        GraphElementsController gec = Lookup.getDefault().lookup(GraphElementsController.class);
        SearchResult result = null;
        Set<Integer> columnsToSearch = searchOptions.getColumnsToSearch();
        boolean searchAllColumns = columnsToSearch.isEmpty();
        Edge[] edges = searchOptions.getEdgesToSearch();
        AttributeRow row;
        Object value;
        for (; rowIndex < edges.length; rowIndex++) {
            if (!gec.isEdgeInGraph(edges[rowIndex])) {
                continue;//Make sure edge is still in graph when continuing a search
            }
            row = (AttributeRow) edges[rowIndex].getEdgeData().getAttributes();
            for (; columnIndex < row.countValues(); columnIndex++) {
                if (searchAllColumns || columnsToSearch.contains(columnIndex)) {
                    value = row.getValue(columnIndex);
                    result = matchRegex(value, searchOptions, rowIndex, columnIndex);
                    if (result != null) {
                        result.setFoundEdge(edges[rowIndex]);
                        return result;
                    }
                }
                searchOptions.setRegionStart(0);//Start at the beginning for the next value
            }
            searchOptions.setRegionStart(0);//Start at the beginning for the next value
            columnIndex = 0;//Start at the first column for the next row
        }
        return result;
    }

    private SearchResult matchRegex(Object value, SearchOptions searchOptions, int rowIndex, int columnIndex) {
        boolean found;
        String str = value != null ? value.toString() : "";
        Matcher matcher = searchOptions.getRegexPattern().matcher(str);
        if (str.isEmpty()) {
            if (searchOptions.getRegionStart() > 0) {
                return null;
            }
        } else if (searchOptions.getRegionStart() >= str.length()) {
            return null;//No more to search in this value, go to next
        }

        if (searchOptions.isOnlyMatchWholeAttributeValue()) {
            found = matcher.matches();//Try to match the whole value
        } else {
            matcher.region(searchOptions.getRegionStart(), str.length());//Try to match a group in the remaining part of the value
            found = matcher.find();
        }

        if (found) {
            searchOptions.setStartingRow(rowIndex);//For next search
            searchOptions.setStartingColumn(columnIndex);//For next search
            int end = matcher.end();
            if (matcher.start() == end && !str.isEmpty()) {
                return null;//Do not match empty string in not empty values
            }
            if (str.isEmpty()) {
                end++;//To be able to search on next values when the value matched is empty
            }
            searchOptions.setRegionStart(end);//Start next search after this match in this value. (If it is greater than the length of the value, it will be discarded at the beginning of this method next time)
            return new SearchResult(searchOptions, null, null, rowIndex, columnIndex, matcher.start(), matcher.end());//Set node or edge values later
        } else {
            return null;
        }
    }
}
