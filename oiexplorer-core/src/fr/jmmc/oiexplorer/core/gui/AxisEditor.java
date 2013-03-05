/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oiexplorer.core.gui;

import fr.jmmc.jmcs.gui.component.GenericListModel;
import fr.jmmc.oiexplorer.core.model.plot.Axis;
import fr.jmmc.oiexplorer.core.model.plot.Range;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SpinnerNumberModel;

/**
 * Axis editor widget.
 * 
 * @author mella
 */
public class AxisEditor extends javax.swing.JPanel {

    /* members */
    /** PlotDefinitionEditor to notify in case of modification */
    private final PlotDefinitionEditor parentToNotify;
    /** Edited axis reference */
    private Axis axisToEdit;
    /** List of available choices */
    private List<String> choices = new LinkedList<String>();
    /** Flag notification of associated plotDefinitionEditor */
    private boolean notify = true;

    /** 
     * Creates the new AxisEditor form.
     * Use setAxis() to change model to edit.
     * @param parent PlotDefinitionEditor to be notified of changes.
     */
    public AxisEditor(PlotDefinitionEditor parent) {
        initComponents();
        parentToNotify = parent;
        GenericListModel<String> listModel = new GenericListModel<String>(choices, true);
        nameComboBox.setModel(listModel);

    }

    /** 
     * Creates new form AxisEditor.
     * This empty constructor leave here for Netbeans GUI builder
     */
    public AxisEditor() {
        initComponents();
        parentToNotify = null;
    }

    /** Helper that return the value of given Boolean an false it the given value is null
     * @param b Boolean object to read
     * @return value of given object or false if given object is null
     * */
    private boolean getData(Boolean b) {
        if (b == null) {
            return false;
        } else {
            return b.booleanValue();
        }
    }

    /** Initialize widgets according to given axis 
     * 
     * @param axis used to initialize widget states
     */
    public void setAxis(Axis axis, List<String> axisChoices) {
        axisToEdit = axis;
        if (axis == null) {
            // TODO push in a reset state
            return;
        }
        try {
            notify = false;

            choices.clear();
            choices.addAll(axisChoices);
            nameComboBox.setSelectedItem(axis.getName());

            includeZeroCheckBox.setSelected(axis.isIncludeZero());
            logScaleCheckBox.setSelected(axis.isLogScale());
            plotErrorCheckBox.setSelected(getData(axis.isPlotError()));

            Range range = axis.getRange();
            if (range == null) {
                //no range
                rangeCheckBox.setSelected(false);
            } else {
                rangeCheckBox.setSelected(true);
                minSpinner.setValue(range.getMin());
                maxSpinner.setValue(range.getMax());
            }
        } finally {
            notify = true;
        }
    }

    /** 
     * Return the edited Axis.
     * @return the edited Axis.
     */
    public Axis getAxis() {
        return axisToEdit;
    }

    /** 
     * Get min range from spinner
     * @return the minimum value of spinner
     */
    private double getMinRange() {
        return ((SpinnerNumberModel) minSpinner.getModel()).getNumber().doubleValue();
    }

    /** 
     * Get max range from spinner
     * @return the maximum value of spinner
     */
    private double getMaxRange() {
        return ((SpinnerNumberModel) maxSpinner.getModel()).getNumber().doubleValue();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        nameComboBox = new javax.swing.JComboBox();
        plotErrorCheckBox = new javax.swing.JCheckBox();
        logScaleCheckBox = new javax.swing.JCheckBox();
        includeZeroCheckBox = new javax.swing.JCheckBox();
        rangeCheckBox = new javax.swing.JCheckBox();
        minSpinner = new javax.swing.JSpinner();
        maxSpinner = new javax.swing.JSpinner();

        setLayout(new java.awt.GridBagLayout());

        nameComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AxisEditor.this.actionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(nameComboBox, gridBagConstraints);

        plotErrorCheckBox.setText("error");
        plotErrorCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AxisEditor.this.actionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        add(plotErrorCheckBox, gridBagConstraints);

        logScaleCheckBox.setText("log");
        logScaleCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AxisEditor.this.actionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(logScaleCheckBox, gridBagConstraints);

        includeZeroCheckBox.setText("include 0");
        includeZeroCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AxisEditor.this.actionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        add(includeZeroCheckBox, gridBagConstraints);

        rangeCheckBox.setText("min/max");
        rangeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AxisEditor.this.actionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        add(rangeCheckBox, gridBagConstraints);

        minSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), null, null, Double.valueOf(1.0d)));
        minSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AxisEditor.this.stateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        add(minSpinner, gridBagConstraints);

        maxSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), null, null, Double.valueOf(1.0d)));
        maxSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AxisEditor.this.stateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        add(maxSpinner, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionPerformed

        if (evt.getSource() == includeZeroCheckBox) {
            axisToEdit.setIncludeZero(includeZeroCheckBox.isSelected());
        } else if (evt.getSource() == logScaleCheckBox) {
            axisToEdit.setLogScale(logScaleCheckBox.isSelected());
        } else if (evt.getSource() == plotErrorCheckBox) {
            axisToEdit.setPlotError(plotErrorCheckBox.isSelected());
        } else if (evt.getSource() == nameComboBox) {
            axisToEdit.setName((String) nameComboBox.getSelectedItem());
        } else if (evt.getSource() == rangeCheckBox) {
            if (rangeCheckBox.isSelected()) {
                axisToEdit.setRange(null);
            } else {
                final Range r = new Range();
                r.setMin(getMinRange());
                r.setMax(getMaxRange());
                axisToEdit.setRange(r);
            }
        } else {
            throw new IllegalStateException("TODO handling of event from " + evt.getSource());
        }

        if (notify) {
            parentToNotify.updateModel();
        }
    }//GEN-LAST:event_actionPerformed

    private void stateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stateChanged
        // get (or create) axis range
        Range r = axisToEdit.getRange();
        if (r == null) {
            r = new Range();
            axisToEdit.setRange(r);
        }

        // apply change onto the model
        if (evt.getSource() == minSpinner) {
            r.setMin(getMinRange());
        } else if (evt.getSource() == maxSpinner) {
            r.setMax(getMaxRange());
        }

        // notify change
        if (notify) {
            parentToNotify.updateModel();
        }
    }//GEN-LAST:event_stateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox includeZeroCheckBox;
    private javax.swing.JCheckBox logScaleCheckBox;
    private javax.swing.JSpinner maxSpinner;
    private javax.swing.JSpinner minSpinner;
    private javax.swing.JComboBox nameComboBox;
    private javax.swing.JCheckBox plotErrorCheckBox;
    private javax.swing.JCheckBox rangeCheckBox;
    // End of variables declaration//GEN-END:variables
}
