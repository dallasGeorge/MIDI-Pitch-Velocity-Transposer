package gui;
import code.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.EventObject;

public class MidiFileSelectorAndTable extends JFrame {
    private JTextField midiFilePathField;
    private JTable inputTable;
    private DefaultTableModel tableModel;
    private JButton createMidis = new JButton("Export");
    private FileFilter fileFilter;
    private boolean isRadVelSelected;
    public MidiFileSelectorAndTable() {
        setTitle("MIDI File Selector");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // MIDI File Selection Panel
        JPanel midiFilePanel = new JPanel();

        JButton browseButton = new JButton("Input MIDI");
        JRadioButton radioVel = new JRadioButton("Additive Velocities");

        isRadVelSelected = radioVel.isSelected();
        midiFilePathField = new JTextField(30);
        midiFilePathField.setEditable(false);
        createMidis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(midiFilePathField.getText().endsWith(".mid")||midiFilePathField.getText().endsWith(".midi")){
                int rowCount = tableModel.getRowCount();
                int colCount = tableModel.getColumnCount();
                File file = new File("output.mid");
                file.delete();
                for (int row = 0; row < rowCount; row++) {
                    int pitch = 0;
                    int velocity = 0;
                    for (int col = 1; col < colCount; col++) {

                        String cellData = ""+ tableModel.getValueAt(row, col);
                        try{
                        // Do something with the cellData, such as printing it
                        if (col == 1) {

                            pitch = Integer.valueOf(cellData);

                        } else if (col == 2) {

                            velocity = Integer.valueOf(cellData);

                        }


                        }
                        catch(Exception er){
                            JOptionPane.showMessageDialog(null, "An error occurred!\nInput in table is not correct.", "Error", JOptionPane.ERROR_MESSAGE);



                        }


                    }
                    if(!isRadVelSelected) {
                        if (velocity <= 127 && velocity >= 0) {
                            MidiBackEnd.appendMidiWithPitchVelocity(midiFilePathField.getText(), "output.mid", pitch, velocity, row, isRadVelSelected);
                            JOptionPane.showMessageDialog(null, "Export was successful. File was saved as: output.mid.", "Export successful", JOptionPane.INFORMATION_MESSAGE);
                        } else
                            JOptionPane.showMessageDialog(null, "An error occurred!\nVelocity out of range 0-127.", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                    }
                    else {
                        MidiBackEnd.appendMidiWithPitchVelocity(midiFilePathField.getText(), "output.mid", pitch, velocity, row, isRadVelSelected);
                        JOptionPane.showMessageDialog(null, "Export was successful. File was saved as: output.mid.", "Export successful", JOptionPane.INFORMATION_MESSAGE);

                    }
                    }}
                else{
                    JOptionPane.showMessageDialog(null, "An error occurred!\nPlease select a midi file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

                browseButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setAcceptAllFileFilterUsed(false);
                        fileChooser.setFileFilter(new FileFilter() {

                            public String getDescription() {
                                return "MIDI files (*.mid) or (*.midi)";
                            }

                            public boolean accept(File f) {
                                if (f.isDirectory()) {
                                    return true;
                                } else {
                                    String filename = f.getName().toLowerCase();
                                    return filename.endsWith(".mid") || filename.endsWith(".midi");
                                }
                            }
                        });

                        int returnValue = fileChooser.showOpenDialog(null);
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            midiFilePathField.setText(selectedFile.getAbsolutePath());
                        }
                    }
                });

        midiFilePanel.add(browseButton);
        midiFilePanel.add(midiFilePathField);
        midiFilePanel.add(radioVel);

        // Input Table
        String[] columnNames = {"Index","Pitch Interval", "Velocity",};


        tableModel = new DefaultTableModel(columnNames, 0){
        @Override
        public boolean isCellEditable(int row, int column) {
            // Make the first column non-editable
            return column != 0;
            }
        };
        addRowToTable();

        inputTable = new JTable(tableModel){
            @Override
            public boolean editCellAt(int row, int column, EventObject e)
            {
                boolean result = super.editCellAt(row, column, e);
                final Component editor = getEditorComponent();

                if (editor != null && editor instanceof JTextComponent)
                {   if(((JTextComponent)editor).getText().equals("-"))
                    ((JTextComponent)editor).setText("");
                }

                return result;
            }
        };

        radioVel.addActionListener(new ActionListener() {
            // Anonymous class.

            public void actionPerformed(ActionEvent e)
            {
                isRadVelSelected = radioVel.isSelected();
            }});

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        inputTable.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
        inputTable.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
        inputTable.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
        inputTable.getTableHeader().setReorderingAllowed(false);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.add(midiFilePanel, BorderLayout.NORTH);
        JScrollPane tableScroll = new JScrollPane(inputTable);

        mainPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel bottomButton = new JPanel(new BorderLayout());
        JLabel txt1 = new JLabel("                                                                              ");
        JLabel txt2 = new JLabel("                                                                              ");
        bottomButton.add(txt1,BorderLayout.LINE_START);
        bottomButton.add(createMidis,BorderLayout.CENTER);
        bottomButton.add(txt2,BorderLayout.LINE_END);



        mainPanel.add(bottomButton, BorderLayout.SOUTH);
        add(mainPanel);

        inputTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addRowToTable();
                    e.consume();
                    tableScroll.getVerticalScrollBar().setValue(tableScroll.getVerticalScrollBar().getMaximum());
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE ) {

                    removeSelectedRow();
                    e.consume();
                }
                else{
                    super.keyPressed(e);
                }
            }});
    }
    private void addRowToTable(){
        String index = new String(Integer.toString(tableModel.getRowCount()+1));
        String pitchInp = new String("-");
        String velInp = new String("-");

        String[] str = new String[3];
        str[0] = index;
        str[1] =pitchInp;
        str[2]=velInp;

        tableModel.addRow(str);
    }
    private void removeSelectedRow() {
        int rowCount = tableModel.getRowCount();
        if (rowCount > 0) {
            tableModel.removeRow(rowCount - 1);
        }
    }

}
