package org.gephi.layout.plugin.circularlayout.abstractcombo;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
/**
 *
 * @author Matt
 */
public abstract class AbstractComboBoxEditor extends PropertyEditorSupport {
    public HashMap ComboValues;

    @Override
    public String[] getTags()
    {
        return (String[]) ComboValues.values().toArray(new String[0]);
    }


    @Override
    public String getAsText() {
        return (String) ComboValues.get(getValue());
    }

    @Override
    public void setAsText(String s) {
        Set<Map.Entry<String, String>> Entries = ComboValues.entrySet();
        for (Map.Entry<String, String>Entry: Entries) {
            if (Entry.getValue() == null ? s == null : Entry.getValue().equals(s)) {
                setValue(Entry.getKey());
            }
        }
    }


};