package com.github.clagomess.pirilampo.gui.component;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class RadioButtonGroupComponent<T extends Enum<T>> extends JPanel {
    private final List<OnChangeFI<T>> onChangeList = new LinkedList<>();
    private final List<RadioButton<T>> elements = new LinkedList<>();
    private final ButtonGroup buttonGroup = new ButtonGroup();

    public RadioButtonGroupComponent(List<RadioButton<T>> buttons){
        setLayout(new MigLayout("insets 0 0 0 0", "[grow,fill]"));
        buttons.forEach(item -> {
            elements.add(item);
            buttonGroup.add(item);
            add(item);
            item.addActionListener(l -> onChangeList.forEach(ch -> ch.change(item.value)));
        });
    }

    public void addOnChange(OnChangeFI<T> value){
        onChangeList.add(value);
    }

    public T getSelectedValue(){
        Optional<RadioButton<T>> opt = elements.stream()
                .filter(AbstractButton::isSelected)
                .findFirst();

        return opt.map(RadioButton::getValue).orElse(null);
    }

    public void setSelected(T selected){
        if(selected == null) return;

        Optional<RadioButton<T>> opt = elements.stream()
                .filter(item -> item.value.equals(selected))
                .findFirst();

        opt.ifPresent(tRadioButton -> tRadioButton.setSelected(true));
    }

    public void setEnabled(boolean enabled){
        elements.forEach(item -> item.setEnabled(enabled));
    }

    @Getter
    public static class RadioButton<T extends Enum<T>> extends JRadioButton {
        private final T value;

        public RadioButton(T value){
            this(value.name(), value);
        }

        public RadioButton(T value, boolean selected){
            this(value.name(), value, selected);
        }

        public RadioButton(String label, T value){
            this(label, value, false);
        }

        public RadioButton(String label, T value, boolean selected){
            setText(label);
            setSelected(selected);
            this.value = value;
        }
    }

    @FunctionalInterface
    public interface OnChangeFI<T extends Enum<T>> {
        void change(T value);
    }
}
