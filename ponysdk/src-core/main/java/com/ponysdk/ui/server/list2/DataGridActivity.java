/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *	Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *	Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
 *  
 *  WebSite:
 *  http://code.google.com/p/pony-sdk/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ponysdk.ui.server.list2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ponysdk.impl.theme.PonySDKTheme;
import com.ponysdk.ui.server.basic.IsPWidget;
import com.ponysdk.ui.server.basic.PSimplePanel;
import com.ponysdk.ui.server.basic.PWidget;

/***
 * A Grid of data that supports paging and columns.
 * 
 * @see DataGridColumnDescriptor
 */
public class DataGridActivity<D> implements HasPData<D>, IsPWidget {

    protected final SimpleListView view;
    private PSelectionModel<D> setSelectionModel;
    protected final List<DataGridColumnDescriptor<D, ?>> columnDescriptors = new ArrayList<DataGridColumnDescriptor<D, ?>>();
    private final Map<Integer, Integer> subListSizeByFather = new HashMap<Integer, Integer>();

    private int colCount = 0;
    protected final List<D> rows = new ArrayList<D>();
    protected int dataCount = 0;

    public DataGridActivity(final SimpleListView listView) {
        this.view = listView;
        this.view.asWidget().addStyleName(PonySDKTheme.COMPLEXLIST);
    }

    public void addDataGridColumnDescriptor(final DataGridColumnDescriptor<D, ?> columnDescriptor) {
        columnDescriptors.add(columnDescriptor);
        view.addWidget(columnDescriptor.getHeaderCellRenderer().render(), colCount++, 0, 1);
        addFillColumn();
    }

    protected void addFillColumn() {
        final PSimplePanel widget = new PSimplePanel();
        view.removeCellStyle(0, colCount - 1, PonySDKTheme.FILL_COLUMN);
        view.addWidget(widget, colCount, 0, 1);
        view.addCellStyle(0, colCount, PonySDKTheme.FILL_COLUMN);
        view.addHeaderStyle(PonySDKTheme.COMPLEXLIST_COLUMNHEADER_COMPLEX);
    }

    public void setData(final int row, final D data) {
        int col = 0;

        for (final DataGridColumnDescriptor<D, ?> field : columnDescriptors) {
            final IsPWidget renderCell = field.renderCell(row, data);
            view.addWidget(renderCell, col++, row + 1, 1);
        }
        view.addWidget(new PSimplePanel(), col, row + 1, 1);
        view.addRowStyle(row + 1, PonySDKTheme.SIMPLELIST_ROW);
    }

    protected void updateRowIndex(final int min) {}

    public void insertRow(final int row, final int column, final int colSpan, final PWidget widget) {
        if (row > getRowCount() + 1) throw new IndexOutOfBoundsException("row (" + row + ") > size (" + getRowCount() + ")");

        rows.add(row, null);

        view.insertRow((row + 1));
        view.addWidget(widget, column, (row + 1), colSpan);
        updateRowIndex(row);
    }

    public void insertWidget(final int row, final int column, final int colSpan, final PWidget widget) {
        view.addWidget(widget, column, row, colSpan);
    }

    public void insertSubList(final int row, final List<D> datas) {
        if (datas.isEmpty()) return;

        final int totalSubCount = computeTotalSubRows(row);
        subListSizeByFather.put(row, datas.size());

        int subRow = row + 2 + totalSubCount;
        for (final D data : datas) {
            rows.add((subRow - 1), data);
            view.insertRow(subRow); // create a new row after
            view.addRowStyle(subRow, PonySDKTheme.SIMPLELIST_SUBROW);
            int col = 0;
            for (final DataGridColumnDescriptor<D, ?> field : columnDescriptors) {
                view.addWidget(field.renderSubCell(subRow, data), col++, subRow, 1);
            }
            view.addWidget(new PSimplePanel(), col, subRow++, 1);
        }
    }

    public void removeSubList(final int fatherRow) {
        final int totalSubCount = computeTotalSubRows(fatherRow);
        final Integer subListSize = subListSizeByFather.remove(fatherRow);
        if (subListSize != null) {
            for (int i = 1; i <= subListSize; i++) {
                view.removeRow(fatherRow + totalSubCount + 2);
                rows.remove(fatherRow + totalSubCount + 1);
            }
        }
    }

    private int computeTotalSubRows(final int row) {
        int totalSubCount = 0;

        for (final Entry<Integer, Integer> entry : subListSizeByFather.entrySet()) {
            if (entry.getKey() >= row) continue;

            totalSubCount += entry.getValue();
        }
        return totalSubCount;
    }

    public void clear() {
        view.clearList();
    }

    public void selectRow(final int row) {
        view.selectRow(getRowIndex(row));
    }

    public void unSelectRow(final int row) {
        view.unSelectRow(getRowIndex(row));
    }

    @Override
    public PSelectionModel<D> getSelectionModel() {
        return setSelectionModel;
    }

    @Override
    public void setSelectionModel(final PSelectionModel<D> selectionModel) {
        this.setSelectionModel = selectionModel;
    }

    @Override
    public D getVisibleItem(final int indexOnPage) {
        return rows.get(indexOnPage);
    }

    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getVisibleItemCount() {
        return dataCount;
    }

    @Override
    public Iterable<D> getVisibleItems() {

        return new Iterable<D>() {

            private D next;
            private final Iterator<D> rowsIterator = rows.iterator();

            private final Iterator<D> visibleItemIterator = new Iterator<D>() {

                @Override
                public boolean hasNext() {

                    while (rowsIterator.hasNext()) {
                        next = rowsIterator.next();
                        if (next != null) return true;
                    }
                    return false;
                }

                @Override
                public D next() {
                    return next;
                }

                @Override
                public void remove() {
                    throw new IllegalAccessError("Not implemented");
                }
            };

            @Override
            public Iterator<D> iterator() {
                return visibleItemIterator;
            }
        };
    }

    public int getDataIndex(final D data) {
        return rows.indexOf(data);
    }

    @Override
    public void setData(final List<D> data) {
        rows.clear();

        rows.addAll(data);
        dataCount = data.size();

        view.clear(1);
        subListSizeByFather.clear();

        int rowIndex = 0;
        for (final D t : data) {
            setData(rowIndex++, t);
        }
    }

    public void addData(final D data) {
        insertData(getVisibleItemCount(), data);
    }

    public void insertData(final int index, final D data) {
        rows.add(index, data);
        dataCount++;

        view.insertRow(index + 1);
        setData(index, data);
    }

    public void remove(final int index) {
        view.removeRow(index + 1);

        final D removed = rows.remove(index);
        if (removed != null) dataCount--;
    }

    public void remove(final D data) {
        remove(rows.indexOf(data));
    }

    @Override
    public PWidget asWidget() {
        return view.asWidget();
    }

    public List<DataGridColumnDescriptor<D, ?>> getColumnDescriptors() {
        return columnDescriptors;
    }

    public SimpleListView getListView() {
        return view;
    }

    protected int getRowIndex(final int row) {
        return row + computeTotalSubRows(row);
    }
}
