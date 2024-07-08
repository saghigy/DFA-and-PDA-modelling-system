package main.view.settings;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.Component;



public class LanguageItemRenderer extends BasicComboBoxRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected,
          cellHasFocus);
      if (value != null) {
        LanguageItem item = (LanguageItem) value;
        setText(item.getName());
      }
      if (index == -1) {
        LanguageItem item = (LanguageItem) value;
        setText( item.getName());
      }
      return this;
    }
  }