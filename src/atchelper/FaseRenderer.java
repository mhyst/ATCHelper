/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atchelper;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 *
 * @author mhyst
 */
public class FaseRenderer extends BasicComboBoxRenderer {
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus)
        {
            super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);

            if (value != null)
            {
                Fase item = (Fase)value;
                setText( item.getNombre() );
            }

            if (index == -1)
            {
                Fase item = (Fase)value;
                setText( "" + item.getId() );
            }


            return this;
        }
}
